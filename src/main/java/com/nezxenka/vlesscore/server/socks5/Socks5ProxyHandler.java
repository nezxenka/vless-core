package com.nezxenka.vlesscore.server.socks5;

import com.nezxenka.vlesscore.config.AppConfig;
import com.nezxenka.vlesscore.database.dao.TokenDao;
import com.nezxenka.vlesscore.service.VlessAuthService;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.socksx.v5.*;
import com.nezxenka.vlesscore.server.vless.RelayHandler;
import com.nezxenka.vlesscore.server.vless.VlessServerHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

public class Socks5ProxyHandler extends SimpleChannelInboundHandler<Socks5Message> {

    private static final Logger log = LoggerFactory.getLogger(Socks5ProxyHandler.class);

    private final AppConfig config;
    private final TokenDao tokenDao;
    private final Socks5AuthValidator authValidator;

    public Socks5ProxyHandler(AppConfig config, TokenDao tokenDao, VlessAuthService authService) {
        this.config = config;
        this.tokenDao = tokenDao;
        this.authValidator = new Socks5AuthValidator(tokenDao);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Socks5Message msg) throws Exception {

        if (msg instanceof Socks5InitialRequest req) {
            boolean needAuth = config.isSocks5AuthEnabled();

            if (needAuth) {
                ctx.pipeline().addFirst(new Socks5PasswordAuthRequestDecoder());
                ctx.writeAndFlush(new DefaultSocks5InitialResponse(
                        Socks5AuthMethod.PASSWORD));
            } else {
                ctx.pipeline().addFirst(new Socks5CommandRequestDecoder());
                ctx.writeAndFlush(new DefaultSocks5InitialResponse(
                        Socks5AuthMethod.NO_AUTH));
            }
            return;
        }

        if (msg instanceof Socks5PasswordAuthRequest authReq) {
            String user = authReq.username();
            String pass = authReq.password();

            boolean ok = authValidator.validate(user, pass);
            if (!ok) {
                ctx.writeAndFlush(new DefaultSocks5PasswordAuthResponse(
                        Socks5PasswordAuthStatus.FAILURE));
                ctx.close();
                return;
            }

            ctx.pipeline().addFirst(new Socks5CommandRequestDecoder());
            ctx.writeAndFlush(new DefaultSocks5PasswordAuthResponse(
                    Socks5PasswordAuthStatus.SUCCESS));
            return;
        }

        if (msg instanceof Socks5CommandRequest cmdReq) {
            if (cmdReq.type() != Socks5CommandType.CONNECT) {
                ctx.writeAndFlush(new DefaultSocks5CommandResponse(
                        Socks5CommandStatus.COMMAND_UNSUPPORTED,
                        Socks5AddressType.IPv4, "0.0.0.0", 0));
                ctx.close();
                return;
            }

            String host = cmdReq.dstAddr();
            int port = cmdReq.dstPort();

            if (!VlessServerHandler.silentMode) {
                log.info("[SOCKS5] {} → {}:{}", ctx.channel().remoteAddress(), host, port);
            }

            connectToTarget(ctx, host, port);
        }
    }

    private void connectToTarget(ChannelHandlerContext ctx, String host, int port) {
        Channel inbound = ctx.channel();

        Bootstrap b = new Bootstrap();
        b.group(inbound.eventLoop())
                .channel(NioSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.AUTO_READ, false)
                .handler(new ChannelInitializer<>() {
                    @Override
                    protected void initChannel(Channel ch) {
                        ch.pipeline().addLast(new RelayHandler(inbound,
                                VlessServerHandler.silentMode));
                    }
                });

        b.connect(new InetSocketAddress(host, port))
                .addListener((ChannelFutureListener) future -> {
                    if (future.isSuccess()) {
                        Channel outbound = future.channel();

                        ctx.writeAndFlush(new DefaultSocks5CommandResponse(
                                Socks5CommandStatus.SUCCESS,
                                Socks5AddressType.IPv4,
                                "0.0.0.0", 0
                        )).addListener(f -> {
                            if (f.isSuccess()) {
                                ctx.pipeline().remove(Socks5ProxyHandler.this);
                                ctx.pipeline().addLast(new RelayHandler(outbound,
                                        VlessServerHandler.silentMode));
                                outbound.read();
                            }
                        });
                    } else {
                        ctx.writeAndFlush(new DefaultSocks5CommandResponse(
                                Socks5CommandStatus.FAILURE,
                                Socks5AddressType.IPv4, "0.0.0.0", 0));
                        ctx.close();
                    }
                });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (!VlessServerHandler.silentMode) {
            log.error("SOCKS5 error: {}", cause.getMessage());
        }
        ctx.close();
    }
}
