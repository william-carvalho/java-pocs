package com.example.taxsystem.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.taxsystem.entity.TaxRule;

public interface TaxRuleRepository extends JpaRepository<TaxRule, Long> {

    boolean existsByProductCodeAndStateAndYear(String productCode, String state, Integer year);

    Optional<TaxRule> findByProductCodeAndStateAndYear(String productCode, String state, Integer year);

    @Query("select tr from TaxRule tr " +
            "where (:productCode is null or tr.productCode = :productCode) " +
            "and (:state is null or tr.state = :state) " +
            "and (:year is null or tr.year = :year) " +
            "order by tr.productCode, tr.state, tr.year")
    List<TaxRule> findByFilters(@Param("productCode") String productCode,
                                @Param("state") String state,
                                @Param("year") Integer year);
}
