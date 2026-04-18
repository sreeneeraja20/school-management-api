package com.schoolmanagement.entity;

import com.schoolmanagement.entity.base.AuditableEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "parent_accounts", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"tenant_id", "email"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParentAccount extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "tenant_id", nullable = false)
    private String tenantId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", nullable = false, length = 255)
    private String email;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @ManyToOne
    @JoinColumn(name = "relation_id")
    private Relation relation;

    @Column(name = "student_ids", columnDefinition = "NVARCHAR(MAX)")
    private String studentIds;

    @Column(name = "is_first_login")
    private Boolean isFirstLogin = true;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @Column(name = "last_login_device", length = 255)
    private String lastLoginDevice;

    @Column(name = "last_login_platform", length = 100)
    private String lastLoginPlatform;

    @Column(name = "last_login_ip", length = 50)
    private String lastLoginIp;

    @Column(name = "login_count")
    private Integer loginCount = 0;

    @Column(name = "preferred_language", length = 5)
    private String preferredLanguage = "en";

    @Column(name = "notify_attendance")
    private Boolean notifyAttendance = true;

    @Column(name = "notify_events")
    private Boolean notifyEvents = true;

    @Column(name = "notify_exams")
    private Boolean notifyExams = true;

    @Column(name = "fcm_token", length = 500)
    private String fcmToken;
}