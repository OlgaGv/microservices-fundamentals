package com.learn.micro.resourceprocessor.kafka.event;

import java.util.Objects;

public record ResourceEvent(String resourceId, EventType eventType){

    public ResourceEvent {
        Objects.requireNonNull(resourceId);
        Objects.requireNonNull(eventType);
    }
}
