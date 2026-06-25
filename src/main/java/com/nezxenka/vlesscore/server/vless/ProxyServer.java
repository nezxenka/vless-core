package com.nezxenka.vlesscore.server.vless;

import com.nezxenka.vlesscore.config.AppConfig;
import com.nezxenka.vlesscore.database.dao.TokenDao;
import com.nezxenka.vlesscore.service.VlessAuthService;
import com.nezxenka.vlesscore.service.TrafficService;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProxyServer {

    private static final Logger log = LoggerFactory.getLogger(ProxyServer.class);

    private final AppConfig config;
    private final TokenDao tokenDao;
    private final VlessAuthService authService;
    private final TrafficService trafficService;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private Channel serverChannel;

    public ProxyServer(AppConfig config, TokenDao tokenDao,
                        VlessAuthService authService, TrafficService trafficService) {
        this.config = config;
        this.tokenDao = tokenDao;
        this.authService = authService;
        this.trafficService = trafficService;
    }

    public void start() throws InterruptedException {
        int workers = config.getWorkerThreads() > 0
                ? config.getWorkerThreads()
                : Runtime.getRuntime().availableProcessors() * 2;

        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup(workers);

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new FrontendInitializer(config, tokenDao, authService, trafficService))
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .option(ChannelOption.SO_REUSEADDR, true)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childOption(ChannelOption.AUTO_READ, true)
                    .childOption(ChannelOption.WRITE_BUFFER_WATER_MARK,
                            new WriteBufferWaterMark(32 * 1024, 64 * 1024));

            ChannelFuture future = bootstrap.bind(config.getPort()).sync();
            serverChannel = future.channel();

            log.info("═══════════════════════════════════════");
            log.info("  {} started on port {}", config.getServerName(), config.getPort());
            log.info("  TLS: {}", config.isTlsEnabled() ? "ENABLED" : "DISABLED");
            log.info("  Worker threads: {}", workers);
            log.info("═══════════════════════════════════════");

            serverChannel.closeFuture().sync();
        } finally {
            stop();
        }
    }

    public void stop() {
        log.info("Stopping proxy server...");
        if (serverChannel != null) {
            serverChannel.close();
        }
        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
    }
}
