package com.xamarsia.simplephotosharingplatform.user.following;


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
}
