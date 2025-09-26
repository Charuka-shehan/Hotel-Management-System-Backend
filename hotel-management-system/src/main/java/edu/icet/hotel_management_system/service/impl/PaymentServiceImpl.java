package edu.icet.hotel_management_system.service.impl;

import com.stripe.model.PaymentIntent;
import com.stripe.model.Refund;
import edu.icet.hotel_management_system.exception.ResourceNotFoundException;
import edu.icet.hotel_management_system.model.dto.PaymentDto;
import edu.icet.hotel_management_system.model.dto.PaymentRequestDto;
import edu.icet.hotel_management_system.model.entity.Booking;
import edu.icet.hotel_management_system.model.entity.Payment;
import edu.icet.hotel_management_system.model.entity.enums.PaymentMethod;
import edu.icet.hotel_management_system.model.entity.enums.PaymentStatus;
import edu.icet.hotel_management_system.repository.BookingRepository;
import edu.icet.hotel_management_system.repository.PaymentRepository;
import edu.icet.hotel_management_system.service.PaymentService;
import edu.icet.hotel_management_system.service.StripeService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentServiceImpl.class);

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private StripeService stripeService;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public PaymentDto processPayment(PaymentRequestDto paymentRequest) {
        logger.info("Processing payment for booking: {} using method: {}",
                paymentRequest.getBookingId(), paymentRequest.getPaymentMethod());

        PaymentMethod method = PaymentMethod.valueOf(paymentRequest.getPaymentMethod().toUpperCase());

        switch (method) {
            case CASH:
                return processCashPayment(paymentRequest);
            case CREDIT_CARD:
            case DEBIT_CARD:
                if (paymentRequest.getPaymentMethodId() != null) {
                    return createPaymentIntent(paymentRequest); // Stripe payment
                } else {
                    return processCardPayment(paymentRequest); // Manual card processing
                }
            case STRIPE:
                return createPaymentIntent(paymentRequest);
            case BANK_TRANSFER:
                return processBankTransfer(paymentRequest);
            case MOBILE_PAYMENT:
                return processMobilePayment(paymentRequest);
            case PAYPAL:
                return processPayPalPayment(paymentRequest);
            default:
                throw new IllegalArgumentException("Unsupported payment method: " + method);
        }
    }

    @Override
    public PaymentDto processCashPayment(PaymentRequestDto paymentRequest) {
        logger.info("Processing cash payment for booking: {}", paymentRequest.getBookingId());

        Booking booking = bookingRepository.findById(paymentRequest.getBookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking", "id", paymentRequest.getBookingId()));

        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setAmount(paymentRequest.getAmount());
        payment.setCurrency(paymentRequest.getCurrency());
        payment.setMethod(PaymentMethod.CASH);
        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setCashReceived(paymentRequest.getCashReceived());

        // Calculate change
        if (paymentRequest.getCashReceived() != null) {
            BigDecimal change = paymentRequest.getCashReceived().subtract(paymentRequest.getAmount());
            payment.setChangeAmount(change.compareTo(BigDecimal.ZERO) > 0 ? change : BigDecimal.ZERO);
        }

        payment.setCashierName(paymentRequest.getCashierName());
        payment.setNotes(paymentRequest.getNotes());
        payment.setTransactionId("CASH-" + System.currentTimeMillis());

        Payment savedPayment = paymentRepository.save(payment);
        return modelMapper.map(savedPayment, PaymentDto.class);
    }

    @Override
    public PaymentDto processCardPayment(PaymentRequestDto paymentRequest) {
        logger.info("Processing card payment for booking: {}", paymentRequest.getBookingId());

        Booking booking = bookingRepository.findById(paymentRequest.getBookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking", "id", paymentRequest.getBookingId()));

        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setAmount(paymentRequest.getAmount());
        payment.setCurrency(paymentRequest.getCurrency());
        payment.setMethod(PaymentMethod.valueOf(paymentRequest.getPaymentMethod().toUpperCase()));
        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setCardHolderName(paymentRequest.getCardHolderName());
        payment.setCardLastFourDigits(paymentRequest.getCardLastFourDigits());
        payment.setCardType(paymentRequest.getCardType());
        payment.setNotes(paymentRequest.getNotes());
        payment.setTransactionId("CARD-" + System.currentTimeMillis());

        Payment savedPayment = paymentRepository.save(payment);
        return modelMapper.map(savedPayment, PaymentDto.class);
    }

    @Override
    public PaymentDto processBankTransfer(PaymentRequestDto paymentRequest) {
        logger.info("Processing bank transfer for booking: {}", paymentRequest.getBookingId());

        Booking booking = bookingRepository.findById(paymentRequest.getBookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking", "id", paymentRequest.getBookingId()));

        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setAmount(paymentRequest.getAmount());
        payment.setCurrency(paymentRequest.getCurrency());
        payment.setMethod(PaymentMethod.BANK_TRANSFER);
        payment.setStatus(PaymentStatus.AWAITING_CONFIRMATION);
        payment.setBankName(paymentRequest.getBankName());
        payment.setAccountNumber(paymentRequest.getAccountNumber());
        payment.setReferenceNumber(paymentRequest.getReferenceNumber());
        payment.setNotes(paymentRequest.getNotes());
        payment.setTransactionId("BANK-" + System.currentTimeMillis());

        Payment savedPayment = paymentRepository.save(payment);
        return modelMapper.map(savedPayment, PaymentDto.class);
    }

    @Override
    public PaymentDto processMobilePayment(PaymentRequestDto paymentRequest) {
        logger.info("Processing mobile payment for booking: {}", paymentRequest.getBookingId());

        Booking booking = bookingRepository.findById(paymentRequest.getBookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking", "id", paymentRequest.getBookingId()));

        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setAmount(paymentRequest.getAmount());
        payment.setCurrency(paymentRequest.getCurrency());
        payment.setMethod(PaymentMethod.MOBILE_PAYMENT);
        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setMobileNumber(paymentRequest.getMobileNumber());
        payment.setMobilePaymentProvider(paymentRequest.getMobilePaymentProvider());
        payment.setNotes(paymentRequest.getNotes());
        payment.setTransactionId("MOBILE-" + System.currentTimeMillis());

        Payment savedPayment = paymentRepository.save(payment);
        return modelMapper.map(savedPayment, PaymentDto.class);
    }

    private PaymentDto processPayPalPayment(PaymentRequestDto paymentRequest) {
        logger.info("Processing PayPal payment for booking: {}", paymentRequest.getBookingId());

        Booking booking = bookingRepository.findById(paymentRequest.getBookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking", "id", paymentRequest.getBookingId()));

        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setAmount(paymentRequest.getAmount());
        payment.setCurrency(paymentRequest.getCurrency());
        payment.setMethod(PaymentMethod.PAYPAL);
        payment.setStatus(PaymentStatus.PENDING);
        payment.setReceiptEmail(paymentRequest.getReceiptEmail());
        payment.setNotes(paymentRequest.getNotes());
        payment.setTransactionId("PAYPAL-" + System.currentTimeMillis());

        Payment savedPayment = paymentRepository.save(payment);
        return modelMapper.map(savedPayment, PaymentDto.class);
    }

    @Override
    public PaymentDto createPaymentIntent(PaymentRequestDto paymentRequest) {
        logger.info("Creating payment intent for booking: {}", paymentRequest.getBookingId());

        try {
            Booking booking = bookingRepository.findById(paymentRequest.getBookingId())
                    .orElseThrow(() -> new ResourceNotFoundException("Booking", "id", paymentRequest.getBookingId()));

            PaymentIntent paymentIntent = stripeService.createPaymentIntent(paymentRequest);

            Payment payment = new Payment();
            payment.setBooking(booking);
            payment.setAmount(paymentRequest.getAmount());
            payment.setCurrency(paymentRequest.getCurrency());
            payment.setMethod(PaymentMethod.STRIPE);
            payment.setStatus(PaymentStatus.PENDING);
            payment.setStripePaymentIntentId(paymentIntent.getId());
            payment.setClientSecret(paymentIntent.getClientSecret());
            payment.setReceiptEmail(paymentRequest.getReceiptEmail());

            Payment savedPayment = paymentRepository.save(payment);
            return modelMapper.map(savedPayment, PaymentDto.class);

        } catch (Exception e) {
            logger.error("Failed to create payment intent for booking: {}", paymentRequest.getBookingId(), e);
            throw new RuntimeException("Failed to create payment intent: " + e.getMessage());
        }
    }

    @Override
    public PaymentDto confirmPayment(String paymentIntentId) {
        logger.info("Confirming payment with PaymentIntent: {}", paymentIntentId);

        try {
            Payment payment = paymentRepository.findByStripePaymentIntentId(paymentIntentId)
                    .orElseThrow(() -> new ResourceNotFoundException("Payment", "stripePaymentIntentId", paymentIntentId));

            PaymentIntent paymentIntent = stripeService.confirmPayment(paymentIntentId);

            if ("succeeded".equals(paymentIntent.getStatus())) {
                payment.setStatus(PaymentStatus.COMPLETED);
                payment.setTransactionId(paymentIntent.getId());
            } else if ("requires_action".equals(paymentIntent.getStatus())) {
                payment.setStatus(PaymentStatus.PENDING);
            } else {
                payment.setStatus(PaymentStatus.FAILED);
                payment.setFailureReason("Payment failed during confirmation");
            }

            Payment updatedPayment = paymentRepository.save(payment);
            return modelMapper.map(updatedPayment, PaymentDto.class);

        } catch (Exception e) {
            logger.error("Failed to confirm payment with PaymentIntent: {}", paymentIntentId, e);
            throw new RuntimeException("Failed to confirm payment: " + e.getMessage());
        }
    }

    @Override
    public PaymentDto getPaymentById(Long id) {
        logger.info("Fetching payment with id: {}", id);
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", id));
        return modelMapper.map(payment, PaymentDto.class);
    }

    @Override
    public List<PaymentDto> getPaymentsByBookingId(Long bookingId) {
        logger.info("Fetching payments for booking: {}", bookingId);
        List<Payment> payments = paymentRepository.findByBookingId(bookingId);
        return payments.stream()
                .map(payment -> modelMapper.map(payment, PaymentDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public PaymentDto updatePaymentStatus(Long id, String status) {
        logger.info("Updating payment {} status to: {}", id, status);
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", id));

        payment.setStatus(PaymentStatus.valueOf(status.toUpperCase()));
        Payment updatedPayment = paymentRepository.save(payment);
        return modelMapper.map(updatedPayment, PaymentDto.class);
    }

    @Override
    public PaymentDto refundPayment(Long id, String reason) {
        logger.info("Processing refund for payment: {}", id);
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", id));

        if (payment.getStatus() != PaymentStatus.COMPLETED) {
            throw new IllegalStateException("Cannot refund a payment that is not completed");
        }

        try {
            if (payment.getStripePaymentIntentId() != null) {
                Long amountInCents = payment.getAmount().multiply(BigDecimal.valueOf(100)).longValue();
                Refund refund = stripeService.refundPayment(payment.getStripePaymentIntentId(), amountInCents);
                payment.setTransactionId(refund.getId());
            }

            payment.setStatus(PaymentStatus.REFUNDED);
            payment.setFailureReason(reason);
            Payment refundedPayment = paymentRepository.save(payment);
            return modelMapper.map(refundedPayment, PaymentDto.class);

        } catch (Exception e) {
            logger.error("Failed to process refund for payment: {}", id, e);
            throw new RuntimeException("Failed to process refund: " + e.getMessage());
        }
    }

    @Override
    public List<PaymentDto> getAllPayments() {
        logger.info("Fetching all payments");
        List<Payment> payments = paymentRepository.findAll();
        return payments.stream()
                .map(payment -> modelMapper.map(payment, PaymentDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public void handleWebhook(String payload, String sigHeader) {
        logger.info("Processing Stripe webhook");
        // Implementation for handling Stripe webhooks
    }

    @Override
    public String generateReceipt(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", paymentId));

        StringBuilder receipt = new StringBuilder();
        receipt.append("=== HOTEL MANAGEMENT SYSTEM ===\n");
        receipt.append("Receipt Number: ").append(payment.getReceiptNumber()).append("\n");
        receipt.append("Date: ").append(payment.getPaymentDate()).append("\n");
        receipt.append("Booking ID: ").append(payment.getBooking().getId()).append("\n");
        receipt.append("Room: ").append(payment.getBooking().getRoom().getRoomNumber()).append("\n");
        receipt.append("Guest: ").append(payment.getBooking().getUser().getFirstName())
                .append(" ").append(payment.getBooking().getUser().getLastName()).append("\n");
        receipt.append("Amount: ").append(payment.getCurrency()).append(" ")
                .append(payment.getAmount()).append("\n");
        receipt.append("Payment Method: ").append(payment.getMethod()).append("\n");

        if (payment.getMethod() == PaymentMethod.CASH) {
            receipt.append("Cash Received: ").append(payment.getCashReceived()).append("\n");
            receipt.append("Change: ").append(payment.getChangeAmount()).append("\n");
            receipt.append("Cashier: ").append(payment.getCashierName()).append("\n");
        }

        receipt.append("Status: ").append(payment.getStatus()).append("\n");
        receipt.append("Transaction ID: ").append(payment.getTransactionId()).append("\n");
        receipt.append("===============================\n");

        return receipt.toString();
    }
}
