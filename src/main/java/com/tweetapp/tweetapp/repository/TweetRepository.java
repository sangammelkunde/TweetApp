package com.tweetapp.tweetapp.repository;

import com.tweetapp.tweetapp.model.Tweets;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TweetRepository extends MongoRepository<Tweets, String>{

    List<Tweets> findByUsername(String username);

    @Query("{username : ?0}")
    Tweets findByTweetUsername(String username);

    @Query("{username : ?0, tweetId : ?1}")
    Tweets findUserByUsernameAndTweetId(String username, String tweetId);

    @Query("{tweetId : ?0}")
    List<Tweets> findByTweetId(String tweetId);
}
