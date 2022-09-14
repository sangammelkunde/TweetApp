package com.tweetapp.tweetapp.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Document(collection = "tweets")
public class Tweets {
    @Id
    private String tweetId;
    private String username;
    private String tweetText;
    private String firstName;
    private String lastName;
    private String tweetDate;
    private String imageUrl;
    private List<String> likes = new ArrayList<>();
    private List<Comment> comments = new ArrayList<>();

}
