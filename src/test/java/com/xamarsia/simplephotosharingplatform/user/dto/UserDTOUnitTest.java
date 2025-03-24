package com.xamarsia.simplephotosharingplatform.user.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import com.xamarsia.simplephotosharingplatform.user.State;
import com.xamarsia.simplephotosharingplatform.user.User;
import com.xamarsia.simplephotosharingplatform.user.UserService;
import com.xamarsia.simplephotosharingplatform.user.dto.mappers.UserDTOMapper;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;


@ExtendWith(MockitoExtension.class)
public class UserDTOUnitTest {
    
    @Mock
    private UserService service;

    @Mock
    private Authentication authentication;

    private final static Long USER_ID = 9845786234L;

    private final static User user = User.builder()
        .id(USER_ID)
        .username("username")
        .fullName("Full Name")
        .build();

    @Test
    public void testConvertUserToUserDTO() {
        when(service.getState(authentication, user.getUsername())).thenReturn(State.CURRENT);

        UserDTOMapper userDTOMapper = new UserDTOMapper(service);
        UserDTO userDTO = userDTOMapper.apply(authentication, user);

        assertEquals(user.getId(), userDTO.id());
        assertEquals(user.getUsername(), userDTO.username());
        assertEquals(user.getFullName(), userDTO.fullName());
        assertEquals(user.getDescription(), userDTO.description());
        assertEquals(user.getIsProfileImageExist(), userDTO.isProfileImageExist());
    }
}
