package com.xamarsia.simplephotosharingplatform.user.following;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.xamarsia.simplephotosharingplatform.user.User;

import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "following", uniqueConstraints = {
        @UniqueConstraint(name = "follower_following_unique", columnNames = { "follower_id", "following_id" })
})
public class Following {
    @EmbeddedId
    private FollowingPK id;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "follower_id", nullable = false)
    private User follower;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "following_id", nullable = false)
    private User following;
}
