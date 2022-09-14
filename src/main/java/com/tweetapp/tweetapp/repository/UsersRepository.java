package com.tweetapp.tweetapp.repository;

import com.tweetapp.tweetapp.model.UserModel;
import com.tweetapp.tweetapp.model.Users;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersRepository extends MongoRepository<Users,String> {
    Users findByLoginId(String loginId);
    Boolean existsByLoginId(String loginId);

    @Query("{loginId : ?0, password : ?1}")
    UserModel findUserByUsernameAndPassword(String loginId, String password);
}
