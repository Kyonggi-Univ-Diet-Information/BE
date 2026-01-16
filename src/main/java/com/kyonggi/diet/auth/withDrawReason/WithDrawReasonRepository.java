package com.kyonggi.diet.auth.withDrawReason;

import com.kyonggi.diet.auth.withDrawReason.domain.WithDrawReason;
import com.kyonggi.diet.auth.withDrawReason.domain.WithDrawReasonType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface WithDrawReasonRepository extends JpaRepository<WithDrawReason, Long> {
    @Query("""
    select w.type, count(w)
    from WithDrawReason w
    group by w.type
    """)
    List<Object[]> countGroupByType();

    List<WithDrawReason> findByType(WithDrawReasonType type);
}