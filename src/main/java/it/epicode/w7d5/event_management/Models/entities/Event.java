package it.epicode.w7d5.event_management.Models.entities;

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
    @Transient
    private int placesAvailable;

    @Column(nullable = false)
    private int totalPlaces;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "events_users",
            joinColumns = @JoinColumn(name = "event_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "userd_id", nullable = false)
    )
    private List<User> subscribedUsers = new ArrayList<>();

    public Event(String title, String description, LocalDate date, int placesAvailable, int totalPlaces) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.placesAvailable = placesAvailable;
        this.totalPlaces = totalPlaces;
    }
}
