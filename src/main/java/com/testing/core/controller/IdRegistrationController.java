package com.testing.core.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import rw.eccelanza.identityregistration.dto.IdNumberVerificationResponseDto;
import rw.eccelanza.identityregistration.service.IIdNumberVerificationService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/id")
public class IdRegistrationController {
  private final IIdNumberVerificationService idValidationService;

  @GetMapping("verify")
  public ResponseEntity<Mono<IdNumberVerificationResponseDto>> verifyId(
      @RequestParam("id") String id) {
    return new ResponseEntity<>(
        idValidationService.validateId(id), org.springframework.http.HttpStatus.OK);
  }
}
