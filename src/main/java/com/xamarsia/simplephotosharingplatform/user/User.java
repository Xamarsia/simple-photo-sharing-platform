package com.xamarsia.simplephotosharingplatform.user;

import com.xamarsia.simplephotosharingplatform.security.token.Token;
import com.xamarsia.simplephotosharingplatform.post.Post;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "_user",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "user_email_unique",
                        columnNames = "email"
                ),
        }
)
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Builder.Default
    private Boolean isProfileImageExist = false;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Token> tokens;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<Post> posts;


    @ManyToMany
    @JoinTable(name = "following",
            joinColumns = @JoinColumn(name = "followingId"),
            inverseJoinColumns = @JoinColumn(name = "followerId")
    )
    private Set<User> followers;

    @ManyToMany
    @JoinTable(name = "following",
            joinColumns = @JoinColumn(name = "followerId"),
            inverseJoinColumns = @JoinColumn(name = "followingId")
    )
    private Set<User> followings;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }


    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id)
                && Objects.equals(fullName, user.fullName)
                && Objects.equals(username, user.username)
                && Objects.equals(email, user.email)
                && Objects.equals(password, user.password)
                && Objects.equals(isProfileImageExist, user.isProfileImageExist);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, fullName, username, email, password, isProfileImageExist);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", fullName='" + fullName + '\'' +
                ", username=" + username + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", isProfileImageExist='" + isProfileImageExist + '\'' +
                '}';
    }
}