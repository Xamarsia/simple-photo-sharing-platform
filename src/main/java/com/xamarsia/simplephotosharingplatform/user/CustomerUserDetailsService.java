package com.xamarsia.simplephotosharingplatform.user;

import com.xamarsia.simplephotosharingplatform.exception.exceptions.ResourceNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
public class CustomerUserDetailsService implements UserDetailsService {

    private final UserService userService;
    @Override
    public UserDetails loadUserByUsername(String username) throws ResourceNotFoundException {
        return userService.findUserByUsername(username);
    }
}
