package it.epicode.w7d5.event_management.services;

import it.epicode.w7d5.event_management.Models.entities.User;
import it.epicode.w7d5.event_management.Models.reqDTO.UserDTO;
import it.epicode.w7d5.event_management.Models.resDTO.AccessTokenRes;
import it.epicode.w7d5.event_management.exceptions.BadRequestException;
import it.epicode.w7d5.event_management.exceptions.InternalServerErrorException;
import it.epicode.w7d5.event_management.exceptions.UnauthorizedException;
import it.epicode.w7d5.event_management.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRp;

    @Autowired
    private PasswordEncoder encoder;

    public User register(UserDTO userDTO) throws BadRequestException, InternalServerErrorException {
        User user = new User(
                userDTO.firstName(),
                userDTO.lastName(),
                userDTO.email(),
                encoder.encode(userDTO.password()));
        try {
            return userRp.save(user);
        } catch (DataIntegrityViolationException e) {
            if (userRp.getAllEmails().contains(user.getEmail()))
                throw new BadRequestException("'email' already exists, cannot register");
            throw new InternalServerErrorException("Data Integrity violation error. " + e.getMessage());
        }
    }

    public Optional<User> findeUserById(UUID id) {
        return userRp.findById(id);
    }

    public AccessTokenRes login(String email, String password) throws UnauthorizedException {
        User user = userRp.findByEmail(email).orElseThrow(
                () -> new UnauthorizedException("Email and/or password are incorrect")
        );
        if (!encoder.matches(password, user.getHashPassword()))
            throw new UnauthorizedException("Email and/or password are incorrect");
        return null;
    }
}
