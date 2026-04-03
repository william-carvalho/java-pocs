package com.example.logisticfreightsystem.repository;

import com.example.logisticfreightsystem.entity.FreightPricingRule;
import com.example.logisticfreightsystem.enums.TransportType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface FreightPricingRuleRepository extends JpaRepository<FreightPricingRule, Long> {

    @Query("select r from FreightPricingRule r " +
            "where (:transportType is null or r.transportType = :transportType) " +
            "and (:referenceDate is null or :referenceDate between r.effectiveFrom and r.effectiveTo) " +
            "order by r.transportType asc, r.effectiveFrom desc")
    List<FreightPricingRule> findAllByFilters(@Param("transportType") TransportType transportType,
                                              @Param("referenceDate") LocalDate referenceDate);

    @Query("select case when count(r) > 0 then true else false end from FreightPricingRule r " +
            "where r.transportType = :transportType " +
            "and r.effectiveFrom <= :effectiveTo " +
            "and r.effectiveTo >= :effectiveFrom")
    boolean existsOverlappingRule(@Param("transportType") TransportType transportType,
                                  @Param("effectiveFrom") LocalDate effectiveFrom,
                                  @Param("effectiveTo") LocalDate effectiveTo);

    Optional<FreightPricingRule> findFirstByTransportTypeAndEffectiveFromLessThanEqualAndEffectiveToGreaterThanEqual(
            TransportType transportType, LocalDate referenceDateFrom, LocalDate referenceDateTo);
}
