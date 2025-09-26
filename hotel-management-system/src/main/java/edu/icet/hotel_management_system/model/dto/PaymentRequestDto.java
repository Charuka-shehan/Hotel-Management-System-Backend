package edu.icet.hotel_management_system.model.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

@Data
public class PaymentRequestDto {
    @NotNull
    @Positive
    private BigDecimal amount;

    @NotNull
    private String currency = "USD";

    @NotNull
    private Long bookingId;

    @NotNull
    private String paymentMethod; // CASH, CREDIT_CARD, DEBIT_CARD, PAYPAL, STRIPE, etc.

    private String receiptEmail;
    private String paymentMethodId; // For Stripe
    private boolean savePaymentMethod = false;

    // Cash payment fields
    private BigDecimal cashReceived;
    private BigDecimal changeAmount;
    private String cashierName;

    // Card payment fields
    private String cardHolderName;
    private String cardLastFourDigits;
    private String cardType; // VISA, MASTERCARD, AMEX

    // Bank transfer fields
    private String bankName;
    private String accountNumber;
    private String referenceNumber;

    // Mobile payment fields
    private String mobileNumber;
    private String mobilePaymentProvider; // bKash, Nagad, etc.

    private String notes;
}
