package com.tweetapp.tweetapp.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tweetapp.tweetapp.auth.JwtTokenUtil;
import com.tweetapp.tweetapp.exception.InvalidUsernameException;
import com.tweetapp.tweetapp.exception.TweetDoesNotExistException;
import com.tweetapp.tweetapp.model.Reply;
import com.tweetapp.tweetapp.model.TweetUpdate;
import com.tweetapp.tweetapp.model.Tweets;
import com.tweetapp.tweetapp.model.Users;
import com.tweetapp.tweetapp.services.Producer;
import com.tweetapp.tweetapp.services.TweetsService;
import com.tweetapp.tweetapp.services.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
@CrossOrigin(origins = "*")
//@CrossOrigin(origins = "http://localhost:4200/")
@RestController
@RequestMapping("/api/v1.0/tweets")
public class TweetController {

    public static final String USER_NAME_NOT_FOUND = "User name not found";
    public static final String GIVEN_TWEET_ID_CANNOT_BE_FOUND = "\"Given tweetId cannot be found\"";
    @Autowired
    private TweetsService tweetService;

    @Autowired
    private UsersService usersService;

    @Autowired
    private Producer kafkaProducer;

    UserDetails loginCredentials;

    private final JwtTokenUtil jwtTokenUtil=new JwtTokenUtil();

    @PostMapping("/{userName}/add")
    public ResponseEntity<?> postNewTweet(@PathVariable String userName, @RequestBody Tweets tweets, @RequestHeader String Authorization) throws ExecutionException, JsonProcessingException, InterruptedException {
        Users user= getUserDetails(userName);

        if(user==null){
            return new ResponseEntity<>(USER_NAME_NOT_FOUND,HttpStatus.BAD_REQUEST);
        }
            if(Authorization!=null && jwtTokenUtil.validateToken(Authorization, loginCredentials)  ){
                tweetService.postNewTweet(userName, tweets);
                //kafkaProducer.sendMessage(tweets);
                return new ResponseEntity<>("\"Tweet created\"",HttpStatus.CREATED);
            }
            return new ResponseEntity<>("Unauthorized",HttpStatus.UNAUTHORIZED);
    }

    @GetMapping( "/all")
    public ResponseEntity<?> getAllTweets() {
            return new ResponseEntity<>(tweetService.getAllTweets(), HttpStatus.OK);
    }

