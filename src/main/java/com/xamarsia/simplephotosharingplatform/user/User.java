package com.xamarsia.simplephotosharingplatform.user;

import com.xamarsia.simplephotosharingplatform.security.authentication.Auth;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "_user", uniqueConstraints = {
        @UniqueConstraint(name = "user_username_unique", columnNames = "username")
})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String username;

    private String fullName;

    private String description;

    @Builder.Default
    private Boolean isProfileImageExist = false;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade = CascadeType.ALL)
    private List<Auth> auth;

    @ManyToMany
    @JoinTable(name = "following",
            joinColumns = @JoinColumn(name = "followingId"),
            inverseJoinColumns = @JoinColumn(name = "followerId")
    )
    private Set<User> followers = new HashSet<User>();

    @ManyToMany
    @JoinTable(name = "following",
            joinColumns = @JoinColumn(name = "followerId"),
            inverseJoinColumns = @JoinColumn(name = "followingId")
    )
    private Set<User> followings = new HashSet<User>();

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }
}
