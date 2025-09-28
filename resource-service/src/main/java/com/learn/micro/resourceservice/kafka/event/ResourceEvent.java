package com.learn.micro.resourceservice.kafka.event;

import java.util.Objects;

public record ResourceEvent(String resourceId, EventType eventType){

    public ResourceEvent {
        Objects.requireNonNull(resourceId);
        Objects.requireNonNull(eventType);
    }
}
