package org.example.pretask.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.example.pretask.service.JwtTokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Clock;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtTokenServiceImpl implements JwtTokenService {

    public static final long JWT_TOKEN_VALIDITY = 5 * 60 * 60 * 1000; //5 hours in milliseconds

    private final String secret;
    private final Clock clock;

    public JwtTokenServiceImpl(@Value("${jwt.secret}") String secret, Clock clock) {
        this.secret = secret;
        this.clock = clock;
    }

    @Override
    public String generateToken(UserDetails userDetails, Long id) {
        Map<String, Object> claims = new HashMap<>();
        String subject = userDetails.getUsername();
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .id(id.toString())
                .issuedAt(new Date(clock.millis()))
                .expiration(new Date(clock.millis() + JWT_TOKEN_VALIDITY))
                .signWith(getSignInKey())
                .compact();
    }

    @Override
    public Boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            final String username = getUsernameFromToken(token);
            return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        } catch (ExpiredJwtException e) {
            return false;
        }
    }

    @Override
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    @Override
    public Long getIdFromToken(String token) {
        return Long.parseLong(getClaimFromToken(token, Claims::getId));
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date(clock.millis()));
    }

    private Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    private <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .clock(() -> new Date(clock.millis()))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSignInKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }
}
