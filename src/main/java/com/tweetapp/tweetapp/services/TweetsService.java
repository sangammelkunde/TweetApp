package com.tweetapp.tweetapp.services;

import com.tweetapp.tweetapp.exception.InvalidUsernameException;
import com.tweetapp.tweetapp.exception.TweetDoesNotExistException;
import com.tweetapp.tweetapp.model.Comment;
import com.tweetapp.tweetapp.model.TweetResponse;
import com.tweetapp.tweetapp.model.Tweets;
import com.tweetapp.tweetapp.model.Users;
import com.tweetapp.tweetapp.repository.TweetRepository;
import com.tweetapp.tweetapp.repository.UsersRepository;
import io.micrometer.core.instrument.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TweetsService {

    public static final String THIS_TWEET_DOES_NOT_EXIST_ANYMORE = "This tweet does not exist anymore.";
    @Autowired
    private TweetRepository tweetRepository;

    @Autowired
    private UsersRepository usersRepository;

    public List<Tweets> getAllTweets() {
        return tweetRepository.findAll();
    }

    public List<TweetResponse> getUserTweets(String username) throws InvalidUsernameException {
        if(!StringUtils.isBlank(username)) {
            List<Tweets> tweets = tweetRepository.findByUsername(username);
            return  tweets.stream().map(tweet ->{
                        Integer likesCount = tweet.getLikes().size();
                        Integer commentsCount = tweet.getComments().size();
                        return new TweetResponse(tweet.getTweetId(), username, tweet.getTweetText(),
                                tweet.getFirstName(), tweet.getLastName(), tweet.getTweetDate(),
                                likesCount, commentsCount,tweet.getImageUrl(), tweet.getComments());
                    })
                    .collect(Collectors.toList());
        } else {
            throw new InvalidUsernameException("Username/loginId provided is invalid");
        }

    }

    public List<TweetResponse> getUserTweetsByTweetId(String tweetId) throws InvalidUsernameException {
        if(!StringUtils.isBlank(tweetId)) {
            List<Tweets> tweets = tweetRepository.findByTweetId(tweetId);
            return  tweets.stream().map(tweet ->{
                        Integer likesCount = tweet.getLikes().size();
                        Integer commentsCount = tweet.getComments().size();
                        return new TweetResponse(tweet.getTweetId(), tweet.getUsername(), tweet.getTweetText(),
                                tweet.getFirstName(), tweet.getLastName(), tweet.getTweetDate(),
                                likesCount, commentsCount,tweet.getImageUrl(), tweet.getComments());
                    })
                    .collect(Collectors.toList());
        } else {
            throw new InvalidUsernameException("Username/loginId provided is invalid");
        }

    }

    //Method to post a new tweet
    public void postNewTweet(String username, Tweets newTweet) {

        newTweet.setTweetId(UUID.randomUUID().toString());
        LocalDateTime myDateObj = LocalDateTime.now();
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm");
        String formattedDate = myDateObj.format(myFormatObj);
        newTweet.setTweetDate(formattedDate);
        Users user=usersRepository.findByLoginId(username);
        newTweet.setFirstName(user.getFirstName());
        newTweet.setLastName(user.getLastName());
        newTweet.setUsername(username);
        newTweet.setImageUrl(user.getImageUrl());
        tweetRepository.insert(newTweet);
    }

    public Tweets updateTweet(String userId, String tweetId, String updatedTweetText) throws TweetDoesNotExistException {

        Optional<Tweets> originalTweetOptional = Optional.ofNullable(tweetRepository.findUserByUsernameAndTweetId(userId, tweetId));
        if(originalTweetOptional.isPresent()) {
            Tweets tweet = originalTweetOptional.get();
            tweet.setTweetText(updatedTweetText);
            return tweetRepository.save(tweet);
        } else {
            throw new TweetDoesNotExistException(THIS_TWEET_DOES_NOT_EXIST_ANYMORE);
        }

    }

    public void deleteTweet(String userName, String tweetId) throws TweetDoesNotExistException {
        if(tweetRepository.findByUsername(userName)!=null && tweetRepository.existsById(tweetId) && !StringUtils.isBlank(tweetId)) {
            tweetRepository.deleteById(tweetId);
        }else {

            throw new TweetDoesNotExistException(THIS_TWEET_DOES_NOT_EXIST_ANYMORE);
        }
    }


    public void likeTweet(String username, String tweetId) throws TweetDoesNotExistException{
        Optional<Tweets> tweetOptional = tweetRepository.findById(tweetId);
        if(tweetOptional.isPresent()) {
            Tweets tweet = tweetOptional.get();
            tweet.getLikes().add(username);
            tweetRepository.save(tweet);
        } else {
            throw new TweetDoesNotExistException(THIS_TWEET_DOES_NOT_EXIST_ANYMORE);
        }
    }

    public void disLikeTweet(String username, String tweetId) throws TweetDoesNotExistException{
        Optional<Tweets> tweetOptional = tweetRepository.findById(tweetId);
        if(tweetOptional.isPresent()) {
            Tweets tweet = tweetOptional.get();
            tweet.getLikes().remove(username);
            tweetRepository.save(tweet);
        } else {
            throw new TweetDoesNotExistException(THIS_TWEET_DOES_NOT_EXIST_ANYMORE);
        }
    }

    public boolean checkLikedOrNot(String username, String tweetId) throws TweetDoesNotExistException {
        Optional<Tweets> tweetOptional = tweetRepository.findById(tweetId);

        if(!tweetOptional.isPresent()) {
            throw new TweetDoesNotExistException(THIS_TWEET_DOES_NOT_EXIST_ANYMORE);
        }
        Tweets tweet = tweetOptional.get();
        return tweet.getLikes().contains(username);
    }

    public void replyTweet(String username, String tweetId, String tweetReply) throws TweetDoesNotExistException {
        Optional<Tweets> tweetOptional = tweetRepository.findById(tweetId);
        Users user=usersRepository.findByLoginId(username);
        if(tweetOptional.isPresent()) {
            Tweets tweet = tweetOptional.get();
            tweet.getComments().add(new Comment(username, tweetReply,user.getImageUrl()));
            tweetRepository.save(tweet);
        } else {
            throw new TweetDoesNotExistException(THIS_TWEET_DOES_NOT_EXIST_ANYMORE);
        }
    }

    public List<Tweets> findByTweetId(String tweetId){
        return tweetRepository.findByTweetId(tweetId);
    }

}
