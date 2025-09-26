package edu.icet.hotel_management_system.model.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaymentDto {
    private Long id;
    private BigDecimal amount;
    private LocalDateTime paymentDate;
    private String method;
    private String status;
    private String transactionId;
    private String stripePaymentIntentId;
    private String clientSecret;
    private Long bookingId;
    private BookingDto booking;
    private String currency = "USD";
    private String receiptEmail;
    private String failureReason;
}
