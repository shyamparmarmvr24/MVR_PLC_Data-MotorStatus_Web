package com.mvrtechnology.plcdata.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import java.util.concurrent.*;

@Configuration
public class ExecutorConfig
{
    @Bean(name = "motorExecutor")
    public ExecutorService motorExecutor()
    {
        return build("motor-", 50);
    }

    @Bean(name = "solarPollExecutor")
    public ExecutorService solarPollExecutor()
    {
        return build("solar-poll-", 50);
    }

    @Bean(name = "liveEffluentExecutor")
    public ExecutorService liveEffluentExecutor()
    {
        return build("live-effluent-", 30);
    }

    @Bean(name = "liveSolarExecutor")
    public ExecutorService liveSolarExecutor()
    {
        return build("live-solar-", 30);
    }

    private ExecutorService build(String prefix, int size)
    {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                size, size, 0L, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(1000));
        executor.setThreadFactory(new CustomizableThreadFactory(prefix));
        return executor;
    }
}