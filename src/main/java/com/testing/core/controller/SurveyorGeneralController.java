package com.testing.core.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rw.eccellenza.core.surverygeneral.dto.DistrictResponse;
import rw.eccellenza.core.surverygeneral.dto.PropertyResponse;
import rw.eccellenza.core.surverygeneral.dto.TownShipResponse;
import rw.eccellenza.core.surverygeneral.service.ISurveyGeneralIntegrationService;

/**
 * @author: RAMBA
 */
@Slf4j
@RestController
@RequestMapping("/surveyor-general")
public class SurveyorGeneralController {

  @Autowired private ISurveyGeneralIntegrationService surveyGeneralIntegrationService;

  @GetMapping("/get-districts")
  public ResponseEntity<DistrictResponse> getDistrict() {
    try {
      return new ResponseEntity<>(
          surveyGeneralIntegrationService.getDistrict().block(), HttpStatus.OK);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  @GetMapping("/get-townships")
  public ResponseEntity<TownShipResponse> getTownShip() {
    try {
      return new ResponseEntity<>(
          surveyGeneralIntegrationService.getTownShip().block(), HttpStatus.OK);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  @GetMapping("/get-township-by-district/{districtId}")
  public ResponseEntity<TownShipResponse> getTownByDistrict(
      @PathVariable("districtId") String districtId) {
    try {
      return new ResponseEntity<>(
          surveyGeneralIntegrationService.searchtownship(districtId).block(), HttpStatus.OK);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  @GetMapping("/seach-property/{districtId}/{townshipId}/{searchKey}")
  public ResponseEntity<PropertyResponse> searchProperty(
      @PathVariable("districtId") String districtId,
      @PathVariable("townshipId") String townshipId,
      @PathVariable("searchKey") String searchKey) {
    try {
      return new ResponseEntity<>(
          surveyGeneralIntegrationService.searchProperty(districtId, townshipId, searchKey).block(),
          HttpStatus.OK);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }
}
