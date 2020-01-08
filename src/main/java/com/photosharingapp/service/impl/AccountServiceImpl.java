package com.photosharingapp.service.impl;

import com.photosharingapp.model.AppUser;
import com.photosharingapp.model.Role;
import com.photosharingapp.model.UserRole;
import com.photosharingapp.repository.AppUserRepository;
import com.photosharingapp.repository.RoleRepository;
import com.photosharingapp.service.AccountService;
import com.photosharingapp.utility.Constants;
import com.photosharingapp.utility.EmailConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class AccountServiceImpl implements AccountService {

    @Autowired
    AccountService accountService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private EmailConstructor emailConstructor;

    @Autowired
    private JavaMailSender mailSender;

//    @Override
//    public void saveUser(AppUser appUser) {
//        String password = RandomStringUtils.randomAlphanumeric(10);
////        String encryptedPassword = bCryptPasswordEncoder.encode(password);
////        appUser.setPassword(encryptedPassword);
////         appUserRepository.save(appUser);
////         mailSender.send(emailConstructor.constructNewUserEmail(appUser, password));
////    }

    @Override
    @Transactional
    public AppUser saveUser(String name, String username, String email) {
        String password = RandomStringUtils.randomAlphanumeric(10);
        String encryptedPassword = bCryptPasswordEncoder.encode(password);
        AppUser appUser = new AppUser();
        appUser.setPassword(encryptedPassword);
        appUser.setName(name);
        appUser.setUsername(username);
        appUser.setEmail(email);
        Set<UserRole> userRoles = new HashSet<>();
        userRoles.add(new UserRole(appUser, accountService.findUserRoleByName("USER")));
        appUser.setUserRoles(userRoles);
        appUserRepository.save(appUser);
        byte[] bytes;
        try{
            bytes = Files.readAllBytes(Constants.TEMP_USER.toPath());
            String filename = appUser.getId() + ".png";
            Path path = Paths.get(Constants.USER_FOLDER + filename);
            Files.write(path, bytes);
        } catch(IOException e) {
            e.printStackTrace();
        }
        mailSender.send(emailConstructor.constructNewUserEmail(appUser, password));
        return appUser;
    }

    @Override
    public AppUser findByUsername(String username) {
       return appUserRepository.findByUsername(username);
    }

    @Override
    public AppUser findByEmail(String email) {
        return appUserRepository.findByEmail(email);
    }

    @Override
    public List<AppUser> userList() {
        return appUserRepository.findAll();
    }

    @Override
    public Role findUserRoleByName(String name) {
        return roleRepository.findRoleByName(name);
    }

    @Override
    public Role saveRole(Role role) {
        return roleRepository.save(role);
    }

    @Override
    public AppUser updateUser(AppUser appUser, HashMap<String, String> request) {
        String name = request.get("name");
        String email = request.get("email");
        String bio = request.get("bio");
        appUser.setName(name);
        appUser.setEmail(email);
        appUser.setBio(bio);
        appUserRepository.save(appUser);
        mailSender.send(emailConstructor.constructUpdateUserProfileEmail(appUser));
        return appUser;
    }

    @Override
    public AppUser findUserById(Long id) {
        return appUserRepository.findUserById(id);
    }

    @Override
    public void deleteUser(AppUser appUser) {
        appUserRepository.delete(appUser);
    }

    @Override
    public void resetPassword(AppUser appUser) {
        String password = RandomStringUtils.randomAlphanumeric(10);
        String encryptedPassword = bCryptPasswordEncoder.encode(password);
        appUser.setPassword(encryptedPassword);
        appUserRepository.save(appUser);
        mailSender.send(emailConstructor.constructResetPasswordEmail(appUser, password));
    }

    @Override
    public List<AppUser> getUserListByUsername(String username) {
        return appUserRepository.findByUsernameContaining(username);
    }

    @Override
    public AppUser simpleSave(AppUser appUser) {
        appUserRepository.save(appUser);
        mailSender.send(emailConstructor.constructUpdateUserProfileEmail(appUser));
        return appUser;
    }

    @Override
    //HttpServletRequest request replaced MultipartFile
    public String saveUserImage(MultipartFile multipartFile, Long userImageId) {
//        MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
//        Iterator<String> it = multipartHttpServletRequest.getFileNames();
//        MultipartFile multipartFile = multipartHttpServletRequest.getFile(it.next());
        byte[] bytes;
        try{
            Files.deleteIfExists(Paths.get(Constants.USER_FOLDER + "/" + userImageId + ".png"));
            bytes = multipartFile.getBytes();
            Path path = Paths.get(Constants.USER_FOLDER + userImageId + ".png");
            Files.write(path, bytes);
            return "User picture saved to server";
        } catch(IOException e) {
            return "User picture Saved";
        }
    }

    @Override
    public void updateUserPassword(AppUser appUser, String newPassword) {
        String encryptedPassword = bCryptPasswordEncoder.encode(newPassword);
        appUser.setPassword(encryptedPassword);
        appUserRepository.save(appUser);
        mailSender.send(emailConstructor.constructResetPasswordEmail(appUser, newPassword));
    }
}
