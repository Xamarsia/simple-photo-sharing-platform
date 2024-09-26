package com.xamarsia.simplephotosharingplatform.user;

import java.io.IOException;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.xamarsia.simplephotosharingplatform.exception.ApplicationError;
import com.xamarsia.simplephotosharingplatform.exception.exceptions.ApplicationException;

import com.xamarsia.simplephotosharingplatform.exception.exceptions.ResourceNotFoundException;
import com.xamarsia.simplephotosharingplatform.requests.user.RegisterRequest;
import com.xamarsia.simplephotosharingplatform.requests.user.UserInfoUpdateRequest;
import com.xamarsia.simplephotosharingplatform.requests.user.UsernameUpdateRequest;
import com.xamarsia.simplephotosharingplatform.s3.S3Buckets;
import com.xamarsia.simplephotosharingplatform.s3.S3Service;
import com.xamarsia.simplephotosharingplatform.security.authentication.Auth;
import com.xamarsia.simplephotosharingplatform.security.authentication.AuthService;
import com.xamarsia.simplephotosharingplatform.user.following.FollowingService;

@Service
public class UserService {
    private final UserRepository repository;
    private final S3Service s3Service;
    private final S3Buckets s3Buckets;
    private final AuthService authService;
    private final FollowingService followingService;

