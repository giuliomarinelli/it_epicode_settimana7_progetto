package it.epicode.w7d5.event_management.security;


import com.fasterxml.jackson.databind.ObjectMapper;
import it.epicode.w7d5.event_management.Models.entities.User;
import it.epicode.w7d5.event_management.Models.resDTO.HttpErrorRes;
import it.epicode.w7d5.event_management.exceptions.UnauthorizedException;
import it.epicode.w7d5.event_management.services.AuthService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTools jwtTools;

    @Autowired
    private AuthService authSvc;


    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain) throws ServletException, IOException {
        try {
            String authorization = req.getHeader("Authorization");
            if (authorization == null) {
                throw new UnauthorizedException("No provided access token");
            } else if (!authorization.startsWith("Bearer "))
                throw new UnauthorizedException("Access token is incorrectly provided");

            String token = authorization.split(" ")[1];

            jwtTools.validateToken(token);

            UUID userId = jwtTools.extractUserIdFromToken(token);


            User u = authSvc.findUserById(userId).orElseThrow(
                    () -> new UnauthorizedException("Invalid access token")
            );



            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(u, null, u.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(req, res);
        } catch (UnauthorizedException e) {
            ObjectMapper mapper = new ObjectMapper();
            res.setStatus(HttpStatus.UNAUTHORIZED.value());
            res.setContentType("application/json;charset=UTF-8");
            res.getWriter().write(mapper.writeValueAsString(
                    new HttpErrorRes(HttpStatus.UNAUTHORIZED,
                            "Unauthorized", e.getMessage()
                    )));
        } catch (IllegalArgumentException e) {
            ObjectMapper mapper = new ObjectMapper();
            res.setStatus(HttpStatus.BAD_REQUEST.value());
            res.setContentType("application/json;charset=UTF-8");
            res.getWriter().write(mapper.writeValueAsString(
                    new HttpErrorRes(HttpStatus.BAD_REQUEST,
                            "Bad request", "malformed 'uderid' query param"
                    )));
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return new AntPathMatcher().match("/auth/**", request.getServletPath());
    }


}
