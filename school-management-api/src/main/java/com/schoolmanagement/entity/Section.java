package com.schoolmanagement.entity;

import com.schoolmanagement.entity.base.AuditableEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "sections", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"class_id", "name"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Section extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "tenant_id", nullable = false)
    private String tenantId;

    @Column(name = "class_id", nullable = false)
    private String classId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "max_students")
    private Integer maxStudents = 40;
}