package core.domain.entity.tourspot;

import core.domain.entity.destination.Destination;
import core.domain.entity.itinerary.Itinerary;
import jakarta.persistence.*;
import lombok.*;

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
