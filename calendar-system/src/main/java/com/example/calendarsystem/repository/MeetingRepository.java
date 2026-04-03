package com.example.calendarsystem.repository;

import com.example.calendarsystem.entity.Meeting;
import com.example.calendarsystem.entity.MeetingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface MeetingRepository extends JpaRepository<Meeting, Long> {

    List<Meeting> findByStatusOrderByStartDateTimeAsc(MeetingStatus status);

    List<Meeting> findAllByOrderByStartDateTimeAsc();

    List<Meeting> findByStartDateTimeBetweenOrderByStartDateTimeAsc(LocalDateTime start, LocalDateTime end);

    List<Meeting> findByStatusAndStartDateTimeBetweenOrderByStartDateTimeAsc(MeetingStatus status, LocalDateTime start, LocalDateTime end);

    @Query("select distinct m from Meeting m left join m.participants p " +
            "where m.status = :status and (m.organizer.id = :personId or p.id = :personId) " +
            "order by m.startDateTime asc")
    List<Meeting> findByPersonIdAndStatus(@Param("personId") Long personId, @Param("status") MeetingStatus status);

    @Query("select distinct m from Meeting m left join m.participants p " +
            "where (m.organizer.id = :personId or p.id = :personId) order by m.startDateTime asc")
    List<Meeting> findByPersonId(@Param("personId") Long personId);

    @Query("select distinct m from Meeting m left join m.participants p " +
            "where m.status = :status and (m.organizer.id in :personIds or p.id in :personIds) " +
            "and m.startDateTime < :searchEnd and m.endDateTime > :searchStart order by m.startDateTime asc")
    List<Meeting> findConflictingMeetings(
            @Param("personIds") List<Long> personIds,
            @Param("status") MeetingStatus status,
            @Param("searchStart") LocalDateTime searchStart,
            @Param("searchEnd") LocalDateTime searchEnd
    );
}
