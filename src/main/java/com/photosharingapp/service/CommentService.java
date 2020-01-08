package com.photosharingapp.service;

import com.photosharingapp.model.Post;

public interface CommentService {
//    public void saveComment(Comment comment);
    public void saveComment(Post post, String username, String content);
}
