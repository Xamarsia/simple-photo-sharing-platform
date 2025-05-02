package com.xamarsia.simplephotosharingplatform.service;

import java.io.IOException;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.xamarsia.simplephotosharingplatform.config.s3.S3Buckets;
import com.xamarsia.simplephotosharingplatform.entity.Auth;
import com.xamarsia.simplephotosharingplatform.entity.User;
import com.xamarsia.simplephotosharingplatform.entity.PK.FollowingPK;
import com.xamarsia.simplephotosharingplatform.enums.ApplicationError;
import com.xamarsia.simplephotosharingplatform.enums.State;
import com.xamarsia.simplephotosharingplatform.exception.applicationException.ApplicationException;
import com.xamarsia.simplephotosharingplatform.exception.applicationException.ResourceNotFoundException;
import com.xamarsia.simplephotosharingplatform.repository.UserRepository;
import com.xamarsia.simplephotosharingplatform.request.user.RegisterRequest;
import com.xamarsia.simplephotosharingplatform.request.user.UserInfoUpdateRequest;
import com.xamarsia.simplephotosharingplatform.request.user.UsernameUpdateRequest;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository repository;
    private final S3Service s3Service;
    private final S3Buckets s3Buckets;
    private final AuthService authService;
    private final FollowingService followingService;

    @Transactional(readOnly = true)
    public State getState(Authentication authentication, String username) {
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return State.UNDEFINED;
        }

        User currentUser = getAuthenticatedUser(authentication);

        if (Objects.equals(currentUser.getUsername(), username)) {
            return State.CURRENT;
        }

        User follower = findUserByUsername(username);
        FollowingPK followingPK = new FollowingPK(currentUser, follower);

        if (followingService.isUserFollowedBy(followingPK)) {
            return State.FOLLOW;
        }
        return State.NONE;
    }

    @Transactional(readOnly = true)
    public User getUserByUsername(String username) {
        return findUserByUsername(username);
    }

    @Transactional(readOnly = true)
    public User getAuthenticatedUser(Authentication authentication) throws ResourceNotFoundException {
        User user = authService.getAuthentication(authentication).getUser();
        if (user == null) {
            throw new ResourceNotFoundException("[getAuthenticatedUser]: User not found.");
        }
        return user;
    }

    public User register(Authentication authentication, RegisterRequest registerRequest) {
        Auth auth = authService.getAuthentication(authentication);
        User user = User.builder()
                .fullName(registerRequest.fullName())
                .username(registerRequest.username())
                .build();

        User savedUser = saveUser(user);

        auth.setUser(savedUser);
        authService.saveAuth(auth);
        return savedUser;
    }

    @Transactional(readOnly = true)
    public Page<User> getUserFollowersPage(String username, Integer pageNumber, Integer pageSize) {
        User user = getUserByUsername(username);
        PageRequest pageable = PageRequest.of(pageNumber, pageSize);
        return repository.findFollowersByUserId(user.getId(), pageable);
    }

    @Transactional(readOnly = true)
    public Page<User> getUsersLikedPostPage(Long postId, Integer pageNumber, Integer pageSize) {
        PageRequest pageable = PageRequest.of(pageNumber, pageSize);
        return repository.findUsersLikedPostPage(postId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<User> getUserFollowingsPage(String username, Integer pageNumber, Integer pageSize) {
        User user = getUserByUsername(username);
        PageRequest pageable = PageRequest.of(pageNumber, pageSize);
        return repository.findFollowingsByUserId(user.getId(), pageable);
    }

    @Transactional(readOnly = true)
    public Page<User> searchUser(String request, Integer pageNumber, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return repository.searchUser(request, pageable);
    }

    @Transactional(readOnly = true)
    public boolean isUsernameUsed(String username) {
        return repository.existsUserByUsername(username);
    }

    @Transactional(readOnly = true)
    public boolean isRegistered(Authentication authentication) {
        User user = authService.getAuthentication(authentication).getUser();
        return user != null;
    }

    @Transactional(readOnly = true)
    public byte[] getProfileImage(String username) {
        User user = getUserByUsername(username);
        String key = user.getIsProfileImageExist() ? user.getId().toString() : "default";
        // TODO: Check if profileImage is empty or null
        return s3Service.getObject(s3Buckets.getProfilesImages(), key);
    }

    public void follow(Authentication authentication, String username) throws IllegalArgumentException {
        User user = getAuthenticatedUser(authentication);

        User follower = findUserByUsername(username);
        if (Objects.equals(user.getUsername(), follower.getUsername())) {
            throw new IllegalArgumentException(String.format(
                    "[Follow]: Invalid parameter. User and his follower can't have the same username '%s'.", username));
        }

        followingService.follow(user, follower);
        return;
    }

    public void deleteFollowing(Authentication authentication, String username) throws IllegalArgumentException {
        User user = getAuthenticatedUser(authentication);

        User follower = findUserByUsername(username);
        if (Objects.equals(user.getUsername(), follower.getUsername())) {
            throw new IllegalArgumentException(String.format(
                    "[DeleteFollowing]: Invalid parameter. User and his follower can't have the same username '%s'.",
                    username));
        }

        followingService.deleteFollowing(user, follower);
        return;
    }

    public User updateUserInfo(Authentication authentication, UserInfoUpdateRequest newUserData) {
        User user = getAuthenticatedUser(authentication);
        user.setFullName(newUserData.fullName());
        user.setDescription(newUserData.description());
        return saveUser(user);
    }

    public User updateUsername(Authentication authentication, UsernameUpdateRequest newUsernameData) {
        User user = getAuthenticatedUser(authentication);
        String newUsername = newUsernameData.username();
        user.setUsername(newUsername);
        return saveUser(user);
    }

    public void uploadProfileImage(User user, MultipartFile file)
            throws ApplicationException, IllegalArgumentException {
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

    public User saveUser(User user) throws ApplicationException, IllegalArgumentException {
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

    public void deleteUser(Authentication authentication) {
        User user = getAuthenticatedUser(authentication);

        if (user.getIsProfileImageExist()) {
            s3Service.deleteObject(s3Buckets.getProfilesImages(), user.getId().toString());
        }

        deleteUser(user);
    }

    public User deleteProfileImage(Authentication authentication) throws IllegalArgumentException {
        User user = getAuthenticatedUser(authentication);

        if (!user.getIsProfileImageExist()) {
            throw new IllegalArgumentException("[DeleteProfileImage]: Profile picture does not exist.");
        }

        s3Service.deleteObject(s3Buckets.getProfilesImages(), user.getId().toString());
        user.setIsProfileImageExist(false);
        return saveUser(user);
    }

    private User findUserByUsername(String username) throws ApplicationException {
        return repository.findUserByUsername(username).orElseThrow(() -> new ResourceNotFoundException(
                String.format("[FindUserByUsername]: User not found with username '%s'.", username)));
    }

    private void deleteUser(User user) {
        try {
            repository.deleteById(user.getId());
        } catch (Exception e) {
            throw new ApplicationException(ApplicationError.INTERNAL_SERVER_ERROR,
                    "[DeleteUser]: " + e.getMessage());
        }
    }
}
