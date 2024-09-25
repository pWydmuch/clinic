package org.example.pretask.service.impl;

import org.example.pretask.service.JwtTokenService;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenServiceImplTest {

    final private static String SECRET = "9a4f2c8d3b7a1e6f45c8a0b3f267d8b1d4e6f3c8a9d2b5f8e3a9c8b5f6v8a3d9";
    final private static Clock CLOCK = Clock.fixed(Instant.parse("2020-12-01T10:05:23.653Z"), ZoneId.of("Europe/Prague"));
    final private static String TOKEN_FOR_USERNAME_LOGIN = "eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJsb2dpbiIsImp0aSI6IjEiLCJpYXQiOjE2MDY4MTcxMjMsImV4cCI6MTYwNjgzNTEyM30.mduoDlfe27f_EE5bFg0IdtlR-HxqH8PCm17vT7fbtzp6cHeNmKSl1S2oSFk6Gt3P";

    final private JwtTokenService jwtTokenService = new JwtTokenServiceImpl(SECRET, CLOCK);

    @Test
    void shouldGenerateTokenForUsernameLogin() {
        String token = jwtTokenService.generateToken(getUserDetails("login"), 1L);
        assertEquals(TOKEN_FOR_USERNAME_LOGIN, token);
    }

    @Test
    void shouldGenerateDifferentTokenForUsernameLogin2() {
        String token = jwtTokenService.generateToken(getUserDetails("login2"), 1L);
        assertNotEquals(TOKEN_FOR_USERNAME_LOGIN, token);
    }

    @Test
    void tokenShouldBeValidForUsernameLoginAndTokenForUsernameLogin() {
        Boolean isTokenValidForLogin = jwtTokenService.isTokenValid(TOKEN_FOR_USERNAME_LOGIN, getUserDetails("login"));
        assertTrue(isTokenValidForLogin);
    }

    @Test
    void tokenShouldBeInvalidForUsernameLogin2AndTokenForUsernameLogin() {
        Boolean isTokenValidForLogin = jwtTokenService.isTokenValid(TOKEN_FOR_USERNAME_LOGIN, getUserDetails("login2"));
        assertFalse(isTokenValidForLogin);
    }

    @Test
    void tokenShouldBeInvalidForUsernameLoginAndToken() {
        Boolean isTokenValidForLogin = jwtTokenService.isTokenValid("eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJsb2dpbiIsImp0aSI6IjEiLCJpYXQiOjE2MDY3OTU1MjMsImV4cCI6MTYwNjgxMzUyM30.nM84LB0kxzm5-4SLBSyI05oAGap0L0gNqDRxT17lszni3dr695fn4AHJrJCB65Qd", getUserDetails("login"));
        assertFalse(isTokenValidForLogin);
    }

    @Test
    void usernameFromTokenForUsernameLoginShouldBeLogin() {
        String username = jwtTokenService.getUsernameFromToken(TOKEN_FOR_USERNAME_LOGIN);
        assertEquals("login", username);
    }

    @Test
    void idFromTokenForUsernameLoginShouldBe1() {
        Long idFromToken = jwtTokenService.getIdFromToken(TOKEN_FOR_USERNAME_LOGIN);
        assertEquals(1L, idFromToken);
    }

    private static User getUserDetails(String login) {
        return new User(login, "password", new ArrayList<>());
    }
}
