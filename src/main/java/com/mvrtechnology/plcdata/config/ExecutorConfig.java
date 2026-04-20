package com.mvrtechnology.plcdata.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ExecutorConfig {

    @Bean
    public ExecutorService motorExecutor() {
        return Executors.newFixedThreadPool(20);
    }
}
