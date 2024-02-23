package it.epicode.w7d5.event_management.services;

import it.epicode.w7d5.event_management.Models.entities.Event;
import it.epicode.w7d5.event_management.Models.entities.User;
import it.epicode.w7d5.event_management.Models.reqDTO.EventDTO;
import it.epicode.w7d5.event_management.Models.resDTO.ConfirmRes;
import it.epicode.w7d5.event_management.exceptions.BadRequestException;
import it.epicode.w7d5.event_management.exceptions.NotFoundException;
import it.epicode.w7d5.event_management.exceptions.SubscriptionException;
import it.epicode.w7d5.event_management.exceptions.UnauthorizedException;
import it.epicode.w7d5.event_management.repositories.EventRepository;
import it.epicode.w7d5.event_management.repositories.UserRepository;
import it.epicode.w7d5.event_management.security.JwtTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class EventService {
    @Autowired
    private UserRepository userRp;

    @Autowired
    private EventRepository eventRp;

    @Autowired
    private JwtTools jwtTools;

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

    public List<Event> findByUserIdWithoutControls(UUID userId) throws BadRequestException {
        User u = userRp.findById(userId).orElseThrow(
                () -> new BadRequestException("userId='" + userId + "' refers to an inexistent user")
        );

        return u.getEvents();
    }

    public List<Event> findByUserId(UUID userId) throws BadRequestException, UnauthorizedException {
        User u = userRp.findById(userId).orElseThrow(
                () -> new BadRequestException("userId='" + userId + "' refers to an inexistent user")
        );
        if (!jwtTools.matchTokenSub(userId))
            throw new UnauthorizedException("You don't have the permission to view the events filtering by users other than you");

        return u.getEvents();
    }

    public Event create(EventDTO eventDTO) throws BadRequestException {
        if (LocalDate.parse(eventDTO.date()).isBefore(LocalDate.now()))
            throw new BadRequestException("Event 'date' sent is located in the past. Cannot create");
        Event e = new Event(
                eventDTO.title(),
                eventDTO.description(),
                LocalDate.parse(eventDTO.date()),
                eventDTO.totalPlaces(),
                eventDTO.location()
        );
        e.setSubscriptions(0);
        return eventRp.save(e);
    }

    public Event update(EventDTO eventDTO, UUID id) throws BadRequestException {
        if (LocalDate.parse(eventDTO.date()).isBefore(LocalDate.now()))
            throw new BadRequestException("Event 'date' sent is located in the past. Cannot update");
        Event e = eventRp.findById(id).orElseThrow(
                () -> new BadRequestException("Event with id='" + id + "' doesn't exist. Cannot update")
        );
        e.setTitle(eventDTO.title());
        e.setDescription(eventDTO.description());
        e.setDate(LocalDate.parse(eventDTO.date()));
        e.setLocation(eventDTO.location());
        if (e.getSubscribedUsers().size() > eventDTO.totalPlaces())
            throw new BadRequestException("'totalPlaces' sent field is minor than the number of " +
                    "currently subscribed users. Cannot update");
        e.setTotalPlaces(eventDTO.totalPlaces());
        e.setSubscriptions(e.getSubscribedUsers().size());
        return eventRp.save(e);
    }

    public ConfirmRes subscribeUserToEvent(UUID eventId, UUID userId) throws BadRequestException, SubscriptionException, UnauthorizedException {
        Event e = eventRp.findById(eventId).orElseThrow(
                () -> new BadRequestException("Event you're trying to subscribe doesn't exist. Cannot subscribe")
        );
        if (e.getDate().isBefore(LocalDate.now()))
            throw new BadRequestException("Event already occurred. Cannot subscribe");
        User u = userRp.findById(userId).orElseThrow(
                () -> new BadRequestException("User you're trying to subscribe to this event doesn't exist. Cannot subscribe")
        );
        if (!jwtTools.matchTokenSub(userId))
            throw new UnauthorizedException("You don't have the permission for subscribing users other than you to the event");
        e.addSubscription(u);
        eventRp.save(e);
        return new ConfirmRes("User with id='" + userId + "' correctly subscribed to event " +
                "'" + e.getTitle() + "' (id='" + eventId + "'", HttpStatus.OK);
    }

    public ConfirmRes unsubscribeUserFromEvent(UUID eventId, UUID userId) throws BadRequestException, UnauthorizedException {
        Event e = eventRp.findById(eventId).orElseThrow(
                () -> new BadRequestException("Event you're trying to unsubscribe doesn't exist. Cannot unsubscribe")
        );
        if (e.getDate().isBefore(LocalDate.now()))
            throw new BadRequestException("Event already occurred. Cannot unsubscribe");
        User u = userRp.findById(userId).orElseThrow(
                () -> new BadRequestException("User you're trying to unsubscribe to this event doesn't exist. Cannot subscribe")
        );
        if (!jwtTools.matchTokenSub(userId))
            throw new UnauthorizedException("You don't have the permission for unsubscribing users other than you from the event");
        e.removeSubscription(u);
        eventRp.save(e);
        return new ConfirmRes("User with id='" + userId + "' correctly unsubscribed from event " +
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
