package it.epicode.w7d5.event_management.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import it.epicode.w7d5.event_management.Models.entities.User;
import it.epicode.w7d5.event_management.exceptions.UnauthorizedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

@Component
@PropertySource("application.properties")
public class JwtTools {
    @Value("${access_token.secret}")
    private String secret;

    @Value("${access_token.expiresIn}")
    private String exp;

    public String createToken(User u) {
        return Jwts.builder().subject(u.getId().toString()).issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + Long.parseLong(exp)))
                .signWith(Keys.hmacShaKeyFor(secret.getBytes())).compact();
    }

    public void validateToken(String token) throws UnauthorizedException {
        try {
            Jwts.parser().verifyWith(Keys.hmacShaKeyFor(secret.getBytes()))
                    .build().parse(token);
        } catch (Exception e) {
            throw new UnauthorizedException("Invalid access token");
        }
    }

    public UUID extractUserIdFromToken(String token) throws UnauthorizedException {
        try{
        return UUID.fromString(Jwts.parser().verifyWith(Keys.hmacShaKeyFor(secret.getBytes())).build()
                .parseSignedClaims(token).getPayload().getSubject());
        } catch (IllegalArgumentException e) {
            throw new UnauthorizedException("Invalid access token");
        }
    }

}
