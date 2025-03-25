package com.xamarsia.simplephotosharingplatform.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.xamarsia.simplephotosharingplatform.common.EmptyJson;
import com.xamarsia.simplephotosharingplatform.dto.mapper.user.ProfileDTOMapper;
import com.xamarsia.simplephotosharingplatform.dto.mapper.user.UserDTOMapper;
import com.xamarsia.simplephotosharingplatform.dto.user.ProfileDTO;
import com.xamarsia.simplephotosharingplatform.dto.user.UserDTO;
import com.xamarsia.simplephotosharingplatform.entity.Auth;
import com.xamarsia.simplephotosharingplatform.entity.User;
import com.xamarsia.simplephotosharingplatform.request.user.RegisterRequest;
import com.xamarsia.simplephotosharingplatform.request.user.UserInfoUpdateRequest;
import com.xamarsia.simplephotosharingplatform.request.user.UsernameUpdateRequest;
import com.xamarsia.simplephotosharingplatform.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * @brief Controller for managing user-related operations.
 * 
 *        This controller handles endpoints for user authentication,
 *        registration,
 *        and profile management.
 */
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService service;
    private final UserDTOMapper userDTOMapper;
    private final ProfileDTOMapper profileDTOMapper;

    /**
     * @brief Retrieve the authenticated user's information.
     * 
     *        Handles GET requests to the endpoint: `/user`
     * 
     * @param authentication The {@link Authentication} object.
     * @return A {@link ResponseEntity} containing the {@link UserDTO} of the
     *         authenticated user.
     */
    @GetMapping("")
    public ResponseEntity<UserDTO> getAuthenticatedUser(Authentication authentication) {
        UserDTO userDTO = userDTOMapper.apply(authentication, service.getAuthenticatedUser(authentication));
        return ResponseEntity.ok().body(userDTO);
    }

    /**
     * @brief Check if a username is already in use.
     * 
     *        Handles GET requests to the endpoint:
     *        `/user/isUsernameUsed/{username}`
     * 
     * @param username The username to check.
     * @return A {@link ResponseEntity} containing a boolean indicating if the
     *         username is used.
     */
    @GetMapping("/isUsernameUsed/{username}")
    public ResponseEntity<Boolean> isUsernameUsed(@PathVariable String username) {
        Boolean isUsernameUsed = service.isUsernameUsed(username);
        return ResponseEntity.ok().body(isUsernameUsed);
    }

    /**
     * @brief Check if the authenticated user is registered.
     * 
     *        Handles GET requests to the endpoint: `/user/isRegistered`
     * 
     * @param authentication The {@link Authentication} object.
     * @return A {@link ResponseEntity} containing a boolean indicating if the user
     *         is registered.
     */
    @GetMapping("/isRegistered")
    public ResponseEntity<Boolean> isRegistered(Authentication authentication) {
        Boolean isRegistered = service.isRegistered(authentication);
        return ResponseEntity.ok().body(isRegistered);
    }

    /**
     * @brief Register a new user.
     * 
     *        Handles POST requests to the endpoint: `/user/register`
     * 
     * @param authentication The {@link Authentication} object.
     *                       Before registration, the {@link AuthController} must
     *                       create an {@link Auth} object based on this parameter.
     *                       Otherwise, registration will fail with an
     *                       UnauthorizedAccessException.
     * @param request        The {@link RegisterRequest} containing user
     *                       information.
     * @return A {@link ResponseEntity} containing the {@link UserDTO} of the newly
     *         registered user.
     */
    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(Authentication authentication,
            @RequestBody @Valid RegisterRequest request) {
        User user = service.register(authentication, request);
        UserDTO userDto = userDTOMapper.apply(authentication, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(userDto);
    }

    /**
     * @brief Retrieve the user profile information by username.
     * 
     *        Handles GET requests to the endpoint: `/user/{username}/profile`
     * 
     * @param authentication The {@link Authentication} object.
     * @param username       The username of the user whose profile is being
     *                       retrieved.
     * @return A {@link ResponseEntity} containing the {@link ProfileDTO} of the
     *         specified user.
     */
    @GetMapping("/{username}/profile")
    public ResponseEntity<ProfileDTO> getProfileDTOByUsername(Authentication authentication,
            @PathVariable String username) {
        User user = service.getUserByUsername(username);
        ProfileDTO profileDTO = profileDTOMapper.apply(authentication, user);
        return ResponseEntity.ok().body(profileDTO);
    }

    /**
     * @brief Retrieve the profile image of a user.
     * 
     *        Handles GET requests to the endpoint: `/user/{username}/image`
     * 
     * @param username The username of the user whose profile image is being
     *                 retrieved.
     * @return A byte array representing the user's profile image in JPEG format.
     */
    @GetMapping(value = "{username}/image", produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] getProfileImage(@PathVariable("username") String username) {
        return service.getProfileImage(username);
    }

    /**
     * @brief Retrieve a paginated list of followers for a user.
     * 
     *        Handles GET requests to the endpoint: `/user/{username}/followers`
     * 
     * @param authentication The {@link Authentication} object.
     * @param username       The username of the user whose followers are being
     *                       retrieved.
     * @param size           The number of followers to retrieve per page.
     * @param page           The page number to retrieve.
     * @return A {@link Page} containing a list of {@link UserDTO} representing the
     *         user's followers.
     */
    @GetMapping("/{username}/followers")
    public Page<UserDTO> getUserFollowersPage(Authentication authentication,
            @PathVariable String username,
            @RequestParam Integer size,
            @RequestParam Integer page) {
        Page<User> followersPage = service.getUserFollowersPage(username, page, size);
        List<UserDTO> followingsDTO = followersPage.stream()
                .map(follower -> userDTOMapper.apply(authentication, follower)).collect(Collectors.toList());
        return new PageImpl<>(followingsDTO, followersPage.getPageable(), followersPage.getTotalElements());
    }

    /**
     * @brief Retrieve a paginated list of followings for a user.
     * 
     *        Handles GET requests to the endpoint: `/user/{username}/followings`
     * 
     * @param authentication The {@link Authentication} object.
     * @param username       The username of the user whose followings are being
     *                       retrieved.
     * @param size           The number of followings to retrieve per page.
     * @param page           The page number to retrieve.
     * @return A {@link Page} containing a list of {@link UserDTO} representing the
     *         user's followings.
     */
    @GetMapping("/{username}/followings")
    public Page<UserDTO> getUserFollowingsPage(Authentication authentication,
            @PathVariable String username,
            @RequestParam Integer size,
            @RequestParam Integer page) {
        Page<User> followingsPage = service.getUserFollowingsPage(username, page, size);
        List<UserDTO> followingsDTO = followingsPage.stream()
                .map(following -> userDTOMapper.apply(authentication, following)).collect(Collectors.toList());
        return new PageImpl<>(followingsDTO, followingsPage.getPageable(), followingsPage.getTotalElements());
    }

    /**
     * @brief Retrieve a paginated list of users who liked a specific post.
     * 
     *        Handles GET requests to the endpoint: `/user/{postId}/liked`
     * 
     * @param authentication The {@link Authentication} object.
     * @param postId         The ID of the post whose likers are being retrieved.
     * @param size           The number of users to retrieve per page.
     * @param page           The page number to retrieve.
     * @return A {@link Page} containing a list of {@link UserDTO} representing
     *         users who liked the post.
     */
    @GetMapping("/{postId}/liked")
    public Page<UserDTO> getUsersLikedPostPage(Authentication authentication,
            @PathVariable Long postId,
            @RequestParam Integer size,
            @RequestParam Integer page) {
        Page<User> followingsPage = service.getUsersLikedPostPage(postId, page, size);
        List<UserDTO> followingsDTO = followingsPage.stream()
                .map(following -> userDTOMapper.apply(authentication, following)).collect(Collectors.toList());
        return new PageImpl<>(followingsDTO, followingsPage.getPageable(), followingsPage.getTotalElements());
    }

    /**
     * @brief Search for users based on a query.
     *        Users are selected by username or full name.
     * 
     *        Handles GET requests to the endpoint: `/user/search`
     * 
     * @param authentication The {@link Authentication} object.
     * @param request        The search query for user names.
     * @param size           The number of users to retrieve per page.
     * @param page           The page number to retrieve.
     * @return A {@link Page} containing a list of {@link UserDTO} representing the
     *         search results.
     */
    @GetMapping("/search")
    public Page<UserDTO> searchUser(Authentication authentication,
            @RequestParam String request,
            @RequestParam Integer size,
            @RequestParam Integer page) {
        Page<User> searchedPage = service.searchUser(request, page, size);

        List<UserDTO> followingsDTO = searchedPage.stream()
                .map(following -> userDTOMapper.apply(authentication, following)).collect(Collectors.toList());
        return new PageImpl<>(followingsDTO, searchedPage.getPageable(), searchedPage.getTotalElements());
    }

    /**
     * @brief Add a follower for the authenticated user.
     * 
     *        Handles PUT requests to the endpoint:
     *        `/user/follow/{followerUsername}`
     * 
     * @param authentication   The {@link Authentication} object.
     * @param followerUsername The username of the user to follow.
     * @return A {@link ResponseEntity} containing a empty Json.
     */
    @PutMapping("/follow/{followerUsername}")
    public ResponseEntity<EmptyJson> addFollower(Authentication authentication,
            @PathVariable String followerUsername) {
        service.follow(authentication, followerUsername);
        return ResponseEntity.status(HttpStatus.OK).body(new EmptyJson());
    }

    /**
     * @brief Remove a follower for the authenticated user.
     * 
     *        Handles PUT requests to the endpoint:
     *        `/user/deleteFollowing/{followerUsername}`
     * 
     * @param authentication   The {@link Authentication} object.
     * @param followerUsername The username of the user to unfollow.
     * @return A {@link ResponseEntity} containing a empty Json.
     */
    @PutMapping("/deleteFollowing/{followerUsername}")
    public ResponseEntity<EmptyJson> removeFollower(Authentication authentication,
            @PathVariable String followerUsername) {
        service.deleteFollowing(authentication, followerUsername);
        return ResponseEntity.status(HttpStatus.OK).body(new EmptyJson());
    }

    /**
     * @brief Update the authenticated user's information.
     * 
     *        Handles PUT requests to the endpoint: `/user/updateUserInfo`
     * 
     * @param authentication The {@link Authentication} object.
     * @param newUserData    The {@link UserInfoUpdateRequest} object containing the
     *                       new user information.
     * @return A {@link ResponseEntity} containing the updated {@link UserDTO}.
     */
    @PutMapping("/updateUserInfo")
    public ResponseEntity<UserDTO> updateUserInfo(Authentication authentication,
            @RequestBody @Valid UserInfoUpdateRequest newUserData) {
        User updatedUser = service.updateUserInfo(authentication, newUserData);
        UserDTO userDTO = userDTOMapper.apply(authentication, updatedUser);
        return ResponseEntity.ok().body(userDTO);
    }

    /**
     * @brief Update the authenticated user's username.
     * 
     *        Handles PUT requests to the endpoint: `/user/updateUsername`
     * 
     * @param authentication The {@link Authentication} object.
     * @param newUsername    The {@link UsernameUpdateRequest} object containing the
     *                       new username.
     * @return A {@link ResponseEntity} containing the updated {@link UserDTO}.
     */
    @PutMapping("/updateUsername")
    public ResponseEntity<UserDTO> updateUsername(Authentication authentication,
            @RequestBody @Valid UsernameUpdateRequest newUsername) {
        User updatedUser = service.updateUsername(authentication, newUsername);
        UserDTO userDTO = userDTOMapper.apply(authentication, updatedUser);
        return ResponseEntity.ok().body(userDTO);
    }

    /**
     * @brief Upload a new profile image for the authenticated user.
     * 
     *        Handles PUT requests to the endpoint: `/user/image`
     * 
     * @param authentication The {@link Authentication} object.
     * @param file           The {@link MultipartFile} object file to be uploaded as
     *                       the user's profile image.
     * @return A {@link ResponseEntity} containing a empty Json.
     */
    @PutMapping(value = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<EmptyJson> uploadProfileImage(
            Authentication authentication,
            @RequestParam("file") MultipartFile file) {
        User user = service.getAuthenticatedUser(authentication);
        service.uploadProfileImage(user, file);
        return ResponseEntity.status(HttpStatus.OK).body(new EmptyJson());
    }

    /**
     * @brief Delete the profile image of the authenticated user.
     * 
     *        Handles DELETE requests to the endpoint: `/user/image`
     * 
     * @param authentication The {@link Authentication} object.
     * @return A {@link ResponseEntity} containing the updated {@link UserDTO} after
     *         deletion.
     */
    @DeleteMapping("/image")
    public ResponseEntity<UserDTO> deleteProfileImage(Authentication authentication) {
        User user = service.deleteProfileImage(authentication);
        UserDTO userDTO = userDTOMapper.apply(authentication, user);
        return ResponseEntity.ok().body(userDTO);
    }

    /**
     * @brief Delete the authenticated user account.
     * 
     *        Handles DELETE requests to the endpoint: `/user`
     * 
     * @param authentication The {@link Authentication} object.
     * @return A {@link ResponseEntity} containing a empty Json.
     */
    @DeleteMapping("")
    public ResponseEntity<EmptyJson> deleteUser(Authentication authentication) {
        service.deleteUser(authentication);
        return ResponseEntity.status(HttpStatus.OK).body(new EmptyJson());
    }
}
