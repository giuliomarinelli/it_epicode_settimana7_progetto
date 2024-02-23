package it.epicode.w7d5.event_management.controllers;

import it.epicode.w7d5.event_management.Models.entities.User;
import it.epicode.w7d5.event_management.Models.reqDTO.LoginDTO;
import it.epicode.w7d5.event_management.Models.reqDTO.UserDTO;
import it.epicode.w7d5.event_management.Models.resDTO.AccessTokenRes;
import it.epicode.w7d5.event_management.exceptions.BadRequestException;
import it.epicode.w7d5.event_management.exceptions.InternalServerErrorException;
import it.epicode.w7d5.event_management.exceptions.UnauthorizedException;
import it.epicode.w7d5.event_management.exceptions.ValidationMessages;
import it.epicode.w7d5.event_management.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    @Autowired
    private AuthService authSvc;

    @PostMapping("/auth/register")
    public User register(@RequestBody @Validated UserDTO userDTO, BindingResult validation) throws BadRequestException, InternalServerErrorException {
        if (validation.hasErrors())
            throw new BadRequestException(ValidationMessages.generateValidationErrorMessage(validation));
        return authSvc.register(userDTO);
    }

    @PostMapping("/auth/login")
    public AccessTokenRes login(@RequestBody @Validated LoginDTO loginDTO, BindingResult validation) throws BadRequestException, UnauthorizedException {
        if (validation.hasErrors())
            throw new BadRequestException(ValidationMessages.generateValidationErrorMessage(validation));
        return authSvc.login(loginDTO.email(), loginDTO.password());
    }

}
