package com.tripplannerai.entity.keep;

import com.tripplannerai.entity.destination.Destination;
import com.tripplannerai.entity.member.Member;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Table(name = "keep")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Keep {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "keep_id")
    private Long id;
    @ManyToOne
    @JoinColumn(name = "destination_id")
    private Destination destination;
    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    public static Keep of(Destination destination, Member member) {
        return Keep.builder()
                .destination(destination)
                .member(member)
                .build();
    }
}
