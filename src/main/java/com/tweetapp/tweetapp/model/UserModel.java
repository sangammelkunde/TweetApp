package com.tweetapp.tweetapp.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Document(collection = "users")
public class UserModel {
    private String id;
    private String emailId;
    private String loginId;
    private String firstName;
    private String lastName;
    private String contactNo;
    private String imageUrl;
}
