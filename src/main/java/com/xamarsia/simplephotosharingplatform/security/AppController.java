package com.xamarsia.simplephotosharingplatform.security;

import java.security.Principal;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AppController {

    @GetMapping("/auth/foo")
    public String foo() {
        return "foo";
    }

    @GetMapping("/test")
    public String test2(Principal principal) {
        return principal.getName();
    }
}
