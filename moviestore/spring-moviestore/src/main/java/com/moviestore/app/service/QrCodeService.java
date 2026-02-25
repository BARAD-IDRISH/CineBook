package com.moviestore.app.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.moviestore.app.model.Reservation;
import org.springframework.stereotype.Service;

import java.nio.file.Path;

@Service
public class QrCodeService {
  private static final int QR_SIZE = 1080;
  private final FileStorageService fileStorageService;

  public QrCodeService(FileStorageService fileStorageService) {
    this.fileStorageService = fileStorageService;
  }

  public String createReservationQr(Reservation reservation) {
    try {
      String text = buildReservationQrText(reservation);
      QRCodeWriter writer = new QRCodeWriter();
      BitMatrix matrix = writer.encode(text, BarcodeFormat.QR_CODE, QR_SIZE, QR_SIZE);
      String filename = "reservation-" + reservation.getId() + ".png";
      Path output = fileStorageService.buildPath("qrcodes", filename);
      MatrixToImageWriter.writeToPath(matrix, "PNG", output);
      return "/uploads/qrcodes/" + filename;
    } catch (Exception e) {
      throw new RuntimeException("Unable to generate QR code", e);
    }
  }

  private String buildReservationQrText(Reservation reservation) {
    String screenName = reservation.getScreen() == null ? "TBA" : reservation.getScreen().getName();
    return "MovieStore Reservation\n"
        + "Reservation ID: " + reservation.getId() + "\n"
        + "Movie: " + reservation.getMovie().getTitle() + "\n"
        + "Cinema: " + reservation.getCinema().getName() + "\n"
        + "Screen: " + screenName + "\n"
        + "Date: " + reservation.getDate() + "\n"
        + "Time: " + reservation.getStartAt() + "\n"
        + "Seats: " + reservation.getSeatLabels() + "\n"
        + "Ticket Price: " + reservation.getTicketPrice() + "\n"
        + "Total: " + reservation.getTotal() + "\n"
        + "Booked By: " + reservation.getUsername() + "\n"
        + "Phone: " + reservation.getPhone();
  }
}


