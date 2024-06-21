package com.testing.core.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import rw.eccellenza.core.notification.domain.EmailResponse;
import rw.eccellenza.core.notification.domain.MailList;
import rw.eccellenza.core.notification.dtos.SmsRequestDto;
import rw.eccellenza.core.notification.dtos.SmsResponseDto;
import rw.eccellenza.core.notification.service.IEmailService;
import rw.eccellenza.core.notification.service.ISmsNotificationService;

/**
 * @Author -- Richard Mazimpaka
 */
@Slf4j
@RestController
@RequestMapping("/notification")
public class NotificationController {

  @Autowired private IEmailService emailService;
  @Autowired private ISmsNotificationService smsNotificationService;

  @PostMapping("/email/send")
  public ResponseEntity<Mono<EmailResponse>> sendEmail(
      @RequestBody(required = true) MailList mailList) {

    try {
      log.info("Initiating email sending process for: {}", mailList);

      Mono<EmailResponse> responseMono =
          emailService
              .sendEmails(mailList)
              .map(
                  response -> {
                    log.info("Email sent successfully with status: {}", response.getStatus());
                    return response;
                  })
              .doOnError(error -> log.error("Error sending email: {}", error.getMessage()))
              .doOnSuccess(success -> log.info("Email sending completed"));

      return ResponseEntity.ok(responseMono);

    } catch (Exception e) {
      log.error("Exception while initiating email send: ", e);
      EmailResponse errorResponse = new EmailResponse();
      errorResponse.setStatus(500);
      errorResponse.setMessage("Error initiating email send.");
      return ResponseEntity.status(500).body(Mono.just(errorResponse));
    }
  }


@PostMapping("sms/send")
  public ResponseEntity<Mono<SmsResponseDto>> sendSms(
          @RequestParam("phoneNumber") String  phoneNumber,@RequestParam("message") String message) {

    try {
      log.info("Initiating Sms sending process for: {}", phoneNumber);

      Mono<SmsResponseDto> responseMono =
              smsNotificationService
                      .sendSms(message,phoneNumber)
                      .map(
                              response -> {
                                log.info("SMS sent successfully with status: {}", response.getMessages().get(0).getStatus().getName());
                                return response;
                              })
                      .doOnError(error -> log.error("Error sending sms: {}", error.getMessage()))
                      .doOnSuccess(success -> log.info("SMS sending completed"));

      return ResponseEntity.ok(responseMono);

    } catch (Exception e) {
      log.error("Exception while initiating sms send: ", e);
      SmsResponseDto errorResponse = new SmsResponseDto();
      errorResponse.setStatus("BAD_REQUEST");
      errorResponse.setMessage("Error initiating sms send :" + e.getMessage());
      return ResponseEntity.status(500).body(Mono.just(errorResponse));
    }
  }
}
