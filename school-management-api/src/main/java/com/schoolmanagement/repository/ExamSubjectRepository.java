package com.schoolmanagement.repository;

import com.schoolmanagement.entity.ExamSubject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamSubjectRepository extends JpaRepository<ExamSubject, String> {
    
    List<ExamSubject> findByExamId(String examId);
    
    @Query("SELECT es FROM ExamSubject es WHERE es.tenantId = :tenantId AND es.examId = :examId")
    List<ExamSubject> findByExamIdAndTenant(@Param("tenantId") String tenantId, @Param("examId") String examId);
    
    @Query("SELECT es FROM ExamSubject es WHERE es.tenantId = :tenantId AND es.examId = :examId AND es.classId = :classId")
    List<ExamSubject> findByExamAndClassId(@Param("tenantId") String tenantId,
                                           @Param("examId") String examId,
                                           @Param("classId") String classId);
}