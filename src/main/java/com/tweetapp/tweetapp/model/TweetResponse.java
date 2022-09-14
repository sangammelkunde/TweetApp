package com.tweetapp.tweetapp.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TweetResponse implements Serializable{


    private String tweetId;
    private String username;
    private String tweetText;
    private String firstName;
    private String lastName;
    private String tweetDate;
    private Integer likesCount;
    private Integer commentsCount;
    private String imageUrl;
    private List<Comment> comments = new ArrayList<>();
}
