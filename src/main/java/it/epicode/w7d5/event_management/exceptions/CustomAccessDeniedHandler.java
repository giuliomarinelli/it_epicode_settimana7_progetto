package it.epicode.w7d5.event_management.exceptions;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.epicode.w7d5.event_management.Models.resDTO.HttpErrorRes;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;

public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest req,
                       HttpServletResponse res,
                       AccessDeniedException accessDeniedException)  {



        ObjectMapper mapper = new ObjectMapper();
        res.setContentType("application/json;charset=UTF-8");
        res.setStatus(HttpStatus.UNAUTHORIZED.value());
        try {
            res.getWriter().write(mapper.writeValueAsString(
                    new HttpErrorRes(HttpStatus.UNAUTHORIZED, "Unauthorized",
                            "You don't have permissions to access this resource")));
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }




    }
}