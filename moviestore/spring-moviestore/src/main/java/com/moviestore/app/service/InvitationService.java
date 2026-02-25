package com.moviestore.app.service;

import com.moviestore.app.model.Reservation;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InvitationService {
  private final JavaMailSender mailSender;

  @Value("${app.mail-from:no-reply@moviestore.local}")
  private String mailFrom;

  @Value("${app.base-url:http://localhost:8080}")
  private String baseUrl;

  public InvitationService(JavaMailSender mailSender) {
    this.mailSender = mailSender;
  }

  public void sendReservationInvitations(Reservation reservation, String hostName, List<String> recipients) {
    for (String to : recipients) {
      sendOne(reservation, hostName, to.trim());
    }
  }

  private void sendOne(Reservation reservation, String hostName, String to) {
    if (to.isBlank()) return;
    try {
      MimeMessage msg = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(msg, true, "UTF-8");
      helper.setFrom(mailFrom);
      helper.setTo(to);
      helper.setSubject("Movie Invitation: " + reservation.getMovie().getTitle());

      String checkinUrl = baseUrl + "/checkin/" + reservation.getId();
      String qrUrl = baseUrl + reservation.getQrCodePath();
      String screenName = reservation.getScreen() == null ? "TBA" : reservation.getScreen().getName();
      String html = "<h2>You are invited to a movie!</h2>"
          + "<p><strong>Host:</strong> " + hostName + "</p>"
          + "<p><strong>Movie:</strong> " + reservation.getMovie().getTitle() + "</p>"
          + "<p><strong>Cinema:</strong> " + reservation.getCinema().getName() + "</p>"
          + "<p><strong>Screen:</strong> " + screenName + "</p>"
          + "<p><strong>Date:</strong> " + reservation.getDate() + "</p>"
          + "<p><strong>Time:</strong> " + reservation.getStartAt() + "</p>"
          + "<p><strong>Seats:</strong> " + reservation.getSeatLabels() + "</p>"
          + "<p><a href='" + checkinUrl + "'>Check-in link</a></p>"
          + "<p>Ticket QR:</p><img src='" + qrUrl + "' alt='QR code' style='max-width:220px;'/>";

      helper.setText(html, true);
      mailSender.send(msg);
    } catch (MailAuthenticationException e) {
      throw new IllegalStateException("Email is not configured. Set MAIL_USERNAME/MAIL_PASSWORD.");
    } catch (MessagingException e) {
      throw new RuntimeException("Failed to compose invitation email", e);
    }
  }
}
