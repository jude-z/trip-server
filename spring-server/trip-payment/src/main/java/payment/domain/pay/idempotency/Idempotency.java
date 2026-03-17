package payment.domain.pay.idempotency;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Idempotency {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "idempotency_key")
    private String idempotencyKey;

    public static Idempotency of(String idempotencyKey) {
        return Idempotency.builder()
                .idempotencyKey(idempotencyKey).build();
    }
}
