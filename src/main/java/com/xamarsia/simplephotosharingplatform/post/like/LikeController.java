package com.xamarsia.simplephotosharingplatform.post.like;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.xamarsia.simplephotosharingplatform.common.EmptyJson;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/like")
@RequiredArgsConstructor
public class LikeController {
    private final LikeService service;

    @PostMapping("/{postId}")
    public ResponseEntity<?> like(Authentication authentication,
            @PathVariable Long postId) {
        service.like(authentication, postId);
        return ResponseEntity.status(HttpStatus.OK).body(new EmptyJson());
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<?> deleteLike(Authentication authentication, 
            @PathVariable Long postId) {
        service.deleteLike(authentication, postId);
        return ResponseEntity.status(HttpStatus.OK).body(new EmptyJson());
    }
}
