package com.seproject.buildmanager.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import com.seproject.buildmanager.common.Constants;
import com.seproject.buildmanager.entity.MstFacilities;
import com.seproject.buildmanager.entity.MstFacilitiesDetail;
import com.seproject.buildmanager.entity.MstFacilitiesManagement;
import com.seproject.buildmanager.form.MstFacilitiesDetailForm;
import com.seproject.buildmanager.form.MstFacilitiesForm;
import com.seproject.buildmanager.form.MstFacilitiesManagementForm;
import com.seproject.buildmanager.form.MstMatterForm;
import com.seproject.buildmanager.repository.MstFacilitiesDetailRepository;
import com.seproject.buildmanager.repository.MstFacilitiesManagementRepository;
import com.seproject.buildmanager.repository.MstFacilitiesRepository;

@Service
public class MstFacilitiesService {
  private final MstFacilitiesRepository mstFacilitiesRepository;
  private final MstFacilitiesManagementRepository mstFacilitiesManagementRepository;
  private final MstFacilitiesDetailRepository mstFacilitiesDetailRepository;
  private final MstMatterService mstCaseService;

  public MstFacilitiesService(MstFacilitiesRepository mstFacilitiesRepository,
      MstFacilitiesManagementRepository mstFacilitiesManagementRepository,
      MstFacilitiesDetailRepository mstFacilitiesDetailRepository,
      MstMatterService mstCaseService) {
    this.mstFacilitiesRepository = mstFacilitiesRepository;
    this.mstFacilitiesManagementRepository = mstFacilitiesManagementRepository;
    this.mstFacilitiesDetailRepository = mstFacilitiesDetailRepository;
    this.mstCaseService = mstCaseService;
  }

  public List<MstFacilities> getFacilities() {
    return this.mstFacilitiesRepository.findAll();
  }

  public List<MstFacilitiesManagement> getFacilitiesManagements() {
    return this.mstFacilitiesManagementRepository.findAll();
  }

  public List<MstFacilitiesDetail> getFacilitiesDetails() {
    return this.mstFacilitiesDetailRepository.findAll();
  }

  public List<MstFacilitiesManagementForm> viewFacilitiesManagementForm() {
    List<MstMatterForm> caseForms = this.mstCaseService.viewCaseForm();
    List<MstFacilitiesManagementForm> facilitiesManagementForms =
        new ArrayList<MstFacilitiesManagementForm>();
    for (MstMatterForm caseForm : caseForms) {
      MstFacilitiesManagementForm managementForm =
          this.getFaciliriesManagementByCaseId(caseForm.getId());
      facilitiesManagementForms.add(managementForm);
    }
    return facilitiesManagementForms;
  }

  public MstFacilitiesManagementForm getFaciliriesManagementByCaseId(Integer caseId) {
    MstFacilitiesManagement management =
        this.mstFacilitiesManagementRepository.findByCaseId(caseId).orElse(null);
    MstFacilitiesManagementForm managementForm = new MstFacilitiesManagementForm();
    managementForm.setCaseId(caseId);
    if (management != null) {
      managementForm.setId(management.getId());
    } else {
      managementForm.setId(null);
    }
    managementForm.setCaseName(this.mstCaseService.getCaseById(caseId).getMatterName());
    managementForm.setMstFacilitiesForms(getFaciliriesByCaseId(management));
    return managementForm;
  }

  public List<MstFacilitiesForm> getFaciliriesByCaseId(MstFacilitiesManagement management) {
    List<MstFacilities> mstFacilities = this.mstFacilitiesRepository.findAll();
    List<MstFacilitiesForm> facilitiesForms = new ArrayList<MstFacilitiesForm>();
    for (MstFacilities facilities : mstFacilities) {
      facilitiesForms.add(this.convertMstFacilitiesFormToEntity(facilities));
    }
    if (management != null) {
      String[] values = management.getValue().split(",");
      int i = 0;
      List<MstFacilities> checkFacilities =
          this.mstFacilitiesRepository.findFacilitiesByEquipmentBit(management.getId());
      for (MstFacilitiesForm facilitiesForm : facilitiesForms) {
        for (MstFacilities facilities : checkFacilities) {
          if (facilitiesForm.getId() == facilities.getId()) {
            facilitiesForm.setStatus(1);
            for (MstFacilitiesDetailForm detailForm : facilitiesForm
                .getMstFacilitiesDetailForms()) {
              if (i < values.length) {
                detailForm.setValue(values[i]);
                i++;
              }
            }
          }
        }
      }
    }
    return facilitiesForms;
  }

  public MstFacilitiesManagement save(MstFacilitiesManagementForm managementForm) {
    MstFacilitiesManagement management =
        this.convertMstFacilitiesManagementEntityToForm(managementForm);
    this.mstCaseService.updateFacilties(managementForm.getId());
    return this.mstFacilitiesManagementRepository.save(management);
  }

  public MstFacilitiesForm convertMstFacilitiesFormToEntity(MstFacilities mstFacilities) {
    MstFacilitiesForm facilitiesForm = new MstFacilitiesForm();
    facilitiesForm.setId(mstFacilities.getId());
    facilitiesForm.setName(mstFacilities.getName());
    facilitiesForm.setStatus(Constants.STATUS_FALSE);
    List<MstFacilitiesDetail> details = this.mstFacilitiesDetailRepository
        .findFacilitiesDetailByEquipmentDetailBit(mstFacilities.getId());
    List<MstFacilitiesDetailForm> detailForms = new ArrayList<MstFacilitiesDetailForm>();
    for (MstFacilitiesDetail detail : details) {
      detailForms.add(this.convertMstFacilitiesDetailFormToEntity(detail));
    }
    facilitiesForm.setMstFacilitiesDetailForms(detailForms);
    return facilitiesForm;
  }

  public MstFacilitiesDetailForm convertMstFacilitiesDetailFormToEntity(
      MstFacilitiesDetail mstFacilitiesDetail) {
    MstFacilitiesDetailForm facilitiesDetailForm = new MstFacilitiesDetailForm();
    facilitiesDetailForm.setId(mstFacilitiesDetail.getId());
    facilitiesDetailForm.setName(mstFacilitiesDetail.getName());
    facilitiesDetailForm.setPostposition(mstFacilitiesDetail.getPostposition());
    facilitiesDetailForm.setPreposition(mstFacilitiesDetail.getPreposition());
    return facilitiesDetailForm;
  }

  public MstFacilitiesManagement convertMstFacilitiesManagementEntityToForm(
      MstFacilitiesManagementForm form) {
    MstFacilitiesManagement entity = new MstFacilitiesManagement();
    entity.setId(form.getId());
    entity.setCaseId(form.getCaseId());
    int bitInt = 0;
    String inputValue = "";
    for (MstFacilitiesForm facilites : form.getMstFacilitiesForms()) {
      try {
        if (facilites.getStatus() != null) {

          bitInt += this.mstFacilitiesRepository.findById(facilites.getId()).orElse(null)
              .getEquipmentBit();

          for (MstFacilitiesDetailForm detail : facilites.getMstFacilitiesDetailForms()) {
            if (detail.getValue() != null && !detail.getValue().isEmpty()) {
              inputValue += detail.getValue();
              inputValue += ",";
            } else {
              inputValue += ",";
            }
          }
        }
      } catch (NullPointerException e) {
        e.printStackTrace();
      }

    }
    entity.setValue(inputValue);
    entity.setEquipmentBit(bitInt);
    return entity;
  }
}
