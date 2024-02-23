package it.epicode.w7d5.event_management.services;

import it.epicode.w7d5.event_management.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRp;


}
