package com.tweetapp.tweetapp.controller;

import com.tweetapp.tweetapp.auth.JwtResponse;
import com.tweetapp.tweetapp.auth.JwtTokenUtil;
import com.tweetapp.tweetapp.model.ForgotPassword;
import com.tweetapp.tweetapp.model.LoadFileVO;
import com.tweetapp.tweetapp.model.LoginCredentials;
import com.tweetapp.tweetapp.model.Users;
import com.tweetapp.tweetapp.services.JwtUserDetailsService;
import com.tweetapp.tweetapp.services.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/v1.0/tweets")
public class TweetAppApplicationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private JwtUserDetailsService userDetailsService;

    @Autowired
    private UsersService usersService;

    JwtResponse jwtResponse=new JwtResponse();

    @PostMapping("/register")
    public ResponseEntity<?> registerNewUser(@RequestBody Users users) {
        if(!usersService.checkEmailAndLoginId(users)){
            return new ResponseEntity<>("Email Id and Login Id must be same.", HttpStatus.BAD_REQUEST);
        }
        if(!usersService.checkExistOrNot(users)){

            return new ResponseEntity<>(usersService.storeUserDetails(users), HttpStatus.CREATED);
        }
        return new ResponseEntity<>("User name already exist, please login",
                HttpStatus.CONFLICT);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginCredentials user) throws Exception {
       // authenticate(user.getUsername(), user.getPassword());

        if (usersService.checkUser(user.getUsername(), user.getPassword())) {
            final UserDetails userDetails = userDetailsService
                    .loadUserByUsername(user.getUsername());
            final String token = jwtTokenUtil.generateToken(userDetails);
            jwtResponse.setJwttoken(token);
            return new ResponseEntity<>(usersService.getUser(user.getUsername(), user.getPassword()), HttpStatus.OK);
        }
        return new ResponseEntity<>("\"Invalid credentials\"", HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/{username}/forgot")
    public ResponseEntity<String> forgotPassword(@PathVariable String username, @RequestBody ForgotPassword forgotPassword){

        if(usersService.forgotPassword(username,forgotPassword.getPassword())){
            return new ResponseEntity<>("\"password changed\"",HttpStatus.OK);
        }
        return new ResponseEntity<>("\"user name not found\"",HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/users/all")
    public ResponseEntity<?> getAllUsers(){
        return new ResponseEntity<>(usersService.getAllUsers(), HttpStatus.OK);

    }

    @GetMapping("/user/search/{userName}")
    public ResponseEntity<?> getByUserName(@PathVariable String userName){
        if(usersService.getByUserName(userName)!=null){
            return new ResponseEntity<>(usersService.getDetailsOfUser(userName), HttpStatus.OK);
        }
        return new ResponseEntity<>("User name not found", HttpStatus.BAD_REQUEST);
    }

    @PutMapping("/avatar")
    public ResponseEntity<?> addAvatarFile(@RequestParam String username, @RequestParam("file") MultipartFile file ) throws IOException {
        usersService.addFile(username,file);
        return new ResponseEntity<>("\"Profile uploaded\"", HttpStatus.CREATED);
    }
    @GetMapping("avatar/{id}")
    public ResponseEntity<ByteArrayResource> getHostFile(@PathVariable String id) throws IOException {

        LoadFileVO loadFile = usersService.getHostFile(id);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(loadFile.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + loadFile.getFilename() + "\"")
                .body(new ByteArrayResource(loadFile.getFile()));

    }

    @GetMapping("/jwt/authentication")
    public ResponseEntity<?> getToken(){
        return new ResponseEntity<>(jwtResponse,HttpStatus.OK);
    }

    private void authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }
}
