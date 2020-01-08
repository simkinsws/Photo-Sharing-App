package com.photosharingapp.service;

import com.photosharingapp.model.AppUser;
import com.photosharingapp.model.Role;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;

public interface AccountService {

    public AppUser saveUser(String name, String username, String email);
    public AppUser findByUsername(String username);
    public AppUser findByEmail(String userEmail);
    public List<AppUser> userList();
    public Role findUserRoleByName(String role);
    public Role saveRole(Role role);
    public AppUser updateUser(AppUser appUser, HashMap<String, String> request);
    public AppUser findUserById(Long id);
    public void deleteUser(AppUser appUser);
    public void resetPassword(AppUser appUser);
    public List<AppUser> getUserListByUsername(String username);
    public AppUser simpleSave(AppUser appUser);
//    public String saveUserImage(HttpServletRequest request, Long userImageId);
    public String saveUserImage(MultipartFile multipartFile, Long userImageId);
    public void updateUserPassword(AppUser appUser, String newPassword);









}
