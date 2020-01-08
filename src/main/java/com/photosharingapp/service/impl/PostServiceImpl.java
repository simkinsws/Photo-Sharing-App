package com.photosharingapp.service.impl;

import com.photosharingapp.model.AppUser;
import com.photosharingapp.model.Post;
import com.photosharingapp.repository.PostRepository;
import com.photosharingapp.service.PostService;
import com.photosharingapp.utility.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Service
@Transactional
public class PostServiceImpl implements PostService {

    @Autowired
    private PostRepository postRepository;

    @Override
    public Post savePost(AppUser appUser, HashMap<String, String> request, String postImageName) {
        String caption = request.get("caption");
        String location = request.get("location");
        Post post = new Post();
        post.setCaption(caption);
        post.setLocation(location);
        post.setUsername(appUser.getUsername());
        post.setPostedDate(new Date());
        post.setUserImageId(appUser.getId());
        appUser.setPost(post);
        postRepository.save(post);
        return post;
    }

    @Override
    public List<Post> postList() {
        return postRepository.findAll();
    }

    @Override
    public Post getPostById(Long id) {
        return postRepository.findPostById(id);
    }

    @Override
    public List<Post> findPostByUsername(String username) {
        return postRepository.findByUsername(username);
    }

    @Override
    public Post deletePost(Post post) {
        try{
            Files.deleteIfExists(Paths.get(Constants.POST_FOLDER + "/"
                    + post.getName() + ".png"));
            postRepository.deletePostById(post.getId());
            return post;
        } catch(Exception e) {
        }
        return null;
    }

    @Override
    //replaced HttpServletRequest request with MultipartFile
    public String savePostImage(MultipartFile multipartFile, String fileName) {
//        MultipartHttpServletRequest multipartHttpServletRequest =
//                (MultipartHttpServletRequest) request;
//        Iterator<String> it = multipartHttpServletRequest.getFileNames();
//        MultipartFile multipartFile = multipartHttpServletRequest.getFile(it.next());
        try{
            byte[] bytes = multipartFile.getBytes();
            Path path = Paths.get(Constants.POST_FOLDER + fileName + ".png");
            Files.write(path, bytes, StandardOpenOption.CREATE);
        } catch (IOException e) {
            System.out.println("Error occurred. photo not saved");
            return "Error occurred. photo not saved";
        }
        System.out.println("Photo saved successfully");
        return "Photo saved successfully";
    }
}
