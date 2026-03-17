package core.fixture;

import core.domain.entity.destination.Destination;
import core.domain.entity.itinerary.Itinerary;
import core.domain.entity.member.Member;
import core.domain.entity.plan.Plan;
import core.domain.entity.tourspot.TourSpot;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PlanFixture {

    public static Plan.PlanBuilder defaultPlan(Member member) {
        return Plan.builder()
                .planId(1L)
                .startDate(LocalDateTime.of(2026, 4, 1, 0, 0))
                .endDate(LocalDateTime.of(2026, 4, 3, 0, 0))
                .areaCode("1")
                .member(member)
                .itineraries(new ArrayList<>());
    }

    public static Plan createPlan(Member member) {
        return defaultPlan(member).build();
    }

    public static Itinerary.ItineraryBuilder defaultItinerary(Plan plan) {
        return Itinerary.builder()
                .itineraryId(1L)
                .day(1)
                .plan(plan)
                .tourSpots(new ArrayList<>());
    }

    public static Itinerary createItinerary(Plan plan) {
        return defaultItinerary(plan).build();
    }

    public static TourSpot.TourSpotBuilder defaultTourSpot(Itinerary itinerary, Destination destination) {
        return TourSpot.builder()
                .tourSpotId(1L)
                .order(1)
                .itinerary(itinerary)
                .destination(destination);
    }

    public static TourSpot createTourSpot(Itinerary itinerary, Destination destination) {
        return defaultTourSpot(itinerary, destination).build();
    }
}
