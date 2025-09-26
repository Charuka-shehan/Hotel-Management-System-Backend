package edu.icet.hotel_management_system.service;

import edu.icet.hotel_management_system.model.entity.Booking;
import edu.icet.hotel_management_system.model.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import java.time.format.DateTimeFormatter;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Async
    public void sendVerificationEmail(User user) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            Context context = new Context();
            context.setVariable("name", user.getFirstName() + " " + user.getLastName());
            context.setVariable("verificationUrl", "http://localhost:8080/api/auth/verify?token=" + user.getVerificationToken());

            String htmlContent = templateEngine.process("email-verification", context);

            helper.setTo(user.getEmail());
            helper.setSubject("Verify your email address");
            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send verification email", e);
        }
    }

    @Async
    public void sendPasswordResetEmail(User user) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            Context context = new Context();
            context.setVariable("name", user.getFirstName() + " " + user.getLastName());
            context.setVariable("resetUrl", "http://localhost:3000/reset-password?token=" + user.getResetToken());

            String htmlContent = templateEngine.process("password-reset", context);

            helper.setTo(user.getEmail());
            helper.setSubject("Password Reset Request");
            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send password reset email", e);
        }
    }

    @Async
    public void sendBookingConfirmationEmail(User user, Booking booking) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            Context context = new Context();
            context.setVariable("name", user.getFirstName() + " " + user.getLastName());
            context.setVariable("booking", booking);
            context.setVariable("room", booking.getRoom());
            context.setVariable("formatter", DateTimeFormatter.ofPattern("MMM dd, yyyy"));

            String htmlContent = templateEngine.process("booking-confirmation", context);

            helper.setTo(user.getEmail());
            helper.setSubject("Booking Confirmation #" + booking.getId());
            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send booking confirmation email", e);
        }
    }

    @Async
    public void sendBookingCancellationEmail(User user, Booking booking) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            Context context = new Context();
            context.setVariable("name", user.getFirstName() + " " + user.getLastName());
            context.setVariable("booking", booking);
            context.setVariable("room", booking.getRoom());
            context.setVariable("formatter", DateTimeFormatter.ofPattern("MMM dd, yyyy"));

            String htmlContent = templateEngine.process("booking-cancellation", context);

            helper.setTo(user.getEmail());
            helper.setSubject("Booking Cancellation #" + booking.getId());
            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send booking cancellation email", e);
        }
    }
}