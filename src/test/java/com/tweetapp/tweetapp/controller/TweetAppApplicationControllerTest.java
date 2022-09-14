package com.tweetapp.tweetapp.controller;

import com.tweetapp.tweetapp.model.Users;
import com.tweetapp.tweetapp.services.UsersService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@AutoConfigureMockMvc
@SpringBootTest
class TweetAppApplicationControllerTest {

    @Mock
    private UsersService usersService;

    @Autowired
    UsersService usersServiceBean;

    @Autowired
    MockMvc mockMvc;

    @Test
    void loginUserTest() throws Exception{
       String userName="sangam@gmail.com";
       String password="Sangam@123";
       when(usersService.checkUser(userName,password)).thenReturn(true);
        MvcResult result = mockMvc
                .perform(get("http://localhost:8080/api/v1.0/tweets/login?userName="+userName+"&password="+password))
                .andReturn();
       String actual=result.getResponse().getContentAsString();
       String expected="{\"id\":\"627212ea1741401b94be72a8\",\"emailId\":\"sangam@gmail.com\",\"loginId\":\"sangam@gmail.com\",\"firstName\":\"sangam\",\"lastName\":\"melkunde\",\"password\":\"Sangam@123\",\"contactNo\":\"8830233538\"}";
       assertEquals(expected,actual);
    }

    @Test
    void InvalidLoginUserTest() throws Exception{
        String userName="praneeth@gmail.com";
        String password="invalid";
        when(usersService.checkUser(userName,password)).thenReturn(false);
        MvcResult result = mockMvc
                .perform(get("http://localhost:8080/api/v1.0/tweets/login?userName="+userName+"&password="+password))
                .andReturn();
        String actual=result.getResponse().getContentAsString();
        String expected="Invalid credentials";
        assertEquals(expected,actual);
    }

    @Test
    void registerUserTest() throws Exception{
        Users user=new Users("test-123","siva@gmail.com","siva@gmail.com","siva","siva","siva","9988776655","");
        when(usersService.checkExistOrNot(user)).thenReturn(false);
        MvcResult result = mockMvc
                .perform(post("http://localhost:8080/api/v1.0/tweets/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":\"test-123\",\"emailId\":\"siva@gmail.com\",\"loginId\":\"siva@gmail.com\",\"firstName\":\"siva\",\"lastName\":\"siva\",\"password\":\"siva\",\"contactNo\":\"9988776655\"}")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        String actual=result.getResponse().getContentAsString();
        String expected="{\"id\":\"test-123\",\"emailId\":\"siva@gmail.com\",\"loginId\":\"siva@gmail.com\",\"firstName\":\"siva\",\"lastName\":\"siva\",\"password\":\"siva\",\"contactNo\":\"9988776655\"}";
        assertEquals(expected,actual);
    }

    @Test
    void checkAlreadyRegisteredUserTest() throws Exception{
        Users user=new Users("test-123","siva@gmail.com","siva@gmail.com","siva","siva","siva","9988776655","");
        when(usersService.checkExistOrNot(user)).thenReturn(true);
        when(usersService.checkEmailAndLoginId(user)).thenReturn(true);
        MvcResult result = mockMvc
                .perform(post("http://localhost:8080/api/v1.0/tweets/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":\"test-123\",\"emailId\":\"siva@gmail.com\",\"loginId\":\"siva@gmail.com\",\"firstName\":\"siva\",\"lastName\":\"siva\",\"password\":\"siva\",\"contactNo\":\"9988776655\"}")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        String actual=result.getResponse().getContentAsString();
        String expected="User name already exist, please login";
        assertEquals(expected,actual);
    }

    @Test
    void checkEmailAndLoginIdTest() throws Exception{
        Users user=new Users("test-1234","kalyani@gmail.com","kalyan@gmail.com","siva","siva","siva","9988776655","");
        when(usersService.checkExistOrNot(user)).thenReturn(false);
        when(usersService.checkEmailAndLoginId(user)).thenReturn(false);
        MvcResult result = mockMvc
                .perform(post("http://localhost:8080/api/v1.0/tweets/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":\"test-1234\",\"emailId\":\"kalyan@gmail.com\",\"loginId\":\"kalyani@gmail.com\",\"firstName\":\"siva\",\"lastName\":\"siva\",\"password\":\"siva\",\"contactNo\":\"9988776655\"}")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        String actual=result.getResponse().getContentAsString();
        String expected="Email Id and Login Id must be same.";
        assertEquals(expected,actual);
    }

    @Test
    void resetPasswordTest() throws Exception{
        String userName="siva@gmail.com";
        when(usersService.forgotPassword(userName,"siva")).thenReturn(true);
        MvcResult result = mockMvc
                .perform(post("http://localhost:8080/api/v1.0/tweets/"+userName+"/forgot")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"password\":\"siva\"}")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        String actual=result.getResponse().getContentAsString();
        String expected="password changed";
        assertEquals(expected,actual);
    }

    @Test
    void invalidUserFoundTest() throws Exception{
        String userName="siv@gmail.com";
        when(usersService.forgotPassword(userName,"siva")).thenReturn(true);
        MvcResult result = mockMvc
                .perform(post("http://localhost:8080/api/v1.0/tweets/"+userName+"/forgot")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"password\":\"siva\"}")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        String actual=result.getResponse().getContentAsString();
        String expected="user name not found";
        assertEquals(expected,actual);
    }

    @Test
    void getAllUsersTest() throws Exception{
        MvcResult result = mockMvc
                .perform(get("http://localhost:8080/api/v1.0/tweets/users/all"))
                .andReturn();
        String actual=result.getResponse().getContentAsString();
        assertEquals(actual,actual);
    }

    @Test
    void getByUserNameTest() throws Exception{
        String userName="praneeth@gmail.com";
        MvcResult result = mockMvc
                .perform(get("http://localhost:8080/api/v1.0/tweets/user/search/"+userName))
                .andReturn();
        String actual=result.getResponse().getContentAsString();
        String expected="{\"id\":\"627212ea1741401b94be72a8\",\"emailId\":\"praneeth@gmail.com\",\"loginId\":\"praneeth@gmail.com\",\"firstName\":\"Praneeth\",\"lastName\":\"Praneeth\",\"password\":\"praneeth\",\"contactNo\":\"1234567890\"}";
        assertEquals(expected,actual);
    }

    @Test
    void checkInvalidGetByUserNameTest() throws Exception{
        String userName="praneet@gmail.com";
        MvcResult result = mockMvc
                .perform(get("http://localhost:8080/api/v1.0/tweets/user/search/"+userName))
                .andReturn();
        when(usersService.getByUserName(userName)).thenReturn(null);
        String actual=result.getResponse().getContentAsString();
        String expected="User name not found";
        assertEquals(expected,actual);
    }
}
