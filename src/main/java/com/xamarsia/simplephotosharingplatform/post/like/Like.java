package com.xamarsia.simplephotosharingplatform.post.like;


import jakarta.persistence.*;
import lombok.*;

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
