package com.nezxenka.vlesscore.server.vless;

import com.nezxenka.vlesscore.config.AppConfig;
import com.nezxenka.vlesscore.database.dao.TokenDao;
import com.nezxenka.vlesscore.service.VlessAuthService;
import com.nezxenka.vlesscore.service.TrafficService;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

public class FrontendInitializer extends ChannelInitializer<SocketChannel> {

    private final AppConfig config;
    private final TokenDao tokenDao;
    private final VlessAuthService authService;
    private final TrafficService trafficService;

    public FrontendInitializer(AppConfig config, TokenDao tokenDao,
                                VlessAuthService authService, TrafficService trafficService) {
        this.config = config;
        this.tokenDao = tokenDao;
        this.authService = authService;
        this.trafficService = trafficService;
    }

    @Override
    protected void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();

        pipeline.addLast("idle", new IdleStateHandler(5, 5, 5, TimeUnit.MINUTES));
        pipeline.addLast("vless", new VlessServerHandler(config, tokenDao, authService, trafficService));
    }
}
