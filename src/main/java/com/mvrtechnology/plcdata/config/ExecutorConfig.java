package com.mvrtechnology.plcdata.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class ExecutorConfig {

    @Bean
    public ExecutorService motorExecutor()
    {
        return new ThreadPoolExecutor(
                40,
                40,
                0L,
                TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(500),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }
}
