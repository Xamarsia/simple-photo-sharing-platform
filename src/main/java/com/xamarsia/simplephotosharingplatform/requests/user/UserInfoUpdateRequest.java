package com.xamarsia.simplephotosharingplatform.requests.user;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserInfoUpdateRequest {
    private String fullName;

    private String description;
}
