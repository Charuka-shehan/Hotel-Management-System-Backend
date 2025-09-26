package edu.icet.hotel_management_system.service;

import edu.icet.hotel_management_system.model.dto.PaymentDto;
import edu.icet.hotel_management_system.model.dto.PaymentRequestDto;
import java.util.List;

public interface PaymentService {
    // Stripe payments
    PaymentDto createPaymentIntent(PaymentRequestDto paymentRequest);
    PaymentDto confirmPayment(String paymentIntentId);

    // Cash payments
    PaymentDto processCashPayment(PaymentRequestDto paymentRequest);

    // Card payments (non-Stripe)
    PaymentDto processCardPayment(PaymentRequestDto paymentRequest);

    // Bank transfer
    PaymentDto processBankTransfer(PaymentRequestDto paymentRequest);

    // Mobile payments
    PaymentDto processMobilePayment(PaymentRequestDto paymentRequest);

    // General payment processing
    PaymentDto processPayment(PaymentRequestDto paymentRequest);

    PaymentDto getPaymentById(Long id);
    List<PaymentDto> getPaymentsByBookingId(Long bookingId);
    PaymentDto updatePaymentStatus(Long id, String status);
    PaymentDto refundPayment(Long id, String reason);
    List<PaymentDto> getAllPayments();
    void handleWebhook(String payload, String sigHeader);

    // Receipt generation
    String generateReceipt(Long paymentId);
}
