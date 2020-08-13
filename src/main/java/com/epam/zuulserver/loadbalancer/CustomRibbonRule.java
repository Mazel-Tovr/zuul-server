package com.epam.zuulserver.loadbalancer;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.loadbalancer.BestAvailableRule;
import com.netflix.loadbalancer.Server;
import com.netflix.niws.loadbalancer.DiscoveryEnabledServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.netflix.ribbon.eureka.DomainExtractingServerList;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;


public class CustomRibbonRule extends BestAvailableRule {

    private static Logger log = LoggerFactory.getLogger(CustomRibbonRule.class);

    @Value("${cart-order-service.headerkey}")
    private String routingKey;

    @Autowired
    @Lazy
    private DomainExtractingServerList domainExtractingServerList;

    @Override
    public Server choose(Object key) {
        Optional<DiscoveryEnabledServer> enabledServer =
                domainExtractingServerList.getUpdatedListOfServers()
                        .stream()
                        .filter(e -> e.getInstanceInfo().getStatus().equals(InstanceInfo.InstanceStatus.UP)
                                && e.getInstanceInfo().getMetadata().get(routingKey).equals(getRoutingHeader()))
                        .findAny();
        if (enabledServer.isPresent()) {
            log.debug("Header algorithm used");
            return enabledServer.get();
        }
        log.debug("Default algorithm used");
        return super.choose(key);
    }

    private String getRoutingHeader() {
        HttpServletRequest request =
                ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                        .getRequest();
        return request.getHeader(routingKey);
    }

}