    @GetMapping( "/{userName}")
    public ResponseEntity<?> getUserTweets(@PathVariable String userName ) throws InvalidUsernameException {
        Users user=usersService.getByUserName(userName);
        if(user==null){
            return new ResponseEntity<>(USER_NAME_NOT_FOUND,
                    HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(tweetService.getUserTweets(userName), HttpStatus.OK);

    }

    @GetMapping( "/byTweetId/{tweetId}")
    public ResponseEntity<?> getUserTweetsByTweetId(@PathVariable String tweetId ) throws InvalidUsernameException {
        return new ResponseEntity<>(tweetService.getUserTweetsByTweetId(tweetId), HttpStatus.OK);
    }

    @PutMapping( "/{userName}/update/{tweetId}")
    public ResponseEntity<?> updateTweet(@PathVariable String userName, @PathVariable String tweetId,@RequestBody TweetUpdate tweetUpdate, HttpServletRequest request) {
        Users user=getUserDetails(userName);
        if(user==null){
            return new ResponseEntity<>(USER_NAME_NOT_FOUND,
                    HttpStatus.NOT_FOUND);
        }
        try {

            return new ResponseEntity<>(tweetService.updateTweet(userName,tweetId, tweetUpdate.getTweetText()), HttpStatus.OK);
        } catch (TweetDoesNotExistException e) {
            return new ResponseEntity<>("Given tweetId cannot be found",
                    HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping( "/{userName}/delete/{tweetId}")
    public ResponseEntity<?> deleteTweet( @PathVariable String userName,
                                          @PathVariable String tweetId,@RequestHeader String Authorization) {
        Users user= getUserDetails(userName);

        if(user==null){
            return new ResponseEntity<>("User name not found",
                    HttpStatus.NOT_FOUND);
        }

        try {
            if(  Authorization!=null &&jwtTokenUtil.validateToken(Authorization,loginCredentials) ){
                tweetService.deleteTweet(userName,tweetId);
                return new ResponseEntity<>("\"Tweet deleted successfully\"", HttpStatus.OK);
            }
            return new ResponseEntity<>("\"Unauthorized\"", HttpStatus.UNAUTHORIZED);
        } catch (TweetDoesNotExistException e) {
            return new ResponseEntity<>(GIVEN_TWEET_ID_CANNOT_BE_FOUND,
                    HttpStatus.NOT_FOUND);
        }
    }



    @PutMapping( "/{userName}/like/{tweetId}")
    public ResponseEntity<?> likeATweet(@PathVariable String userName, @PathVariable String tweetId,@RequestHeader String Authorization) {

        Users user= getUserDetails(userName);
        if(user==null){
            return new ResponseEntity<>(USER_NAME_NOT_FOUND,
                    HttpStatus.NOT_FOUND);
        }

        try {
            if(  Authorization!=null && jwtTokenUtil.validateToken(Authorization,loginCredentials)){
                if(!tweetService.checkLikedOrNot(userName, tweetId)){
                    tweetService.likeTweet(userName, tweetId);
                    return new ResponseEntity<>("\" liked tweet \"", HttpStatus.OK);
                }else{
                    tweetService.disLikeTweet(userName, tweetId);
                    return new ResponseEntity<>("\"Disliked tweet\"", HttpStatus.OK);
                }
            }
            return new ResponseEntity<>("\"Unauthorized\"", HttpStatus.UNAUTHORIZED);
        } catch (TweetDoesNotExistException e) {
            return new ResponseEntity<>(GIVEN_TWEET_ID_CANNOT_BE_FOUND,
                    HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/{userName}/reply/{tweetId}")
    public ResponseEntity<?> replyToTweet(@PathVariable String userName,
                                          @PathVariable String tweetId, @RequestBody Reply tweetReply, @RequestHeader String Authorization) {
        Users user= getUserDetails(userName);
        if(user==null){
            return new ResponseEntity<>(USER_NAME_NOT_FOUND,
                    HttpStatus.NOT_FOUND);
        }
        try {
            if( Authorization!=null&&jwtTokenUtil.validateToken( Authorization,loginCredentials) ){
                tweetService.replyTweet(userName, tweetId, tweetReply.getComment());
                return new ResponseEntity<>("\"Replied\"", HttpStatus.OK);
            }
            return new ResponseEntity<>("\"Unauthorized\"", HttpStatus.UNAUTHORIZED);
        } catch (TweetDoesNotExistException e) {
            return new ResponseEntity<>(GIVEN_TWEET_ID_CANNOT_BE_FOUND,
                    HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/getLike/{tweetId}")
    public ResponseEntity<?> getLikes(@PathVariable String tweetId){
        List<Tweets> tweet=tweetService.findByTweetId(tweetId);
        if(tweet==null){
            return new ResponseEntity<>("\"Tweet id not found\"",HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(tweetService.findByTweetId(tweetId),HttpStatus.OK);
    }

    public Users getUserDetails(String userName) {
        Users user=usersService.getByUserName(userName);
        loginCredentials = new UserDetails() {
            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return null;
            }

            @Override
            public String getPassword() {
                return user.getPassword();
            }

            @Override
            public String getUsername() {
                return user.getLoginId();
            }

            @Override
            public boolean isAccountNonExpired() {
                return false;
            }

            @Override
            public boolean isAccountNonLocked() {
                return false;
            }

            @Override
            public boolean isCredentialsNonExpired() {
                return false;
            }

            @Override
            public boolean isEnabled() {
                return false;
            }
        };
        return user;
    }
}
