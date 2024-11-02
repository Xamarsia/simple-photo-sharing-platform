package com.xamarsia.simplephotosharingplatform.post.like;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LikePK implements Serializable {

    @Column(name = "userID", nullable = false)
    private Long userID;

    @Column(name = "postID", nullable = false)
    private Long postID;
}
