package com.budgetmap.client;

import feign.Request;
import feign.Retryer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class PythonServiceClientConfig {

    @Bean
    public Request.Options options() {
        return new Request.Options(
            5, TimeUnit.SECONDS,  
            10, TimeUnit.SECONDS, 
            true                  
        );
    }

    @Bean
    public Retryer retryer() {
        return new Retryer.Default(
            1000, 
            5000, 
            3     
        );
    }
}