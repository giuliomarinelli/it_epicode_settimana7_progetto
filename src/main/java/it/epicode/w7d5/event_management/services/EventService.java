package it.epicode.w7d5.event_management.services;

import it.epicode.w7d5.event_management.Models.entities.Event;
import it.epicode.w7d5.event_management.Models.entities.User;
import it.epicode.w7d5.event_management.Models.reqDTO.EventDTO;
import it.epicode.w7d5.event_management.Models.resDTO.ConfirmRes;
import it.epicode.w7d5.event_management.exceptions.BadRequestException;
import it.epicode.w7d5.event_management.exceptions.NotFoundException;
import it.epicode.w7d5.event_management.exceptions.SubscriptionException;
import it.epicode.w7d5.event_management.repositories.EventRepository;
import it.epicode.w7d5.event_management.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Service
public class EventService {
    @Autowired
    private UserRepository userRp;

    @Autowired
    private EventRepository eventRp;

    public Page<Event> getAll(Pageable pageable) {
        return eventRp.findAll(pageable).map(e -> {
            e.setSubscriptions(e.getSubscribedUsers().size());
            return e;
        });

    }

    public Event findById(UUID id) throws NotFoundException {
        Event e = eventRp.findById(id).orElseThrow(
                () -> new NotFoundException("Event not found")
        );
        e.setSubscriptions(e.getSubscribedUsers().size());
        return e;
    }

    public Event create(EventDTO eventDTO) {
        Event e = new Event(
                eventDTO.title(),
                eventDTO.description(),
                LocalDate.parse(eventDTO.date()),
                eventDTO.totalPlaces()
        );
        e.setSubscriptions(0);
        return eventRp.save(e);
    }

    public Event update(EventDTO eventDTO, UUID id) throws BadRequestException {
        Event e = eventRp.findById(id).orElseThrow(
                () -> new BadRequestException("Event with id='" + id + "' doesn't exist. Cannot update")
        );
        e.setTitle(eventDTO.title());
        e.setDescription(eventDTO.description());
        e.setDate(LocalDate.parse(eventDTO.date()));
        if (e.getSubscribedUsers().size() > eventDTO.totalPlaces())
            throw new BadRequestException("'totalPlaces' sent field is minor than the number of " +
                    "currently subscribed users. Cannot update. You must delete exceeding subscriptions before updating");
        e.setTotalPlaces(eventDTO.totalPlaces());
        e.setSubscriptions(e.getSubscribedUsers().size());
        return eventRp.save(e);
    }

    public ConfirmRes subscribeUserToEvent(UUID eventId, UUID userId) throws BadRequestException, SubscriptionException {
        Event e = eventRp.findById(eventId).orElseThrow(
                () -> new BadRequestException("Event you're trying to subscribe doesn't exist. Cannot subscribe")
        );
        User u = userRp.findById(userId).orElseThrow(
                () -> new BadRequestException("User you're trying to subscribe to this event doesn't exist. Cannot subscribe")
        );
        e.addSubscription(u);
        return new ConfirmRes("User with id='" + userId + "' correctly subscribed to event " +
                "'" + e.getTitle() + "' (id='" + eventId + "'", HttpStatus.OK);
    }

    public ConfirmRes delete(UUID id) throws BadRequestException {
        Event e = eventRp.findById(id).orElseThrow(
                () -> new BadRequestException("Event with id='" + id + "' doesn't exist. Cannot delete")
        );
        eventRp.delete(e);
        return new ConfirmRes("Event with id='" + id + "' correctly deleted", HttpStatus.OK);
    }

}
