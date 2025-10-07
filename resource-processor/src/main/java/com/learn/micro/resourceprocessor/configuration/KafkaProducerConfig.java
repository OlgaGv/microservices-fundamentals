package com.learn.micro.resourceprocessor.configuration;

//@Configuration
public class KafkaProducerConfig {
//
//    private final KafkaProperties kafkaProperties;
//
//    public KafkaProducerConfig(KafkaProperties kafkaProperties) {
//        this.kafkaProperties = kafkaProperties;
//    }
//
//    @Bean
//    public ProducerFactory<String, ResourceEvent> producerFactory() {
//        Map<String, Object> props = kafkaProperties.buildProducerProperties();
//        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
//        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
//        return new DefaultKafkaProducerFactory<>(props);
//    }
//
//    @Bean
//    public KafkaTemplate<String, ResourceEvent> kafkaTemplate() {
//        return new KafkaTemplate<>(producerFactory());
//    }
}