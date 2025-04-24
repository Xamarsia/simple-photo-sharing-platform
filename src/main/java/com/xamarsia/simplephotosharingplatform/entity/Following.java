package com.xamarsia.simplephotosharingplatform.entity;


import com.xamarsia.simplephotosharingplatform.entity.PK.FollowingPK;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
