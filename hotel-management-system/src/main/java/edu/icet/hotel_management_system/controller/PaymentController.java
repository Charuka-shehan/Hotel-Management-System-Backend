package edu.icet.hotel_management_system.controller;

import edu.icet.hotel_management_system.model.dto.PaymentDto;
import edu.icet.hotel_management_system.model.dto.PaymentRequestDto;
import edu.icet.hotel_management_system.service.PaymentService;
import edu.icet.hotel_management_system.service.RolePermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@SecurityRequirement(name = "bearerAuth")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Operation(summary = "Process online payment")
    @PreAuthorize("@permissionEvaluator.hasPermission(T(edu.icet.hotel_management_system.service.RolePermissionService.Permissions).PROCESS_ONLINE_PAYMENTS)")
    @PostMapping("/online")
    public ResponseEntity<PaymentDto> processOnlinePayment(@Valid @RequestBody PaymentRequestDto paymentRequest) {
        PaymentDto paymentDto = paymentService.processPayment(paymentRequest);
        return new ResponseEntity<>(paymentDto, HttpStatus.CREATED);
    }

    @Operation(summary = "Process cash payment (Admin/Cashier only)")
    @PreAuthorize("@permissionEvaluator.hasPermission(T(edu.icet.hotel_management_system.service.RolePermissionService.Permissions).PROCESS_CASH_PAYMENTS)")
    @PostMapping("/cash")
    public ResponseEntity<PaymentDto> processCashPayment(@Valid @RequestBody PaymentRequestDto paymentRequest) {
        paymentRequest.setPaymentMethod("CASH");
        PaymentDto paymentDto = paymentService.processCashPayment(paymentRequest);
        return new ResponseEntity<>(paymentDto, HttpStatus.CREATED);
    }

    @Operation(summary = "Process card payment")
    @PreAuthorize("@permissionEvaluator.hasPermission(T(edu.icet.hotel_management_system.service.RolePermissionService.Permissions).PROCESS_CARD_PAYMENTS)")
    @PostMapping("/card")
    public ResponseEntity<PaymentDto> processCardPayment(@Valid @RequestBody PaymentRequestDto paymentRequest) {
        PaymentDto paymentDto = paymentService.processCardPayment(paymentRequest);
        return new ResponseEntity<>(paymentDto, HttpStatus.CREATED);
    }

    @Operation(summary = "Process bank transfer")
    @PreAuthorize("@permissionEvaluator.hasPermission(T(edu.icet.hotel_management_system.service.RolePermissionService.Permissions).PROCESS_BANK_TRANSFERS)")
    @PostMapping("/bank-transfer")
    public ResponseEntity<PaymentDto> processBankTransfer(@Valid @RequestBody PaymentRequestDto paymentRequest) {
        paymentRequest.setPaymentMethod("BANK_TRANSFER");
        PaymentDto paymentDto = paymentService.processBankTransfer(paymentRequest);
        return new ResponseEntity<>(paymentDto, HttpStatus.CREATED);
    }

    @Operation(summary = "Process mobile payment")
    @PreAuthorize("@permissionEvaluator.hasPermission(T(edu.icet.hotel_management_system.service.RolePermissionService.Permissions).PROCESS_MOBILE_PAYMENTS)")
    @PostMapping("/mobile")
    public ResponseEntity<PaymentDto> processMobilePayment(@Valid @RequestBody PaymentRequestDto paymentRequest) {
        paymentRequest.setPaymentMethod("MOBILE_PAYMENT");
        PaymentDto paymentDto = paymentService.processMobilePayment(paymentRequest);
        return new ResponseEntity<>(paymentDto, HttpStatus.CREATED);
    }

    @Operation(summary = "Get payment by ID")
    @PreAuthorize("@permissionEvaluator.hasPermission(T(edu.icet.hotel_management_system.service.RolePermissionService.Permissions).VIEW_ALL_PAYMENTS) or " +
            "@permissionEvaluator.canAccessPayment(#id)")
    @GetMapping("/{id}")
    public ResponseEntity<PaymentDto> getPaymentById(@PathVariable Long id) {
        PaymentDto payment = paymentService.getPaymentById(id);
        return ResponseEntity.ok(payment);
    }

    @Operation(summary = "Generate receipt")
    @PreAuthorize("@permissionEvaluator.hasPermission(T(edu.icet.hotel_management_system.service.RolePermissionService.Permissions).GENERATE_RECEIPTS)")
    @GetMapping("/{id}/receipt")
    public ResponseEntity<String> generateReceipt(@PathVariable Long id) {
        String receipt = paymentService.generateReceipt(id);
        return ResponseEntity.ok(receipt);
    }

    @Operation(summary = "Refund payment (Admin/Manager only)")
    @PreAuthorize("@permissionEvaluator.hasPermission(T(edu.icet.hotel_management_system.service.RolePermissionService.Permissions).REFUND_PAYMENTS)")
    @PostMapping("/{id}/refund")
    public ResponseEntity<PaymentDto> refundPayment(@PathVariable Long id, @RequestParam(required = false) String reason) {
        PaymentDto refundedPayment = paymentService.refundPayment(id, reason);
        return ResponseEntity.ok(refundedPayment);
    }

    @Operation(summary = "Update payment status (Admin only)")
    @PreAuthorize("@permissionEvaluator.hasPermission(T(edu.icet.hotel_management_system.service.RolePermissionService.Permissions).UPDATE_PAYMENT_STATUS)")
    @PutMapping("/{id}/status")
    public ResponseEntity<PaymentDto> updatePaymentStatus(@PathVariable Long id, @RequestParam String status) {
        PaymentDto updatedPayment = paymentService.updatePaymentStatus(id, status);
        return ResponseEntity.ok(updatedPayment);
    }

    @Operation(summary = "Get all payments (Admin only)")
    @PreAuthorize("@permissionEvaluator.hasPermission(T(edu.icet.hotel_management_system.service.RolePermissionService.Permissions).VIEW_ALL_PAYMENTS)")
    @GetMapping
    public ResponseEntity<List<PaymentDto>> getAllPayments() {
        List<PaymentDto> payments = paymentService.getAllPayments();
        return ResponseEntity.ok(payments);
    }

    @Operation(summary = "View payment statistics (Admin/Manager only)")
    @PreAuthorize("@permissionEvaluator.hasPermission(T(edu.icet.hotel_management_system.service.RolePermissionService.Permissions).VIEW_PAYMENT_STATISTICS)")
    @GetMapping("/statistics")
    public ResponseEntity<String> getPaymentStatistics() {
        return ResponseEntity.ok("Payment statistics implementation");
    }
}