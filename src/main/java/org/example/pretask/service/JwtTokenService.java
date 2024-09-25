package org.example.pretask.service;

import org.springframework.security.core.userdetails.UserDetails;

public interface JwtTokenService {

    String generateToken(UserDetails userDetails, Long id);
    Boolean isTokenValid(String token, UserDetails userDetails);
    String getUsernameFromToken(String token);
    Long getIdFromToken(String token);
}
