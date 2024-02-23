package it.epicode.w7d5.event_management.controllers;

import it.epicode.w7d5.event_management.Models.entities.Event;
import it.epicode.w7d5.event_management.Models.reqDTO.EventDTO;
import it.epicode.w7d5.event_management.Models.reqDTO.SubscriptionDTO;
import it.epicode.w7d5.event_management.Models.resDTO.ConfirmRes;
import it.epicode.w7d5.event_management.exceptions.BadRequestException;
import it.epicode.w7d5.event_management.exceptions.NotFoundException;
import it.epicode.w7d5.event_management.exceptions.SubscriptionException;
import it.epicode.w7d5.event_management.exceptions.ValidationMessages;
import it.epicode.w7d5.event_management.services.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
public class EventController {
    @Autowired
    EventService eventSvc;

    @GetMapping("/events")
    public Page<Event> getAll(Pageable pageable) {
        return eventSvc.getAll(pageable);
    }

    @GetMapping("/events/{id}")
    public Event getById(@PathVariable UUID id) throws NotFoundException {
        return eventSvc.findById(id);
    }

    @PostMapping("/events")
    @PreAuthorize("hasAuthority('EVENT_ORGANIZER')")
    public Event create(@RequestBody @Validated EventDTO eventDTO, BindingResult validation) throws BadRequestException {
        if (validation.hasErrors())
            throw new BadRequestException(ValidationMessages.generateValidationErrorMessage(validation));
        return eventSvc.create(eventDTO);
    }

    @PutMapping("/events/{id}")
    @PreAuthorize("hasAuthority('EVENT_ORGANIZER')")
    public Event update(@RequestBody @Validated EventDTO eventDTO, BindingResult validation, @PathVariable UUID id) throws BadRequestException {
        if (validation.hasErrors())
            throw new BadRequestException(ValidationMessages.generateValidationErrorMessage(validation));
        return eventSvc.update(eventDTO, id);
    }
    @PatchMapping("/events/{id}/user-subscribe")
    public ConfirmRes subscribe(@RequestBody @Validated SubscriptionDTO subscriptionDTO,
                                BindingResult validation, @PathVariable UUID id) throws SubscriptionException, BadRequestException {
        if (validation.hasErrors())
            throw new BadRequestException(ValidationMessages.generateValidationErrorMessage(validation));
        try {
        return eventSvc.subscribeUserToEvent(id, UUID.fromString(subscriptionDTO.userId()));
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("'userId' field is malformed since it doesn't respect the Universal Unique ID pattern");
        }
    }

    @DeleteMapping("/events/{id}")
    @PreAuthorize("hasAuthority('EVENT_ORGANIZER')")
    public ConfirmRes delete(@PathVariable UUID id) throws BadRequestException {
        return eventSvc.delete(id);
    }
}
