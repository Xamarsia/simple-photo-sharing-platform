package com.xamarsia.simplephotosharingplatform.post;

import com.xamarsia.simplephotosharingplatform.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private final LocalDateTime creationDateTime = LocalDateTime.now();
    
    private LocalDateTime updateDateTime;

    private String description;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
