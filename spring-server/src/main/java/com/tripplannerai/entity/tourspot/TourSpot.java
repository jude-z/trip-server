package com.tripplannerai.entity.tourspot;

import com.tripplannerai.entity.destination.Destination;
import com.tripplannerai.entity.itinerary.Itinerary;
import com.tripplannerai.entity.member.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "tour_spot")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TourSpot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tour_spot_id")
    private Long tourSpotId;
    @Column(name = "orders")
    private Integer order;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "itinerary_id")
    private Itinerary itinerary;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_id")
    private Destination destination;

}
