package org.example.pretask.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/basic")
public class RoleTestController {

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/admin")
    public String getAdmin(Authentication authentication) {
        return "Hi admin: " + authentication.getName();
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping("/user")
    public String getUser() {
        return "Hi user";
    }

}
