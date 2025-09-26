package edu.icet.hotel_management_system.repository;

import edu.icet.hotel_management_system.model.entity.User;
import edu.icet.hotel_management_system.model.entity.enums.ERole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    User findByVerificationToken(String token);
    User findByResetToken(String token);

    // Fixed method signatures
    List<User> findByRole(ERole role);
    long countByRole(ERole role);
    long countByEnabledTrue();

    @Query("SELECT COUNT(u) FROM User u WHERE u.enabled = true AND u.role = 'USER'")
    long countActiveCustomers();
}