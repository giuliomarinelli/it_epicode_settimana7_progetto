package it.epicode.w7d5.event_management.repositories;

import it.epicode.w7d5.event_management.Models.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    public Optional<User> findByEmail(String email);
    @Query("SELECT u.email FROM User u")
    public List<String> getAllEmails();
}
