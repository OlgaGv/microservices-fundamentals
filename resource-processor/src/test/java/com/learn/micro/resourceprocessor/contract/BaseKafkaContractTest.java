package com.learn.micro.resourceprocessor.contract;

import com.learn.micro.resourceprocessor.kafka.ResourceConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cloud.contract.verifier.messaging.boot.AutoConfigureMessageVerifier;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.messaging.PollableChannel;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.kafka.ConfluentKafkaContainer;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMessageVerifier
@Import(BaseKafkaContractTest.TestConfig.class)
public abstract class BaseKafkaContractTest {

    static ConfluentKafkaContainer kafka = new ConfluentKafkaContainer(
        DockerImageName.parse("confluentinc/cp-kafka:7.4.0"));

    static {
        System.out.println(System.getenv("DOCKER_HOST"));
        kafka.start();
        System.setProperty("spring.kafka.bootstrap-servers", kafka.getBootstrapServers());
    }

    @Autowired
    private StreamBridge streamBridge;

    @Autowired
    protected ResourceConsumer resourceConsumer;

    public void resourceCreatedEvent() {
        streamBridge.send("resource-events", "123");
    }

    @TestConfiguration
    public static class TestConfig {
        @Bean(name = "resource-events")
        public PollableChannel resourceEventsChannel() {
            return new QueueChannel();
        }
    }
}
