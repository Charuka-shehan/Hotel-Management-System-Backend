package edu.icet.hotel_management_system.service;

import com.stripe.model.PaymentIntent;
import com.stripe.model.Refund;
import edu.icet.hotel_management_system.model.dto.PaymentDto;
import edu.icet.hotel_management_system.model.dto.PaymentRequestDto;

public interface StripeService {
    PaymentIntent createPaymentIntent(PaymentRequestDto paymentRequest) throws Exception;
    PaymentIntent confirmPayment(String paymentIntentId) throws Exception;
    Refund refundPayment(String paymentIntentId, Long amount) throws Exception;
    PaymentIntent retrievePaymentIntent(String paymentIntentId) throws Exception;
}