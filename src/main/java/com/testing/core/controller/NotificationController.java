package com.testing.core.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.testing.core.dto.EmailDto;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;
import rw.eccellenza.core.notification.domain.AttachmentDto;
import rw.eccellenza.core.notification.domain.EmailRequest;
import rw.eccellenza.core.notification.domain.EmailRequestWithMultipleAttachmentRequest;
import rw.eccellenza.core.notification.domain.EmailResponse;
import rw.eccellenza.core.notification.domain.EmailUtil;
import rw.eccellenza.core.notification.domain.MailList;
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
  @Autowired private ObjectMapper objectMapper;
  @Autowired private ISmsNotificationService smsNotificationService;

  @PostMapping(path = "/email/send")
  public ResponseEntity<Mono<EmailResponse>> sendEmail(
      @RequestParam(name = "emailRequest", required = true) String emailRequestStr,
      @RequestParam("attachment") MultipartFile attachment) {

    try {

      log.info("Received file with content type: {}", attachment.getContentType());

      // Deserialize the JSON string to EmailRequest object
      EmailRequest emailRequest = objectMapper.readValue(emailRequestStr, EmailRequest.class);

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

  @PostMapping("sms/send")
  public ResponseEntity<Mono<SmsResponseDto>> sendSms(
      @RequestParam("phoneNumber") String phoneNumber, @RequestParam("message") String message) {

    try {
      log.info("Initiating Sms sending process for: {}", phoneNumber);

      Mono<SmsResponseDto> responseMono =
          smsNotificationService
              .sendSms(message, phoneNumber)
              .map(
                  response -> {
                    log.info(
                        "SMS sent successfully with status: {}",
                        response.getMessages().get(0).getStatus().getName());
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

  @PostMapping(path = "/email/send-multiple-attachments")
  public ResponseEntity<Mono<EmailResponse>> sendEmailWithMultipleAttachments(
      @RequestParam(name = "emailRequest", required = true) String emailRequestStr,
      @RequestParam("attachments") MultipartFile[] attachments) {

    try {

      EmailDto emailRequest = objectMapper.readValue(emailRequestStr, EmailDto.class);

      EmailRequestWithMultipleAttachmentRequest requestWithMultipleAttachmentRequest =
          new EmailRequestWithMultipleAttachmentRequest();

      requestWithMultipleAttachmentRequest.setMessageBodyText(emailRequest.getMessageBodyText());
      requestWithMultipleAttachmentRequest.setRecipientEmailAddress(
          emailRequest.getRecipientEmailAddress());
      requestWithMultipleAttachmentRequest.setSubject(emailRequest.getSubject());

      List<AttachmentDto> attachmentDtos = new ArrayList<>();

      for (MultipartFile f : attachments) {

        AttachmentDto attachmentDto = new AttachmentDto();
        attachmentDto.setContentType(f.getContentType());
        attachmentDto.setFilename(f.getOriginalFilename());
        attachmentDto.setTheAttachmentBytes(f.getBytes());

        attachmentDtos.add(attachmentDto);
      }
      requestWithMultipleAttachmentRequest.setAttachments(attachmentDtos);

      Mono<EmailResponse> responseMono =
          emailService
              .sendEmailWithMultipleAttachment(requestWithMultipleAttachmentRequest)
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
