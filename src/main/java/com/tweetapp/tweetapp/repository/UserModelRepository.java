package com.tweetapp.tweetapp.repository;

import com.tweetapp.tweetapp.model.UserModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserModelRepository extends MongoRepository<UserModel,String> {
    UserModel findByLoginId(String loginId);
}
