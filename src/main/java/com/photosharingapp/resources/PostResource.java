package com.photosharingapp.resources;

import com.photosharingapp.model.AppUser;
import com.photosharingapp.model.Post;
import com.photosharingapp.service.AccountService;
import com.photosharingapp.service.CommentService;
import com.photosharingapp.service.PostService;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/post")
public class PostResource {

    private String postImageName;
    @Autowired
    private PostService postService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private CommentService commentService;

    @GetMapping("/list")
    public List<Post> getPosts() {
        return postService.postList();
    }

    @GetMapping("/getPostById/{postId}")
    public ResponseEntity<?> getOnePostById(@PathVariable("postId") Long id) {
        Post post = postService.getPostById(id);
        if(post == null) {
            return new ResponseEntity<>("No Post Found", HttpStatus.OK);
        }
        return new ResponseEntity<>(post, HttpStatus.OK);
    }

    @GetMapping("/getPostByUsername/{username}")
    public ResponseEntity<?> getPostByUsername(@PathVariable("username") String username){
        AppUser user = accountService.findByUsername(username);
        if(user == null){
            return new ResponseEntity<>("User Not Found",HttpStatus.NOT_FOUND);
        }
        try{
            List<Post> posts = postService.findPostByUsername(username);
            return new ResponseEntity<>(posts,HttpStatus.OK);
        } catch(Exception e) {
            return new ResponseEntity<>("An error occurred", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/save")
    public ResponseEntity<?> savePost(@RequestBody HashMap<String, String> request) {
        String username = request.get("username");
        AppUser user = accountService.findByUsername(username);

        if(user == null) {
            return new ResponseEntity<>("User Not Found", HttpStatus.NOT_FOUND);
        }
        postImageName = RandomStringUtils.randomAlphabetic(10);
        try{
            Post post = postService.savePost(user, request, postImageName);
            return new ResponseEntity<>(post, HttpStatus.CREATED);
        } catch(Exception e){
            return new ResponseEntity<>("An error occurred", HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deletePost(@PathVariable("id") Long id) {
        Post post = postService.getPostById(id);
        if(post == null) {
            return new ResponseEntity<>("Post Not Found", HttpStatus.NOT_FOUND);
        }
        try {
            postService.deletePost(post);
            return new ResponseEntity<>(post, HttpStatus.OK);
        } catch(Exception e) {
            return new ResponseEntity<>("An error occurred", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/like")
    public ResponseEntity<String> likePost(@RequestBody HashMap<String, String> request) {
        String postId = request.get("postId");
        Post post = postService.getPostById(Long.parseLong(postId));
        if(post == null) {
            return new ResponseEntity<>("Post not Found", HttpStatus.NOT_FOUND);
        }
        String username = request.get("username");
        AppUser user = accountService.findByUsername(username);
        if(user == null) {
            return new ResponseEntity<>("User not Found", HttpStatus.NOT_FOUND);
        }
        try {
            post.setLikes(1);
            user.setLikedPost(post);
            accountService.simpleSave(user);
            return new ResponseEntity<>("Post was Liked", HttpStatus.OK);
        } catch(Exception e) {
            return new ResponseEntity<>("Can't like the post",HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/unLike")
    public ResponseEntity<String> unLikePost(@RequestBody HashMap<String, String> request) {
        String postId = request.get("postId");
        Post post = postService.getPostById(Long.parseLong(postId));
        if(post == null){
            return new ResponseEntity<>("Post not Found",HttpStatus.NOT_FOUND);
        }
        String username = request.get("username");
        AppUser user = accountService.findByUsername(username);
        if(user == null){
            return new ResponseEntity<>("User Not Found",HttpStatus.NOT_FOUND);
        }
        try {
            post.setLikes(-1);
            user.getLikedPost().remove(post);
            accountService.simpleSave(user);
            return new ResponseEntity<>("Post was unliked",HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Can't unlike the post",HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/comment/add")
    public ResponseEntity<?> addComment(@RequestBody HashMap<String, String> request) {
        String postId = request.get("postId");
        Post post = postService.getPostById(Long.parseLong(postId));
        if(post == null) {
            return new ResponseEntity<>("Post not Found",HttpStatus.NOT_FOUND);
        }
        String username = request.get("username");
        AppUser user = accountService.findByUsername(username);
        if(user == null) {
            return new ResponseEntity<>("User not Found",HttpStatus.NOT_FOUND);
        }
        String content = request.get("content");
        try{
//            Comment comment = new Comment();
////            comment.setContent(content);
////            comment.setUsername(username);
////            comment.setPostedDate(new Date());
////            post.setCommentList(comment);
////            commentService.saveComment(comment);
            commentService.saveComment(post, username, content);
            return new ResponseEntity<>(post,HttpStatus.OK);
        } catch(Exception e) {
            return new ResponseEntity<>("Comment not added",HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/photo/upload")
    //Replace HttpServletRequest httpServletRequest with MultipartFile
    public ResponseEntity<String> fileUpload(@RequestParam("image") MultipartFile multipartFile) {
        try{
            postService.savePostImage(multipartFile, postImageName);
            return new ResponseEntity<>("Picture saved!",HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Picture Not saved!",HttpStatus.BAD_REQUEST);
        }
    }
}