    public UserService(UserRepository repository, S3Service s3Service,
            S3Buckets s3Buckets, AuthService authService, FollowingService followingService) {
        this.repository = repository;
        this.s3Service = s3Service;
        this.s3Buckets = s3Buckets;
        this.authService = authService;
        this.followingService = followingService;
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
    public User getUserByUsername(String username) {
        return findUserByUsername(username);
    }

    @Transactional(readOnly = true)
    public User getById(Long customerId) {
        return repository.findById(customerId).orElseThrow(() -> new ResourceNotFoundException(
                String.format("[GetUserByID]: User not found with id '%s'.", customerId)));
    }

    @Transactional(readOnly = true)
    public User getAuthenticatedUser(Authentication authentication) {
        User user = authService.getAuthentication(authentication).getUser();
        if (user == null) {
            throw new ResourceNotFoundException("[getAuthenticatedUser]: User not found.");
        }
        return user;
    }

    public User register(Authentication authentication, RegisterRequest registerRequest) {
        Auth auth = authService.getAuthentication(authentication);
        User user = User.builder()
                .fullName(registerRequest.getFullName())
                .username(registerRequest.getUsername())
                .build();

        User savedUser = saveUser(user);

        auth.setUser(savedUser);
        authService.saveAuthentication(auth);
        return savedUser;
    }

    @Transactional(readOnly = true)
    public Page<User> getUserFollowersPage(String username, Integer pageNumber, Integer pageSize) {
        User user = getUserByUsername(username);
        PageRequest pageable = PageRequest.of(pageNumber, pageSize);
        return repository.findFollowersByUserId(user.getId(), pageable);
    }

    @Transactional(readOnly = true)
    public Page<User> getPostLikersPage(Long postId, Integer pageNumber, Integer pageSize) {
        PageRequest pageable = PageRequest.of(pageNumber, pageSize);
        return repository.findPostLikersByPostId(postId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<User> getUserFollowingsPage(String username, Integer pageNumber, Integer pageSize) {
        User user = getUserByUsername(username);
        PageRequest pageable = PageRequest.of(pageNumber, pageSize);
        return repository.findFollowingsByUserId(user.getId(), pageable);
    }

    @Transactional(readOnly = true)
    public Page<User> searchUserBySubstring(String substring, Integer pageNumber, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return repository.searchUserBySubstring(substring, pageable);
    }

    @Transactional(readOnly = true)
    public boolean isUsernameUsed(String username) {
        return repository.existsUserByUsername(username);
    }

    @Transactional(readOnly = true)
    public byte[] getProfileImage(String username) {
        User user = getUserByUsername(username);
        String key = user.getIsProfileImageExist() ? user.getId().toString() : "default";
        // TODO: Check if profileImage is empty or null
        return s3Service.getObject(s3Buckets.getProfilesImages(), key);
    }

    public User findUserByUsername(String username) {
        return repository.findUserByUsername(username).orElseThrow(() -> new ResourceNotFoundException(
                String.format("[FindUserByUsername]: Follower not found with username '%s'.", username)));
    }

    public void follow(Authentication authentication, String username) {
        User user = getAuthenticatedUser(authentication);

        User follower = findUserByUsername(username);
        if (Objects.equals(user.getUsername(), follower.getUsername())) {
            throw new IllegalArgumentException(String.format(
                    "[Follow]: Invalid parameter. User and his follower can't have the same username '%s'.", username));
        }

        followingService.follow(user, follower);
        return;
    }

    public void unfollow(Authentication authentication, String username) {
        User user = getAuthenticatedUser(authentication);

        User follower = findUserByUsername(username);
        if (Objects.equals(user.getUsername(), follower.getUsername())) {
            throw new IllegalArgumentException(String.format(
                    "[Unfollow]: Invalid parameter. User and his follower can't have the same username '%s'.",
                    username));
        }

        followingService.ufollow(user.getId(), follower.getId());
        return;
    }

    @Transactional(readOnly = true)
    public Boolean isUserInFollowing(Authentication authentication, String username) {
        User user = getAuthenticatedUser(authentication);
        return isUserInFollowing(user, username);
    }

    @Transactional(readOnly = true)
    public Boolean isUserInFollowing(User currentUser, String username) {
        User follower = findUserByUsername(username);
        return followingService.isUserInFollowing(currentUser.getId(), follower.getId());
    }

    public User updateUser(Authentication authentication, UserInfoUpdateRequest newUserData) {
        User user = getAuthenticatedUser(authentication);
        user.setFullName(newUserData.getFullName());
        user.setDescription(newUserData.getDescription());
        return saveUser(user);
    }

    public User updateUserUsername(Authentication authentication, UsernameUpdateRequest newUsernameData) {
        User user = getAuthenticatedUser(authentication);
        String newUsername = newUsernameData.getUsername();
        user.setUsername(newUsername);
        return saveUser(user);
    }

    public void uploadProfileImage(User user, MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("[UploadProfileImage]: File is empty. Cannot save an empty file");
        }
        String extension = Objects.requireNonNull(file.getOriginalFilename()).split("\\.")[1];
        if (!(Objects.equals(extension, "jpg") || Objects.equals(extension, "jpeg "))) {
            throw new IllegalArgumentException(String.format("[UploadProfileImage]: Wrong file extension '%s' found. "
                    + "Only .jpg and .jpeg files are allowed.", extension));
        }
        try {
            s3Service.putObject(s3Buckets.getProfilesImages(), user.getId().toString(), file.getBytes());
        } catch (IOException e) {
            throw new ApplicationException(ApplicationError.AWS_S3_ERROR, "[UploadProfileImage]: " + e.getMessage());
        }

        user.setIsProfileImageExist(true);
        saveUser(user);
    }

    public User saveUser(User user) throws IllegalArgumentException {
        try {
            return repository.save(user);
        } catch (Exception e) {
            if (e.getMessage().contains("user_username_unique")) {
                throw new ApplicationException(ApplicationError.UNIQUE_USERNAME_CONSTRAINT_FAILED,
                        String.format("[SaveUser]: User with username '%s' already exist.", user.getUsername()));
            }

            throw new ApplicationException(ApplicationError.INTERNAL_SERVER_ERROR,
                    "[SaveUser]: " + e.getMessage());
        }
    }

    public void deleteUser(User user) {
        try {
            repository.deleteById(user.getId());
        } catch (Exception e) {
            throw new ApplicationException(ApplicationError.INTERNAL_SERVER_ERROR,
                    "[DeleteUser]: " + e.getMessage());
        }
    }

    public void deleteUser(Authentication authentication) {
        User user = getAuthenticatedUser(authentication);

        if (user.getIsProfileImageExist()) {
            s3Service.deleteObject(s3Buckets.getProfilesImages(), user.getId().toString());
        }

        deleteUser(user);
    }

    public User deleteProfileImage(Authentication authentication) {
        User user = getAuthenticatedUser(authentication);

        if (!user.getIsProfileImageExist()) {
            throw new IllegalArgumentException("[DeleteProfileImage]: Profile picture does not exist.");
        }

        s3Service.deleteObject(s3Buckets.getProfilesImages(), user.getId().toString());
        user.setIsProfileImageExist(false);
        return saveUser(user);
    }
}
