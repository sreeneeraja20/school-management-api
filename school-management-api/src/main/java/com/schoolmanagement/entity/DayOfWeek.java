package com.schoolmanagement.entity;

import com.schoolmanagement.entity.base.AuditableEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "days_of_week", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"tenant_id", "code"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DayOfWeek extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "tenant_id", nullable = false)
    private String tenantId;

    @Column(name = "code", nullable = false)
    private String code;  // "MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN"

    @Column(name = "name", nullable = false)
    private String name;  // "Monday", "Tuesday", "Wednesday", etc.

    @Column(name = "sort_order")
    private Integer sortOrder;  // 1, 2, 3, etc.

    @Column(name = "is_working_day")
    private Boolean isWorkingDay = true;  // Some schools don't work on Saturday

    @Column(name = "is_active")
    private Boolean isActive = true;
}