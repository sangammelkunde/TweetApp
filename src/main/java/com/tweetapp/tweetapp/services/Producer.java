package com.tweetapp.tweetapp.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tweetapp.tweetapp.model.Tweets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class Producer {
    private static final String TOPIC = "tweets";

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private KafkaTemplate<String, Tweets> kafkaTemplate;

    public void sendMessage(Tweets tweets) {
        this.kafkaTemplate.send(TOPIC, tweets);
    }
}
