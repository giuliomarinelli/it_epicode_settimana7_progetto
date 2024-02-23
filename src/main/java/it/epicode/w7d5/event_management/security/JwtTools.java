package it.epicode.w7d5.event_management.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import it.epicode.w7d5.event_management.Models.entities.User;
import it.epicode.w7d5.event_management.exceptions.UnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.net.http.HttpRequest;
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

    public boolean matchTokenSub(UUID userId) throws UnauthorizedException {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        HttpServletRequest req;
        if (requestAttributes instanceof ServletRequestAttributes) {
            req = ((ServletRequestAttributes)requestAttributes).getRequest();
        } else {
            return false;
        }
        String token = req.getHeader("Authorization").split(" ")[1];
        UUID tokenUserId = extractUserIdFromToken(token);
        return tokenUserId.equals(userId);
    }





}
