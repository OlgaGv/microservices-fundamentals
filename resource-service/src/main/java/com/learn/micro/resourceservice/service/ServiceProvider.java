package com.learn.micro.resourceservice.service;

import com.learn.micro.resourceservice.exception.GeneralFailureException;
import java.text.MessageFormat;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ServiceProvider {

    private final DiscoveryClient discoveryClient;
    private final MessageHelper messageHelper;

    public ServiceInstance getServiceInstance(String serviceName) {
        return Optional.ofNullable(
                discoveryClient.getInstances(serviceName))
            .filter(instances -> !instances.isEmpty())
            .map(instances -> instances.get(0))
            .orElseThrow(() -> new GeneralFailureException(
                MessageFormat.format(messageHelper.getMessage("server.error.no.service"),
                    serviceName)));
    }
}
