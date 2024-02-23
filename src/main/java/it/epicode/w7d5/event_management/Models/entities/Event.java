package it.epicode.w7d5.event_management.Models.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import it.epicode.w7d5.event_management.exceptions.SubscriptionException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Setter(AccessLevel.NONE)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private String location;

    @Transient
    private int subscriptions;

    @Column(nullable = false)
    private int totalPlaces;

    @Column(nullable = false)
    private boolean soldOut = false;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "events_users",
            joinColumns = @JoinColumn(name = "event_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "userd_id", nullable = false)
    )
    private List<User> subscribedUsers = new ArrayList<>();

    public Event(String title, String description, LocalDate date, int totalPlaces, String location) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.totalPlaces = totalPlaces;
        this.location = location;

    }

    public void addSubscription(User user) throws SubscriptionException {
        if (subscribedUsers.contains(user))
            throw new SubscriptionException("User with id='" + user.getId() + " is already subscribed to this event. Cannot subscribe");
        if (soldOut)
            throw new SubscriptionException("Event is sold out. Cannot subscribe");
        subscribedUsers.add(user);
        if (subscribedUsers.size() == totalPlaces) soldOut = true;
    }

    public void removeSubscription(User user) throws SubscriptionException {
        if (!subscribedUsers.contains(user))
            throw new SubscriptionException("User with id='" + user.getId() + " is not subscribed to the event. Cannot unsubscribe");
        if (soldOut) soldOut = false;
        subscribedUsers.remove(user);
    }


}
