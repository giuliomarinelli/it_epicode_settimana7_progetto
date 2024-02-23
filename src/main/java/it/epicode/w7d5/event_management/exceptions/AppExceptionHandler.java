package it.epicode.w7d5.event_management.exceptions;


import it.epicode.w7d5.event_management.Models.resDTO.HttpErrorRes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.IOException;

@ControllerAdvice
public class AppExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<HttpErrorRes> badRequestHandler(BadRequestException e) {
        return new ResponseEntity<>(new HttpErrorRes(HttpStatus.BAD_REQUEST,
                "Bad request", e.getMessage()), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<HttpErrorRes> notFoundHandler(NotFoundException e) {
        return new ResponseEntity<>(new HttpErrorRes(HttpStatus.NOT_FOUND,
                "Not found", e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IOException.class)
    // Gestisco un'unica eccezione di questo tipo, perciò ometto i controlli che servirebbero a contestualizzare e inquadrare l'errore
    public ResponseEntity<HttpErrorRes> ioExceptionHandler(IOException e) {
        return new ResponseEntity<>(new HttpErrorRes(HttpStatus.SERVICE_UNAVAILABLE,
                "Service unavailable", "An input/output error with Cloudinary provider occurred" +
                " during file upload. " + e.getMessage()), HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(InternalServerErrorException.class)
    public ResponseEntity<HttpErrorRes> internalServerErrorHandler(InternalServerErrorException e) {
        return genericExceptionHandler(e);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<HttpErrorRes> genericExceptionHandler(Exception e) {
        return new ResponseEntity<>(new HttpErrorRes(HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal server error", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<HttpErrorRes> accessDeniedHandler(UnauthorizedException e) {
        return new ResponseEntity<>(new HttpErrorRes(HttpStatus.UNAUTHORIZED,
                "Unauthorized", e.getMessage()), HttpStatus.UNAUTHORIZED);
    }



}
