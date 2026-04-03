package com.example.ticketsystem.repository;

import com.example.ticketsystem.entity.Ticket;
import com.example.ticketsystem.enums.TicketStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    @Query("select count(t) from Ticket t where t.session.id = :sessionId and t.status = :status")
    long countBySessionIdAndStatus(@Param("sessionId") Long sessionId, @Param("status") TicketStatus status);

    @Query("select count(t) from Ticket t where t.session.id = :sessionId and t.zone.id = :zoneId and t.status = :status")
    long countBySessionIdAndZoneIdAndStatus(@Param("sessionId") Long sessionId,
                                            @Param("zoneId") Long zoneId,
                                            @Param("status") TicketStatus status);

    boolean existsBySessionIdAndZoneIdAndSeatNumberIgnoreCaseAndStatus(Long sessionId,
                                                                       Long zoneId,
                                                                       String seatNumber,
                                                                       TicketStatus status);

    @EntityGraph(attributePaths = {"session", "session.show", "session.venue", "zone"})
    @Query("select t from Ticket t " +
            "where (:sessionId is null or t.session.id = :sessionId) " +
            "and (:zoneId is null or t.zone.id = :zoneId) " +
            "and (:status is null or t.status = :status) " +
            "order by t.soldAt desc, t.id desc")
    List<Ticket> findAllByFilters(@Param("sessionId") Long sessionId,
                                  @Param("zoneId") Long zoneId,
                                  @Param("status") TicketStatus status);

    @EntityGraph(attributePaths = {"session", "session.show", "session.venue", "zone"})
    List<Ticket> findBySessionIdAndStatusOrderByZoneIdAscSeatNumberAsc(Long sessionId, TicketStatus status);

    @Override
    @EntityGraph(attributePaths = {"session", "session.show", "session.venue", "zone"})
    Optional<Ticket> findById(Long id);
}
