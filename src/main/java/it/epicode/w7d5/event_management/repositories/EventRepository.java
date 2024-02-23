package it.epicode.w7d5.event_management.repositories;

import it.epicode.w7d5.event_management.Models.entities.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
@Repository
public interface EventRepository extends JpaRepository<Event, UUID>, PagingAndSortingRepository<Event, UUID> {

}
