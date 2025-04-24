package com.xamarsia.simplephotosharingplatform.entity;


import com.xamarsia.simplephotosharingplatform.entity.PK.LikePK;

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
@Table(name = "_like", uniqueConstraints = {
        @UniqueConstraint(name = "user_post_unique", columnNames = { "user_id", "post_id" })
})
public class Like {

    @EmbeddedId
    private LikePK id;
}
