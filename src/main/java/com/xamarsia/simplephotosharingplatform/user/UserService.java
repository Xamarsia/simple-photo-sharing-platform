package com.xamarsia.simplephotosharingplatform.user;

import com.xamarsia.simplephotosharingplatform.dto.user.UserUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public User getByEmail(String email) {
        return repository.findUserByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email " + email));
    }

    @Transactional(readOnly = true)
    public User getById(Long customerId) {
        return selectUserById(customerId)
                .orElseThrow(() -> new RuntimeException("User not found with id " + customerId));
    }

    public User getAuthenticatedUser(Authentication authentication) {
        if (authentication instanceof AnonymousAuthenticationToken) {
            throw new RuntimeException("Authenticated user not found");
        }

        String name = authentication.getName();
        return findUserByUsername(name)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with name " + name));
    }

    public boolean isEmailUsed(String email) {
        return repository.existsUserByEmail(email);
    }

    public boolean isUserWithIdExist(Long id) {
        return repository.existsUserById(id);
    }

    public List<User> selectAllUsers() {
        Page<User> page = repository.findAll(Pageable.ofSize(1000));
        return page.getContent();
    }

    public Optional<User> selectUserById(Long id) {
        return repository.findById(id);
    }

    public User saveUser(User user) {
        return repository.save(user);
    }

    public void deleteUserById(Long id) {
        if (!isUserWithIdExist(id)) {
            throw new RuntimeException("User not found with id " + id);
        }
        repository.deleteById(id);
    }

    public User updateUser(UserUpdateRequest newUserData, Long id) {
        User user = selectUserById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id " + id));
        String newUsername = newUserData.getUsername();
        String newEmail = newUserData.getEmail();

        if (!Objects.equals(user.getEmail(), newEmail) && repository.existsUserByEmail(newEmail)) {
            throw new RuntimeException("User with this email already exist! " + newEmail);
        }

        if (!Objects.equals(user.getUsername(), newUsername) && repository.existsUserByUsername(newUsername)) {
            throw new RuntimeException("User with this username already exist! " + newUsername);
        }

        user.setFullName(newUserData.getFullName());
        user.setUsername(newUserData.getUsername());
        user.setPassword(passwordEncoder.encode(newUserData.getPassword()));
        user.setEmail(newUserData.getEmail());

        return saveUser(user);
    }


    public Optional<User> findUserByUsername(String username) {
        return repository.findUserByUsername(username);
    }

    public Optional<User> selectUserByEmail(String email) {
        return repository.findUserByEmail(email);
    }

    public void updateProfileImageId(String profileImageId, Long userId) {
        repository.updateProfileImageId(profileImageId, userId);
    }
}
