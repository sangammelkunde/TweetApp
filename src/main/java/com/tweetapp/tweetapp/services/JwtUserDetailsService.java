package com.tweetapp.tweetapp.services;

import com.tweetapp.tweetapp.model.Users;
import com.tweetapp.tweetapp.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class JwtUserDetailsService implements UserDetailsService {
    @Autowired
    private UsersRepository userRepository;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // TODO Auto-generated method stub
        Users foundedUser = userRepository.findByLoginId(username);
        if(foundedUser == null) return null;
        String name = foundedUser.getLoginId();
        String pwd = foundedUser.getPassword();
        return new User(name, pwd, new ArrayList<>());
    }
}
