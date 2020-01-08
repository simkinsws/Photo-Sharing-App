package com.photosharingapp.resources;

import com.photosharingapp.model.AppUser;
import com.photosharingapp.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/user")
public class AccountResource {

    private Long userImageId;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private AccountService accountService;

    @GetMapping("/list")
    public ResponseEntity<?> getUserList() {
        List<AppUser> users = accountService.userList();
        if (users.isEmpty()) {
            return new ResponseEntity<>("No Users Found", HttpStatus.OK);
        }
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/{username}")
    public ResponseEntity<?> getUserInfo(@PathVariable("username") String username) {
        AppUser user = accountService.findByUsername(username);
        if (user == null) {
            return new ResponseEntity<>("No User Found", HttpStatus.OK);
        }
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("/findByUsername/{username}")
    public ResponseEntity<?> getUsersListByUsername(@PathVariable("username") String username) {
        List<AppUser> users = accountService.getUserListByUsername(username);
        if (users.isEmpty()) {
            return new ResponseEntity<>("No Users Found", HttpStatus.OK);
        }
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody HashMap<String, String> request) {
        String username = request.get("username");
        if (accountService.findByUsername(username) != null) {
            return new ResponseEntity<>("usernameExist", HttpStatus.CONFLICT);
        }
        String email = request.get("email");
        if (accountService.findByEmail(email) != null) {
            return new ResponseEntity<>("emailExist", HttpStatus.CONFLICT);
        }
        String name = request.get("name");
        try {
            AppUser user = accountService.saveUser(name, username, email);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("An Error Occurred "+ e.toString(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateProfile(@RequestBody HashMap<String, String> request) {
        String id = request.get("id");
        AppUser user = accountService.findUserById(Long.parseLong(id));
        if (user == null) {
            return new ResponseEntity<>("userNotFound", HttpStatus.NOT_FOUND);
        }
        try {
            accountService.updateUser(user, request);
            userImageId = user.getId();
            return new ResponseEntity<>(user, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>("An Error occurred", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/photo/upload")
    //replaced HttpServletRequest httpServletRequest with MultipartFile
    public ResponseEntity<String> fileUpload(@RequestParam("image") MultipartFile multipartFile) {
        try {
            accountService.saveUserImage(multipartFile, userImageId);
            return new ResponseEntity<>("User Picture Saved", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("User Picture Not Saved!", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/changePassword")
    public ResponseEntity<String> changePassword(@RequestBody HashMap<String, String> request) {
        String username = request.get("username");
        AppUser appUser = accountService.findByUsername(username);
        if (appUser == null) {
            return new ResponseEntity<>("User Not Found", HttpStatus.BAD_REQUEST);
        }
        String currentPassword = request.get("currentPassword");
        String newPassword = request.get("newPassword");
        String confirmPassword = request.get("confirmPassword");
        if (!newPassword.equals(confirmPassword)) {
            return new ResponseEntity<>("PasswordNotMatched", HttpStatus.BAD_REQUEST);
        }
        String userPassword = appUser.getPassword();
        try {
            if (newPassword != null && !newPassword.isEmpty() && !StringUtils.isEmpty(newPassword)) {
                if (bCryptPasswordEncoder.matches(currentPassword, userPassword)) {
                    accountService.updateUserPassword(appUser, newPassword);
                }
                } else {
                    return new ResponseEntity<>("IncorrectCurrentPassword", HttpStatus.BAD_REQUEST);
                }
            return new ResponseEntity<>("Password changed successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error Occurred", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/resetPassword/{email}")
    public ResponseEntity<String> resetPassword(@PathVariable("email") String email) {
        AppUser appUser = accountService.findByEmail(email);
        if(appUser == null) {
            return new ResponseEntity<>("User Not Found", HttpStatus.BAD_REQUEST);
        }
        accountService.resetPassword(appUser);
        return new ResponseEntity<>("email sent", HttpStatus.OK);
    }
}
