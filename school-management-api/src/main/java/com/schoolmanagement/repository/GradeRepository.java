package com.schoolmanagement.repository;

import com.schoolmanagement.entity.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GradeRepository extends JpaRepository<Grade, String> {
    
    Optional<Grade> findByExamSubjectIdAndStudentId(String examSubjectId, String studentId);
    
    List<Grade> findByExamSubjectId(String examSubjectId);
    
    @Query("SELECT g FROM Grade g WHERE g.tenantId = :tenantId AND g.examSubjectId IN (SELECT es.id FROM ExamSubject es WHERE es.examId = :examId)")
    List<Grade> findByExamId(@Param("tenantId") String tenantId, @Param("examId") String examId);
    
    @Query("SELECT g FROM Grade g WHERE g.tenantId = :tenantId AND g.studentId = :studentId AND g.examSubjectId IN (SELECT es.id FROM ExamSubject es WHERE es.examId = :examId)")
    List<Grade> findStudentExamGrades(@Param("tenantId") String tenantId,
                                      @Param("studentId") String studentId,
                                      @Param("examId") String examId);
}