package edu.icet.hotel_management_system.repository;

import edu.icet.hotel_management_system.model.entity.Payment;
import edu.icet.hotel_management_system.model.entity.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByBookingId(Long bookingId);
    List<Payment> findByStatus(PaymentStatus status);
    List<Payment> findByTransactionId(String transactionId);
    Optional<Payment> findByStripePaymentIntentId(String stripePaymentIntentId);
}
