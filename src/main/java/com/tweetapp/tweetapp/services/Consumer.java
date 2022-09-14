package com.tweetapp.tweetapp.services;

import com.tweetapp.tweetapp.model.Tweets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class Consumer {

    private final Logger logger = LoggerFactory.getLogger(Producer.class);

    @KafkaListener(topics = "tweets", groupId = "group_id")
    public void consume(Tweets tweets) throws IOException {
        logger.info("consumed tweets "+tweets.toString());
    }
}
