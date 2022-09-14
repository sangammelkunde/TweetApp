package com.tweetapp.tweetapp.services;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.tweetapp.tweetapp.model.Comment;
import com.tweetapp.tweetapp.model.LoadFileVO;
import com.tweetapp.tweetapp.model.Tweets;
import com.tweetapp.tweetapp.model.UserModel;
import com.tweetapp.tweetapp.model.Users;
import com.tweetapp.tweetapp.repository.TweetRepository;
import com.tweetapp.tweetapp.repository.UserModelRepository;
import com.tweetapp.tweetapp.repository.UsersRepository;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class UsersService {
    public static final String STORE_AVATAR_PROFILE = "http://tweetapp-env.eba-ih3pr6wj.us-east-1.elasticbeanstalk.com/api/v1.0/tweets/avatar/";
    @Autowired
    private GridFsOperations operations;
    @Autowired
    private GridFsTemplate template;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private TweetRepository tweetsRepository;

    @Autowired
    private UserModelRepository userModelRepository;

    public Users storeUserDetails(Users user){
        usersRepository.save(user);
        return user;
    }

    public boolean checkEmailAndLoginId(Users user){
        return user.getEmailId().equals(user.getLoginId());
    }

    public boolean checkExistOrNot(Users user){
        return usersRepository.existsByLoginId(user.getLoginId());
    }

    public boolean checkUser(String userName,String password){
        Users tempUser = usersRepository.findByLoginId(userName);
        return  (tempUser!=null && tempUser.getLoginId().equals(userName) && tempUser.getPassword().equals(password));
    }

    public UserModel getUser(String userName, String password){
        return usersRepository.findUserByUsernameAndPassword(userName,password);
    }

    public boolean forgotPassword(String userName, String newPassword){
       Users user= usersRepository.findByLoginId(userName);
       if(user !=null){
           user.setPassword(newPassword);
           usersRepository.save(user);
           return true;
       }
       return false;
    }

    public List<UserModel> getAllUsers(){
        return userModelRepository.findAll();
    }

    public Users getByUserName(String userName){
        return usersRepository.findByLoginId(userName);
    }


    public UserModel getDetailsOfUser(String userName){
        return userModelRepository.findByLoginId(userName);
    }

    public String addFile(String id, MultipartFile file) throws IOException {
        DBObject metadata = new BasicDBObject();
        metadata.put("entity","Host");
        metadata.put("fileSize",file.getSize());
        metadata.put("_contentType", file.getContentType());
        metadata.put("id",id);
        metadata.put("createDate", LocalDateTime.now());
        try {
            Users user=  usersRepository.findByLoginId(id);
            List<Tweets> tweets=tweetsRepository.findAll();
            String imageId= template.store(file.getInputStream(), file.getOriginalFilename(), file.getContentType(), metadata).toString();
            user.setImageUrl(STORE_AVATAR_PROFILE +imageId);
            for (Tweets i:tweets) {
                if(i.getUsername().equals(id)){
                    i.setImageUrl(STORE_AVATAR_PROFILE+imageId);
                }
                List<Comment> comments=i.getComments();
                for (Comment comment:comments) {
                    if(comment.getUsername().equals(id)){
                        comment.setImageUrl(STORE_AVATAR_PROFILE+imageId);
                    }

                }
                i.setComments(comments);
                tweetsRepository.save(i);
            }
            usersRepository.save(user);
            return imageId;
        }
        catch (IOException ex)
        {
            throw new RuntimeException();
        }
    }

    public LoadFileVO getHostFile(String id ) throws IOException {
        GridFSFile gridFSFile = template.findOne( new Query(Criteria.where("_id").is(id)) );
        LoadFileVO loadFile=new LoadFileVO();
        if (gridFSFile != null && gridFSFile.getMetadata() != null) {
            loadFile.setFilename( gridFSFile.getFilename() );
            loadFile.setFileType( gridFSFile.getMetadata().get("_contentType").toString() );
            loadFile.setFileSize( gridFSFile.getMetadata().get("fileSize").toString() );
            loadFile.setFile( IOUtils.toByteArray(operations.getResource(gridFSFile).getInputStream()) );
        }
        return loadFile;
    }

}
