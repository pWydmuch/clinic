package org.example.pretask.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface JwtUserDetailsService extends UserDetailsService {

    String authenticate(String login, String password);
    @Override
    UserDetails loadUserByUsername(String login);
}