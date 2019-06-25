package com.trendyol.recomengine.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.trendyol.recomengine.engine.Producer;
import com.trendyol.recomengine.resource.Review;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController

public class ReviewController {

    private final Producer producer;

    @Autowired
    ReviewController(Producer producer) {
        this.producer = producer;
    }

    @PostMapping(value = "/users/{userId}/reviews")
    public Object sendMessageToKafkaTopic(@RequestBody Review newReview) {
        try {
            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            String jsonObject = ow.writeValueAsString(newReview);
            this.producer.sendMessage(jsonObject);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        if (newReview.getScore() > 5) return new Object() {
            public final String error = "Score can't be larger than 5";
        };

        return newReview;
    }
}