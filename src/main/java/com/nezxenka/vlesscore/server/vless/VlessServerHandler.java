package com.nezxenka.vlesscore.server.vless;

import com.nezxenka.vlesscore.config.AppConfig;
import com.nezxenka.vlesscore.core.protocol.VlessConnection;
import com.nezxenka.vlesscore.core.protocol.VlessConstants;
import com.nezxenka.vlesscore.core.session.VlessSession;
import com.nezxenka.vlesscore.core.session.SessionState;
import com.nezxenka.vlesscore.core.protocol.VlessHeaderDecoder;
import com.nezxenka.vlesscore.core.protocol.VlessResponseEncoder;
import com.nezxenka.vlesscore.database.dao.TokenDao;
import com.nezxenka.vlesscore.service.VlessAuthService;
import com.nezxenka.vlesscore.service.TrafficService;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicBoolean;

public class VlessServerHandler extends ChannelInboundHandlerAdapter {

    private static final Logger log = LoggerFactory.getLogger(VlessServerHandler.class);

    public static volatile boolean silentMode = false;

    private final AppConfig config;
    private final TokenDao tokenDao;
    private final VlessAuthService authService;
    private final TrafficService trafficService;

    private Channel outboundChannel;
    private final AtomicBoolean headerSent = new AtomicBoolean(false);
    private VlessConnection vlessConn;
    private VlessSession session;
    private boolean authenticated = false;

    public VlessServerHandler(AppConfig config, TokenDao tokenDao,
                               VlessAuthService authService, TrafficService trafficService) {
        this.config = config;
        this.tokenDao = tokenDao;
        this.authService = authService;
        this.trafficService = trafficService;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf buf = (ByteBuf) msg;

        if (!authenticated) {
            handleInitialRequest(ctx, buf);
        } else {
            if (outboundChannel != null && outboundChannel.isActive()) {
                outboundChannel.writeAndFlush(buf).addListener((ChannelFutureListener) future -> {
                    if (!future.isSuccess()) {
                        closeAll(ctx);
                    }
                });
            } else {
                buf.release();
                closeAll(ctx);
            }
        }
    }

    private void handleInitialRequest(ChannelHandlerContext ctx, ByteBuf buf) {
        VlessConnection conn = VlessHeaderDecoder.decode(buf);
        buf.release();

        if (conn == null) {
            if (!silentMode) log.warn("VLESS decode failed from {}",
                    ctx.channel().remoteAddress());
            ctx.close();
            return;
        }

        this.vlessConn = conn;
        this.session = new VlessSession(conn);

        String uuid = conn.getUuidString();

        boolean valid = authService.authenticate(uuid);
        if (!valid) {
            if (!silentMode) log.warn("Auth failed: UUID={} from {}",
                    uuid, ctx.channel().remoteAddress());
            ctx.close();
            return;
        }

        authService.recordConnection(uuid);
        authenticated = true;

        if (!silentMode) {
            log.info("[CONNECT] {} → {} ({})",
                    ctx.channel().remoteAddress(),
                    conn.getDestination(),
                    conn.isTcp() ? "TCP" : "UDP");
        }

        connectToTarget(ctx, conn);
    }

    private void connectToTarget(ChannelHandlerContext ctx, VlessConnection conn) {
        Channel inboundChannel = ctx.channel();

        Bootstrap b = new Bootstrap();
        b.group(inboundChannel.eventLoop())
                .channel(NioSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.AUTO_READ, false)
                .handler(new ChannelInitializer<>() {
                    @Override
                    protected void initChannel(Channel ch) {
                        ch.pipeline().addLast(new RelayHandler(inboundChannel, silentMode));
                    }
                });

        ChannelFuture connectFuture = b.connect(
                new InetSocketAddress(conn.getAddress(), conn.getPort()));

        outboundChannel = connectFuture.channel();

        connectFuture.addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                if (headerSent.compareAndSet(false, true)) {
                    ByteBuf responseHeader = VlessResponseEncoder.encodeResponseHeader();
                    inboundChannel.writeAndFlush(responseHeader).addListener((ChannelFutureListener) f -> {
                        if (f.isSuccess()) {
                            if (conn.getPayload() != null && conn.getPayload().length > 0) {
                                outboundChannel.writeAndFlush(
                                        Unpooled.wrappedBuffer(conn.getPayload()));
                            }
                            outboundChannel.read();
                        }
                    });
                }
            } else {
                if (!silentMode) {
                    log.warn("Connection failed to {}: {}",
                            conn.getDestination(), future.cause().getMessage());
                }
                inboundChannel.close();
            }
        });
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        if (evt instanceof IdleStateEvent) {
            if (!silentMode) log.debug("Idle timeout, closing: {}",
                    ctx.channel().remoteAddress());
            closeAll(ctx);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        if (!silentMode && vlessConn != null) {
            log.info("[DISCONNECT] {} ← {}", ctx.channel().remoteAddress(),
                    vlessConn.getDestination());
        }
        if (session != null) {
            session.setState(SessionState.CLOSED);
            trafficService.recordTraffic(
                    vlessConn.getAuthToken(),
                    session.getBytesUploaded(),
                    session.getBytesDownloaded());
        }
        closeOutbound();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (!silentMode) {
            log.error("VLESS handler error: {}", cause.getMessage());
        }
        closeAll(ctx);
    }

    private void closeAll(ChannelHandlerContext ctx) {
        ctx.close();
        closeOutbound();
    }

    private void closeOutbound() {
        if (outboundChannel != null && outboundChannel.isActive()) {
            outboundChannel.close();
        }
    }
}
