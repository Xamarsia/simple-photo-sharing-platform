package com.xamarsia.simplephotosharingplatform.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.xamarsia.simplephotosharingplatform.common.EmptyJson;
import com.xamarsia.simplephotosharingplatform.service.LikeService;

import lombok.RequiredArgsConstructor;

/**
 * @brief Controller for handling like operations on posts.
 * 
 *        This controller provides endpoints for users to like and unlike posts.
 */
@RestController
@RequestMapping("/like")
@RequiredArgsConstructor
public class LikeController {
    private final LikeService service;

    /**
     * @brief Like a post by its ID.
     * 
     *        Handles POST requests to the endpoint: `/like/{postId}`
     * 
     * @param authentication The {@link Authentication} object.
     * @param postId         The ID of the post to be liked.
     * @return A {@link ResponseEntity} containing a empty Json.
     */
    @PostMapping("/{postId}")
    public ResponseEntity<EmptyJson> like(Authentication authentication,
            @PathVariable Long postId) {
        service.like(authentication, postId);
        return ResponseEntity.status(HttpStatus.OK).body(new EmptyJson());
    }

    /**
     * @brief Unlike a post by its ID.
     * 
     *        Handles DELETE requests to the endpoint: `/like/{postId}`
     * 
     * @param authentication The {@link Authentication} object.
     * @param postId         The ID of the post to be unliked.
     * @return A {@link ResponseEntity} containing a empty Json.
     */
    @DeleteMapping("/{postId}")
    public ResponseEntity<EmptyJson> deleteLike(Authentication authentication,
            @PathVariable Long postId) {
        service.deleteLike(authentication, postId);
        return ResponseEntity.status(HttpStatus.OK).body(new EmptyJson());
    }
}
