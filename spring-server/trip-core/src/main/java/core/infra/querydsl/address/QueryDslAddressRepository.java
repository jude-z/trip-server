package core.infra.querydsl.address;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import core.domain.entity.address.Address;
import core.domain.entity.address.QAddress;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class QueryDslAddressRepository{

    private final JPAQueryFactory queryFactory;

    public Optional<Address> findAddressByCodes(String areaCode, String sigunguCode) {
        QAddress address = QAddress.address;
        BooleanBuilder builder = new BooleanBuilder();

        if (isValid(areaCode)) {
            builder.and(address.areaCode.eq(areaCode));
        }
        if (isValid(sigunguCode)) {
            builder.and(address.sigunguCode.eq(sigunguCode));
        } else {
            builder.and(address.sigunguCode.isNull()); //
        }
        return Optional.ofNullable(queryFactory
                .selectFrom(address)
                .where(builder)
                .fetchFirst());
    }

    private boolean isValid(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
