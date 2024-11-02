package com.xamarsia.simplephotosharingplatform.post.like;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class LikePK implements Serializable {

    @Column(name = "userPK", nullable = false)
    private Long userPK;

    @Column(name = "postPK", nullable = false)
    private Long postPK;
}
