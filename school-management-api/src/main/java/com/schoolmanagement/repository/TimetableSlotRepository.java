package com.schoolmanagement.repository;

import com.schoolmanagement.entity.TimetableSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TimetableSlotRepository extends JpaRepository<TimetableSlot, String> {
    
    List<TimetableSlot> findByTenantIdAndClassIdAndSectionId(String tenantId, String classId, String sectionId);
    
    @Query("SELECT t FROM TimetableSlot t WHERE t.tenantId = :tenantId AND t.classId = :classId AND t.sectionId = :sectionId AND t.dayOfWeek.id = :dayOfWeekId")
    List<TimetableSlot> findByClassAndSectionAndDay(@Param("tenantId") String tenantId,
                                                     @Param("classId") String classId,
                                                     @Param("sectionId") String sectionId,
                                                     @Param("dayOfWeekId") String dayOfWeekId);
    
    @Query("SELECT t FROM TimetableSlot t WHERE t.tenantId = :tenantId AND t.staffId = :staffId ORDER BY t.dayOfWeek.sortOrder, t.periodNumber")
    List<TimetableSlot> findTeacherTimetable(@Param("tenantId") String tenantId, @Param("staffId") String staffId);
    
    @Query("SELECT t FROM TimetableSlot t WHERE t.tenantId = :tenantId AND t.classId = :classId AND t.sectionId = :sectionId AND t.academicYearId = :academicYearId ORDER BY t.dayOfWeek.sortOrder, t.periodNumber")
    List<TimetableSlot> findClassTimetable(@Param("tenantId") String tenantId,
                                           @Param("classId") String classId,
                                           @Param("sectionId") String sectionId,
                                           @Param("academicYearId") String academicYearId);
}