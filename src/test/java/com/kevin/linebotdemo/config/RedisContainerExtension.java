package com.kevin.linebotdemo.config;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

@Slf4j
public class RedisContainerExtension implements AfterAllCallback {

    private static final GenericContainer redis;

    static {
        val imageName = DockerImageName.parse("redis:latest");
        redis = new GenericContainer(imageName)
                .withReuse(true)
                .withExposedPorts(6379);
        redis.start();

        System.setProperty("spring.redis.port", "" + redis.getFirstMappedPort());
    }

    @Override
    public void afterAll(ExtensionContext context) {
    }
}
