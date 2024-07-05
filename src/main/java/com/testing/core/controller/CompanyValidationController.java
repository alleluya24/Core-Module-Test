package com.testing.core.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rw.eccellenza.core.companyvalidation.dto.CompanyResponseData;
import rw.eccellenza.core.companyvalidation.exception.CompanyValidationException;
import rw.eccellenza.core.companyvalidation.service.ICompanyValidationService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/company")
public class CompanyValidationController {
  private final ICompanyValidationService companyValidationService;

  @GetMapping("verify")
  public ResponseEntity<CompanyResponseData> verifyId(
      @RequestParam("id_number") String id) {
    try {
      CompanyResponseData responseDataMono = companyValidationService.validateCompany(id).block();
      return new ResponseEntity<>(
              responseDataMono, org.springframework.http.HttpStatus.OK);
    }
    catch (CompanyValidationException e) {
        log.error("Error validating company: ", e);
        return ResponseEntity.status(404).build();
    }
  }
}
