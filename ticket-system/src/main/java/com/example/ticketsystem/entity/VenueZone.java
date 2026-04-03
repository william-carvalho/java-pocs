package com.example.ticketsystem.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "venue_zones")
public class VenueZone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "venue_id", nullable = false)
    private Venue venue;

    @Column(nullable = false, length = 80)
    private String name;

    @Column(nullable = false)
    private Integer maxCapacity;

    @Column(length = 10)
    private String seatPrefix;

    @Column(nullable = false)
    private Integer seatStart;

    @Column(nullable = false)
    private Integer seatEnd;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Venue getVenue() {
        return venue;
    }

    public void setVenue(Venue venue) {
        this.venue = venue;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(Integer maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public String getSeatPrefix() {
        return seatPrefix;
    }

    public void setSeatPrefix(String seatPrefix) {
        this.seatPrefix = seatPrefix;
    }

    public Integer getSeatStart() {
        return seatStart;
    }

    public void setSeatStart(Integer seatStart) {
        this.seatStart = seatStart;
    }

    public Integer getSeatEnd() {
        return seatEnd;
    }

    public void setSeatEnd(Integer seatEnd) {
        this.seatEnd = seatEnd;
    }
}
