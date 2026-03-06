package com.tripplannerai.entity.itinerary;

import com.tripplannerai.entity.plan.Plan;
import com.tripplannerai.entity.tourspot.TourSpot;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "itinerary")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Itinerary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "itinerary_id")
    private Long itineraryId;
    private int day;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_Id", nullable = false)
    private Plan plan;

    @OneToMany(mappedBy = "itinerary", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TourSpot> tourSpots;
}
