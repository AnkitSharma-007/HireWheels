package com.upgrad.hirewheels.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name="BOOKING")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int bookingId;
    Date pickUpDate;
    Date dropOffDate;
    Date bookingDate;
    int amount;
    @ManyToOne(fetch = FetchType.LAZY,cascade
            = CascadeType.MERGE)
    @JoinColumn(name = "userId")
    @JsonBackReference
    Users bookingWithUser;
    @ManyToOne(fetch = FetchType.LAZY,cascade
            = CascadeType.MERGE)
    @JoinColumn(name = "locationId")
    @JsonBackReference
    Location locationWithBooking;
    @ManyToOne(fetch = FetchType.LAZY,cascade
            = CascadeType.MERGE)
    @JoinColumn(name = "vehicleId")
    @JsonBackReference
    Vehicle vehicleWithBooking;
}
