package com.xamarsia.simplephotosharingplatform.entity.PK;

import java.io.Serializable;
import java.util.Objects;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.xamarsia.simplephotosharingplatform.entity.User;

import jakarta.persistence.Embeddable;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class FollowingPK implements Serializable {
    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "follower_id", nullable = false)
    private User follower;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "following_id", nullable = false)
    private User following;

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof FollowingPK)) {
            return false;
        }
        FollowingPK other = (FollowingPK) obj;
        return (this.follower.equals(other.follower)) && this.following.equals(other.following);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(follower, following);
    }
}
