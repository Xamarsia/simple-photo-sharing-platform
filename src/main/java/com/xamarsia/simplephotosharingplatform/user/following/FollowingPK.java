package com.xamarsia.simplephotosharingplatform.user.following;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class FollowingPK implements Serializable {
    @Column(name = "followerPK", nullable = false)
    private Long followerPK;

    @Column(name = "followingPK", nullable = false)
    private Long followingPK;
}
