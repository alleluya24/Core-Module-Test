package com.testing.core.controller;

import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;
import rw.eccellenza.core.notification.domain.EmailRequest;
import rw.eccellenza.core.notification.domain.EmailResponse;
import rw.eccellenza.core.notification.domain.EmailUtil;
import rw.eccellenza.core.notification.domain.MailList;
import rw.eccellenza.core.notification.service.IEmailService;

/**
 * @Author -- Richard Mazimpaka
 */
@Slf4j
@RestController
@RequestMapping("/notification")
public class NotificationController {

  @Autowired private IEmailService emailService;

  @PostMapping(
      path = "/email/send",
      consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
  public ResponseEntity<Mono<EmailResponse>> sendEmail(
      @RequestPart(name = "emailRequest", required = true) EmailRequest emailRequest,
      @RequestPart("attachment") MultipartFile attachment) {

    try {

      log.info("Received file with content type: {}", attachment.getContentType());

      MailList mailList = new MailList();
      EmailUtil emailUtil = new EmailUtil();
      List<EmailUtil> emailUtils = new ArrayList<>();
      emailUtil.setSubject(emailRequest.getSubject());
      emailUtil.setBodyText(emailRequest.getBodyText());
      emailUtil.setReceipient(emailRequest.getReceipient());

      emailUtil.setContentType(attachment.getContentType());
      emailUtil.setMyBytes(attachment.getBytes());
      emailUtil.setFilename(attachment.getName());

      emailUtils.add(emailUtil);
      mailList.setEmails(emailUtils);

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
}
