package edu.icet.hotel_management_system.model.entity;

import edu.icet.hotel_management_system.model.entity.enums.PaymentMethod;
import edu.icet.hotel_management_system.model.entity.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private String currency = "USD";

    @Column(nullable = false)
    private LocalDateTime paymentDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod method;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    private String transactionId;
    private String stripePaymentIntentId;
    private String clientSecret;
    private String receiptEmail;
    private String failureReason;

    // Cash payment fields
    private BigDecimal cashReceived;
    private BigDecimal changeAmount;
    private String cashierName;

    // Card payment fields
    private String cardHolderName;
    private String cardLastFourDigits;
    private String cardType;

    // Bank transfer fields
    private String bankName;
    private String accountNumber;
    private String referenceNumber;

    // Mobile payment fields
    private String mobileNumber;
    private String mobilePaymentProvider;

    private String notes;
    private String receiptNumber;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id")
    private Booking booking;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (paymentDate == null) {
            paymentDate = LocalDateTime.now();
        }
        if (receiptNumber == null) {
            receiptNumber = generateReceiptNumber();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    private String generateReceiptNumber() {
        return "HMS-" + System.currentTimeMillis();
    }
}
