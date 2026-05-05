package com.naz.profiler.config;

import io.github.bucket4j.distributed.ExpirationAfterWriteStrategy;
import io.github.bucket4j.distributed.proxy.ClientSideConfig;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import io.github.bucket4j.redis.lettuce.cas.LettuceBasedProxyManager;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.codec.ByteArrayCodec;
import io.lettuce.core.codec.RedisCodec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

import java.time.Duration;

@Configuration
public class RateLimitConfig {
//    @Value("${spring.data.redis.url:redis://localhost:6379}")
//    private String redisUrl;

//    private String redisUrl = "redis://localhost:6379";

//    @Bean
//    public RedisClient redisClient() {
//        return RedisClient.create(redisUrl);
//    }

//    @Bean
//    public ProxyManager<byte[]> proxyManager(RedisClient redisClient) {
//        StatefulRedisConnection<byte[], byte[]> connection = redisClient
//                .connect(RedisCodec.of(new ByteArrayCodec(), new ByteArrayCodec()));
//
//        ClientSideConfig clientSideConfig = ClientSideConfig.getDefault()
//                .withExpirationAfterWriteStrategy(ExpirationAfterWriteStrategy.none());
//
//        return LettuceBasedProxyManager.builderFor(connection)
//                .withClientSideConfig(clientSideConfig)
//                .build();
//    }

    @Bean
    public ProxyManager<byte[]> proxyManager(LettuceConnectionFactory connectionFactory) {
        // This pulls the connection from the Factory Spring already set up
        // using the spring.data.redis.url property above.
        RedisClient redisClient = (RedisClient) connectionFactory.getNativeClient();
        StatefulRedisConnection<byte[], byte[]> connection = redisClient
                .connect(RedisCodec.of(new ByteArrayCodec(), new ByteArrayCodec()));

        ClientSideConfig clientSideConfig = ClientSideConfig.getDefault()
                .withExpirationAfterWriteStrategy(ExpirationAfterWriteStrategy.fixedTimeToLive(Duration.ofHours(1)));

        return LettuceBasedProxyManager.builderFor(connection)
                .withClientSideConfig(clientSideConfig)
                .build();
    }
}
