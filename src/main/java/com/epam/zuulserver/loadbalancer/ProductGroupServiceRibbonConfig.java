package com.epam.zuulserver.loadbalancer;

import com.netflix.loadbalancer.BestAvailableRule;
import com.netflix.loadbalancer.IPing;
import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.PingUrl;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RibbonClient(name = "${product-group-service}", configuration = ProductGroupServiceRibbonConfig.class)
public class ProductGroupServiceRibbonConfig {

    @Bean
    public IPing ping() {
        return new PingUrl();
    }

    @Bean
    public IRule rule()
    {
        return new BestAvailableRule();
    }

}
