package com.seproject.buildmanager.controller;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.seproject.buildmanager.form.MstFacilitiesForm;
import com.seproject.buildmanager.service.MstFacilitiesService;

@RestController
@RequestMapping("api/facilities")
public class FacilitiesApiController {

  @Autowired
  private MstFacilitiesService mstFacilitiesService;

  @GetMapping("forms")
  public ResponseEntity<List<MstFacilitiesForm>> getFacilities() {
    List<MstFacilitiesForm> forms = this.mstFacilitiesService.showFacilitiesForm();

    return new ResponseEntity<>(forms, HttpStatus.OK);
  }

  @PostMapping("register")
  public ResponseEntity<String> getFacilitiesRegister(@RequestBody Map<String, Integer> request) {
    Integer titleId = request.get("titleId");
    Integer matterId = request.get("matterId");
    this.mstFacilitiesService.save(matterId, titleId);
    return new ResponseEntity<>("ok", HttpStatus.OK);
  }
}
