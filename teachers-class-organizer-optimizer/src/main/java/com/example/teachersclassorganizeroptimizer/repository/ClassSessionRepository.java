package com.example.teachersclassorganizeroptimizer.repository;

import com.example.teachersclassorganizeroptimizer.entity.ClassSession;
import com.example.teachersclassorganizeroptimizer.entity.SessionStatus;
import java.time.DayOfWeek;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ClassSessionRepository extends JpaRepository<ClassSession, Long> {

    List<ClassSession> findByStatusOrderByDayOfWeekAscStartTimeAsc(SessionStatus status);

    List<ClassSession> findByTeacherIdOrderByDayOfWeekAscStartTimeAsc(Long teacherId);

    List<ClassSession> findBySchoolClassIdOrderByDayOfWeekAscStartTimeAsc(Long schoolClassId);

    List<ClassSession> findByRoomIdOrderByDayOfWeekAscStartTimeAsc(Long roomId);

    List<ClassSession> findByDayOfWeekOrderByStartTimeAsc(DayOfWeek dayOfWeek);

    @Query("select s from ClassSession s where " +
            "(:teacherId is null or s.teacher.id = :teacherId) and " +
            "(:schoolClassId is null or s.schoolClass.id = :schoolClassId) and " +
            "(:roomId is null or s.room.id = :roomId) and " +
            "(:dayOfWeek is null or s.dayOfWeek = :dayOfWeek) and " +
            "(:status is null or s.status = :status) " +
            "order by s.dayOfWeek asc, s.startTime asc")
    List<ClassSession> search(
            @Param("teacherId") Long teacherId,
            @Param("schoolClassId") Long schoolClassId,
            @Param("roomId") Long roomId,
            @Param("dayOfWeek") DayOfWeek dayOfWeek,
            @Param("status") SessionStatus status
    );

    @Query("select s from ClassSession s where s.status = :status and s.dayOfWeek = :dayOfWeek order by s.startTime asc")
    List<ClassSession> findScheduledByDay(@Param("dayOfWeek") DayOfWeek dayOfWeek, @Param("status") SessionStatus status);
}
