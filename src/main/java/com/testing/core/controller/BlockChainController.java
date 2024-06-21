package com.testing.core.controller;

import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import rw.eccellenza.core.blockChainIntegration.dto.FetchDeedResponse;
import rw.eccellenza.core.blockChainIntegration.dto.Payload;
import rw.eccellenza.core.blockChainIntegration.service.IBlockChainIntegrationService;

/**
 * @Author -- Richard Mazimpaka
 */
@Slf4j
@RestController
@RequestMapping("/deeds")
public class BlockChainController {

  @Autowired private IBlockChainIntegrationService blockChainIntegrationService;

  @GetMapping("/fetch")
  public ResponseEntity<Mono<FetchDeedResponse>> feetchDeedOfGrant(
      @RequestParam Optional<Integer> deedId,
      @RequestParam Optional<String> name,
      @RequestParam Optional<String> dob,
      @RequestParam Optional<Integer> limit,
      Optional<Integer> page) {

    try {
      log.info("Initiating fetching  process");
      Integer id = null;
      String owner = null;
      String dateOfBirth = null;
      Integer pageNumber = null;
      Integer pageLimit = null;
      if (deedId.isPresent()) {
        id = deedId.get();
      }

      if (name.isPresent()) {
        owner = name.get();
      }

      if (dob.isPresent()) {
        dateOfBirth = dob.get();
      }

      if (limit.isPresent()) {
        pageLimit = limit.get();
      }

      if (page.isPresent()) {
        pageNumber = page.get();
      }

      Mono<FetchDeedResponse> responseMono =
          blockChainIntegrationService
              .feetchDeedOfGrant(id, owner, dateOfBirth, pageLimit, pageNumber)
              .map(
                  response -> {
                    log.info(response.getMessage());
                    return response;
                  })
              .doOnError(error -> log.error("Error fetching deeds: {}", error.getMessage()))
              .doOnSuccess(success -> log.info("fetching deeds completed"));

      return ResponseEntity.ok(responseMono);

    } catch (Exception e) {
      log.error("Exception while creating deeds: ", e);
      FetchDeedResponse errorResponse = new FetchDeedResponse();
      errorResponse.setMessage(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
      return ResponseEntity.status(500).body(Mono.just(errorResponse));
    }
  }

  @PostMapping("/create")
  public ResponseEntity<Mono<String>> createDeedOfGrant(
      @RequestBody(required = true) Payload payload) {

    try {
      log.info("Initiating creation of deed  process");

      Mono<String> responseMono =
          blockChainIntegrationService
              .CreateDeedOfGrant(payload)
              .map(
                  response -> {
                    log.info("error ");
                    return response;
                  })
              .doOnError(error -> log.error("Error creating deeds: {}", error.getMessage()))
              .doOnSuccess(success -> log.info("creating deeds completed"));

      return ResponseEntity.ok(responseMono);

    } catch (Exception e) {
      log.error("Exception creating deeds: ", e);
      return ResponseEntity.status(500)
          .body(Mono.just(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()));
    }
  }
}
