package com.xamarsia.simplephotosharingplatform.user;

import com.xamarsia.simplephotosharingplatform.dto.user.PasswordUpdateRequest;
import com.xamarsia.simplephotosharingplatform.dto.user.UserUpdateRequest;
import com.xamarsia.simplephotosharingplatform.s3.S3Buckets;
import com.xamarsia.simplephotosharingplatform.s3.S3Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
public class UserService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final S3Service s3Service;
    private final S3Buckets s3Buckets;

    public UserService(UserRepository repository, PasswordEncoder passwordEncoder, S3Service s3Service, S3Buckets s3Buckets) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.s3Service = s3Service;
        this.s3Buckets = s3Buckets;
    }

    @Transactional(readOnly = true)
    public User getByEmail(String email) {
        return repository.findUserByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email " + email));
    }

    @Transactional(readOnly = true)
    public State getState(Authentication authentication, String username) {
        User currentUser = getAuthenticatedUser(authentication);
        if (Objects.equals(currentUser.getUsername(), username)) {
            return State.CURRENT;
        }
        if (isUserInFollowing(currentUser, username)) {
            return State.FOLLOWED;
        }
        return State.UNFOLLOWED;
    }

    @Transactional(readOnly = true)
    public State getState(Authentication authentication, User user) {
        User currentUser = getAuthenticatedUser(authentication);
        if (Objects.equals(currentUser.getUsername(), user.getUsername())) {
            return State.CURRENT;
        }
        if (isUserInFollowing(currentUser, user.getUsername())) {
            return State.FOLLOWED;
        }
        return State.UNFOLLOWED;
    }

    @Transactional(readOnly = true)
    public User getByUsername(String username) {
        return repository.findUserByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username " + username));
    }

    @Transactional(readOnly = true)
    public User getById(Long customerId) {
        return selectUserById(customerId)
                .orElseThrow(() -> new RuntimeException("User not found with id " + customerId));
    }

    public User getAuthenticatedUser(Authentication authentication) {
        if (authentication instanceof AnonymousAuthenticationToken) {
            throw new RuntimeException("Authenticated user not found");
        }

        String name = authentication.getName();
        return findUserByUsername(name)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with name " + name));
    }

    @Transactional(readOnly = true)
    public Page<User> getUserFollowersPage(String username,
                                           Integer pageNumber,
                                           Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        User user = getByUsername(username);
        return repository.findUsersByFollowings(user, pageable);
    }

    @Transactional(readOnly = true)
    public Page<User> getUserFollowingsPage(String username,
                                            Integer pageNumber,
                                            Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        User user = getByUsername(username);
        return repository.findUsersByFollowers(user, pageable);
    }

    public boolean isEmailUsed(String email) {
        return repository.existsUserByEmail(email);
    }

    public boolean isUsernameUsed(String username) {
        return repository.existsUserByUsername(username);
    }

    public boolean isUserWithIdExist(Long id) {
        return repository.existsUserById(id);
    }

    public List<User> selectAllUsers() {
        Page<User> page = repository.findAll(Pageable.ofSize(1000));
        return page.getContent();
    }

    public Optional<User> selectUserById(Long id) {
        return repository.findById(id);
    }

    public User saveUser(User user) {
        return repository.save(user);
    }

    public void deleteUser(Authentication authentication) {
        User user = getAuthenticatedUser(authentication);
        if (user.getIsProfileImageExist()) {
            s3Service.deleteObject(s3Buckets.getProfilesImages(), user.getId().toString());
        }
        repository.deleteById(user.getId());
    }

    public User updateUserPassword(Authentication authentication,
                                   PasswordUpdateRequest passwordData) {
        User user = getAuthenticatedUser(authentication);
        String oldPassword = passwordData.getOldPassword();
        String newPassword = passwordData.getNewPassword();

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("Update user password: Wrong confirmation password!");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        return saveUser(user);
    }

    public User follow(Authentication authentication, String username) {
        User user = getAuthenticatedUser(authentication);

        User follower = repository.findUserByUsername(username)
                .orElseThrow(() -> new RuntimeException("[Follow]: Follower not found with username " + username));
        if (Objects.equals(user.getUsername(), follower.getUsername())) {
            throw new RuntimeException("[Follow]: Invalid parameter. User and his follower can't have the same username: " + username);
        }
        user.getFollowings().add(follower);
        saveUser(follower);
        return saveUser(user);
    }

    public User unfollow(Authentication authentication, String username) {
        User user = getAuthenticatedUser(authentication);

        User follower = repository.findUserByUsername(username)
                .orElseThrow(() -> new RuntimeException("[Unfollow]: Follower not found with username " + username));
        if (Objects.equals(user.getUsername(), follower.getUsername())) {
            throw new RuntimeException("[Unfollow]: Invalid parameter. User and his follower can't have the same username: " + username);
        }
        user.getFollowings().remove(follower);
        saveUser(follower);
        return saveUser(user);
    }

    public Boolean isUserInFollowing(Authentication authentication, String username) {
        User user = getAuthenticatedUser(authentication);
        return isUserInFollowing(user, username);
    }

    public Boolean isUserInFollowing(User currentUser, String username) {
        return currentUser.getFollowings().stream().map(User::getUsername).anyMatch(username::equals);
    }

    public User updateUser(Authentication authentication, UserUpdateRequest newUserData) {
        User user = getAuthenticatedUser(authentication);

        String newUsername = newUserData.getUsername();
        String newEmail = newUserData.getEmail();

        if (!Objects.equals(user.getEmail(), newEmail) && repository.existsUserByEmail(newEmail)) {
            throw new RuntimeException("Update user: User with this email already exist! " + newEmail);
        }

        if (!Objects.equals(user.getUsername(), newUsername) && repository.existsUserByUsername(newUsername)) {
            throw new RuntimeException("Update user: User with this username already exist! " + newUsername);
        }

        user.setFullName(newUserData.getFullName());
        user.setUsername(newUserData.getUsername());
        user.setEmail(newUserData.getEmail());

        return saveUser(user);
    }

    public Optional<User> findUserByUsername(String username) {
        return repository.findUserByUsername(username);
    }

    public Optional<User> selectUserByEmail(String email) {
        return repository.findUserByEmail(email);
    }

    public void uploadProfileImage(User user, MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("[UploadProfileImage]: File is empty. Cannot save an empty file");
        }
        String extension = Objects.requireNonNull(file.getOriginalFilename()).split("\\.")[1];
        if (!(Objects.equals(extension, "jpg") || Objects.equals(extension, "jpeg "))) {
            throw new RuntimeException("[UploadProfileImage]: Wrong file extension found: " + extension
                    + ". Only .jpg and .jpeg files are allowed.");
        }
        try {
            s3Service.putObject(s3Buckets.getProfilesImages(),
                    user.getId().toString(),
                    file.getBytes()
            );
        } catch (IOException e) {
            throw new RuntimeException("[UploadProfileImage]: " + e.getMessage());
        }

        user.setIsProfileImageExist(true);
        saveUser(user);
    }

    @Transactional(readOnly = true)
    public byte[] getProfileImage(String username) {
        User user = getByUsername(username);
        String key = user.getIsProfileImageExist() ? user.getId().toString() : "default";
        //TODO: Check if profileImage is empty or null
        return s3Service.getObject(s3Buckets.getProfilesImages(),
                key);
    }
}
