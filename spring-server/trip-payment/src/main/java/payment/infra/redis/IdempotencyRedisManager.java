package payment.infra.redis;

import core.common.Status;
import core.common.exception.CommonException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import payment.domain.pay.status.IdempotencyStatus;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Component
@RequiredArgsConstructor
public class IdempotencyRedisManager {

    private final RedisTemplate<String, String> redisTemplate;
    private static final Duration TTL = Duration.of(24L, ChronoUnit.HOURS);

    /**
     * 멱등성 키 선점 (PROCESSING 상태로 저장)
     * @return true: 선점 성공 / false: 이미 존재
     */
    public boolean tryAcquire(String key) {
        Boolean success = redisTemplate.opsForValue()
                .setIfAbsent(key, IdempotencyStatus.PROCESSING.getValue(), TTL);
        if (success == null) throw new CommonException(Status.REDIS_SERVER_ERROR);
        return success;
    }

    /**
     * 결제 성공 시 COMPLETED로 변경
     */
    public void complete(String key) {
        redisTemplate.opsForValue().set(key, IdempotencyStatus.COMPLETED.getValue(), TTL);
    }

    /**
     * 결제 실패 시 키 삭제 (재시도 가능하도록)
     */
    public void fail(String key) {
        redisTemplate.delete(key);
    }

    /**
     * 현재 상태 조회
     */
    public IdempotencyStatus getStatus(String key) {
        String value = redisTemplate.opsForValue().get(key);
        if (value == null) return null;
        return IdempotencyStatus.valueOf(value);
    }
}
