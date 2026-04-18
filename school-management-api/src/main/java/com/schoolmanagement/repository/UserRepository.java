package com.schoolmanagement.repository;

import com.schoolmanagement.entity.User;
import com.schoolmanagement.entity.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String>, JpaSpecificationExecutor<User> {
    
    Optional<User> findByEmailAndTenantId(String email, String tenantId);
    
    boolean existsByEmailAndTenantId(String email, String tenantId);

    Optional<User> findByIdAndTenantId(String id, String tenantId);
    
    Page<User> findByTenantId(String tenantId, Pageable pageable);
    
    List<User> findByTenantIdAndUserRole(String tenantId, UserRole userRole);
    
    @Query("SELECT u FROM User u WHERE u.tenantId = :tenantId AND (u.name LIKE %:search% OR u.email LIKE %:search%)")
    Page<User> searchUsers(@Param("tenantId") String tenantId, 
                           @Param("search") String search, 
                           Pageable pageable);
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.tenantId = :tenantId AND u.userRole.name = 'ADMIN' AND u.isActive = true")
    long countActiveAdmins(@Param("tenantId") String tenantId);
}
