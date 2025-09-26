package edu.icet.hotel_management_system.service.impl;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Refund;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.PaymentIntentConfirmParams;
import com.stripe.param.RefundCreateParams;
import edu.icet.hotel_management_system.model.dto.PaymentRequestDto;
import edu.icet.hotel_management_system.service.StripeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class StripeServiceImpl implements StripeService {

    private static final Logger logger = LoggerFactory.getLogger(StripeServiceImpl.class);

    @Override
    public PaymentIntent createPaymentIntent(PaymentRequestDto paymentRequest) throws StripeException {
        logger.info("Creating PaymentIntent for booking: {}, amount: {}",
                paymentRequest.getBookingId(), paymentRequest.getAmount());

        // Convert amount to cents (Stripe uses smallest currency unit)
        Long amountInCents = paymentRequest.getAmount().multiply(java.math.BigDecimal.valueOf(100)).longValue();

        PaymentIntentCreateParams.Builder paramsBuilder = PaymentIntentCreateParams.builder()
                .setAmount(amountInCents)
                .setCurrency(paymentRequest.getCurrency().toLowerCase())
                .setAutomaticPaymentMethods(
                        PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                .setEnabled(true)
                                .build()
                );

        // Add metadata
        Map<String, String> metadata = new HashMap<>();
        metadata.put("booking_id", paymentRequest.getBookingId().toString());
        metadata.put("integration", "hotel_management_system");
        paramsBuilder.putAllMetadata(metadata);

        // Add receipt email if provided
        if (paymentRequest.getReceiptEmail() != null && !paymentRequest.getReceiptEmail().isEmpty()) {
            paramsBuilder.setReceiptEmail(paymentRequest.getReceiptEmail());
        }

        PaymentIntentCreateParams params = paramsBuilder.build();
        return PaymentIntent.create(params);
    }

    @Override
    public PaymentIntent confirmPayment(String paymentIntentId) throws StripeException {
        logger.info("Confirming PaymentIntent: {}", paymentIntentId);

        PaymentIntentConfirmParams params = PaymentIntentConfirmParams.builder()
                .setReturnUrl("http://localhost:3000/payment/success") // Your frontend success URL
                .build();

        PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
        return paymentIntent.confirm(params);
    }

    @Override
    public Refund refundPayment(String paymentIntentId, Long amountInCents) throws StripeException {
        logger.info("Creating refund for PaymentIntent: {}, amount: {}", paymentIntentId, amountInCents);

        RefundCreateParams.Builder paramsBuilder = RefundCreateParams.builder()
                .setPaymentIntent(paymentIntentId);

        if (amountInCents != null) {
            paramsBuilder.setAmount(amountInCents);
        }

        RefundCreateParams params = paramsBuilder.build();
        return Refund.create(params);
    }

    @Override
    public PaymentIntent retrievePaymentIntent(String paymentIntentId) throws StripeException {
        return PaymentIntent.retrieve(paymentIntentId);
    }
}
