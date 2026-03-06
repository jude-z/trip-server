package com.tripplannerai.repository.address;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tripplannerai.entity.address.Address;
import com.tripplannerai.entity.address.QAddress;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class AddressCustomRepositoryImpl implements AddressCustomRepository{

    private final JPAQueryFactory queryFactory;

    @Override
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
