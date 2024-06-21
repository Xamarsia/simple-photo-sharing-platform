package com.xamarsia.simplephotosharingplatform.user;

import java.io.IOException;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.xamarsia.simplephotosharingplatform.dto.EmailUpdateRequest;
import com.xamarsia.simplephotosharingplatform.dto.UsernameUpdateRequest;
import com.xamarsia.simplephotosharingplatform.dto.user.PasswordUpdateRequest;
import com.xamarsia.simplephotosharingplatform.dto.user.UserUpdateRequest;
import com.xamarsia.simplephotosharingplatform.email.EmailVerificationService;
import com.xamarsia.simplephotosharingplatform.exception.ApplicationError;
import com.xamarsia.simplephotosharingplatform.exception.exceptions.ApplicationException;
import com.xamarsia.simplephotosharingplatform.exception.exceptions.InvalidEmailVerification;
import com.xamarsia.simplephotosharingplatform.exception.exceptions.ResourceNotFoundException;
import com.xamarsia.simplephotosharingplatform.exception.exceptions.UnauthorizedAccessException;
import com.xamarsia.simplephotosharingplatform.post.Post;
import com.xamarsia.simplephotosharingplatform.s3.S3Buckets;
import com.xamarsia.simplephotosharingplatform.s3.S3Service;

@Service
public class UserService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final S3Service s3Service;
    private final S3Buckets s3Buckets;
    private final EmailVerificationService emailVerificationService;

    public UserService(UserRepository repository, PasswordEncoder passwordEncoder, S3Service s3Service,
            S3Buckets s3Buckets, EmailVerificationService emailVerificationService) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.s3Service = s3Service;
        this.s3Buckets = s3Buckets;
        this.emailVerificationService = emailVerificationService;
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
        if (authentication instanceof AnonymousAuthenticationToken) {
            throw new UnauthorizedAccessException("[GetAuthenticatedUser]: User not authenticated.");
        }

        String username = authentication.getName();
        return findUserByUsername(username);
    }

    @Transactional(readOnly = true)
    public Page<User> getUserFollowersPage(String username, Integer pageNumber, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        User user = getUserByUsername(username);
        return repository.findUsersByFollowings(user, pageable);
    }

    @Transactional(readOnly = true)
    public Page<User> getUserFollowingsPage(String username, Integer pageNumber, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        User user = getUserByUsername(username);
        return repository.findUsersByFollowers(user, pageable);
    }

    @Transactional(readOnly = true)
    public Page<User> searchUserBySubstring(String substring, Integer pageNumber, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return repository.searchUserBySubstring(substring, pageable);
    }

    @Transactional(readOnly = true)
    public boolean isEmailUsed(String email) {
        return repository.existsUserByEmail(email);
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

    @Transactional(readOnly = true)
    public User findUserByEmail(String email) {
        return repository.findUserByEmail(email).orElseThrow(() -> new ResourceNotFoundException(
                String.format("[FindUserByEmail]: User not found with email '%s'.", email)));
    }

    public User findUserByUsername(String username) {
        return repository.findUserByUsername(username).orElseThrow(() -> new ResourceNotFoundException(
                String.format("[FindUserByUsername]: Follower not found with username '%s'.", username)));
    }

    public User follow(Authentication authentication, String username) {
        User user = getAuthenticatedUser(authentication);

        User follower = findUserByUsername(username);
        if (Objects.equals(user.getUsername(), follower.getUsername())) {
            throw new IllegalArgumentException(String.format(
                    "[Follow]: Invalid parameter. User and his follower can't have the same username '%s'.", username));
        }
        user.getFollowings().add(follower);
        saveUser(follower);
        return saveUser(user);
    }

    public User unfollow(Authentication authentication, String username) {
        User user = getAuthenticatedUser(authentication);

        User follower = findUserByUsername(username);
        if (Objects.equals(user.getUsername(), follower.getUsername())) {
            throw new IllegalArgumentException(String.format(
                    "[Unfollow]: Invalid parameter. User and his follower can't have the same username '%s'.",
                    username));
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
        user.setFullName(newUserData.getFullName());
        user.setDescription(newUserData.getDescription());
        return saveUser(user);
    }

    public User updateUserEmail(Authentication authentication, EmailUpdateRequest newEmailData) {
        User user = getAuthenticatedUser(authentication);

        String newEmail = newEmailData.getEmail();
        boolean isCodeCorrect = emailVerificationService.isVerificationCodeCorrect(newEmailData.getEmail(),
                newEmailData.getEmailVerificationCode());

        if (!isCodeCorrect) {
            throw new InvalidEmailVerification("[UpdateUserEmail]: Email verification failed");
        }

        user.setEmail(newEmail);
        return saveUser(user);
    }

    public User updateUserUsername(Authentication authentication, UsernameUpdateRequest newUsernameData) {
        User user = getAuthenticatedUser(authentication);
        String newUsername = newUsernameData.getUsername();
        user.setUsername(newUsername);
        return saveUser(user);
    }

    public User updateUserPassword(Authentication authentication, PasswordUpdateRequest passwordData) {
        User user = getAuthenticatedUser(authentication);
        String oldPassword = passwordData.getOldPassword();
        String newPassword = passwordData.getNewPassword();

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BadCredentialsException("[UpdateUserPassword]: Wrong confirmation password.");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
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

            if (e.getMessage().contains("user_email_unique")) {
                throw new ApplicationException(ApplicationError.UNIQUE_EMAIL_CONSTRAINT_FAILED,
                        String.format("[SaveUser]: User with email '%s' already exist.", user.getEmail()));
            }

            throw new ApplicationException(ApplicationError.INTERNAL_SERVER_ERROR,
                    "[SaveUser]: " + e.getMessage());
        }
    }

    public void deleteUser(Authentication authentication) {
        User user = getAuthenticatedUser(authentication);

        for (Post post : user.getPosts()) {
            s3Service.deleteObject(s3Buckets.getPostsImages(), post.getId().toString());
        }

        if (user.getIsProfileImageExist()) {
            s3Service.deleteObject(s3Buckets.getProfilesImages(), user.getId().toString());
        }
        repository.deleteById(user.getId());
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
