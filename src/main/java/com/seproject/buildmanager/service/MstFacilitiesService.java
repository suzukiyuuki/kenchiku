package com.seproject.buildmanager.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import com.seproject.buildmanager.entity.MstFacilities;
import com.seproject.buildmanager.entity.MstFacilitiesDetail;
import com.seproject.buildmanager.entity.MstFacilitiesDetailTitle;
import com.seproject.buildmanager.entity.MstFacilitiesManagement;
import com.seproject.buildmanager.entity.MstFacilitiesTitle;
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

  private final MstFacilitiesTitleService mstFacilitiesTitleService;
  private final MstMatterService mstCaseService;

  private final CommonService commonService;

  // コンストラクタ
  public MstFacilitiesService(MstFacilitiesRepository mstFacilitiesRepository,
      MstFacilitiesManagementRepository mstFacilitiesManagementRepository,
      MstFacilitiesDetailRepository mstFacilitiesDetailRepository,
      MstFacilitiesTitleService mstFacilitiesTitleService, MstMatterService mstCaseService,
      CommonService commonService) {
    this.mstFacilitiesRepository = mstFacilitiesRepository;
    this.mstFacilitiesManagementRepository = mstFacilitiesManagementRepository;
    this.mstFacilitiesDetailRepository = mstFacilitiesDetailRepository;
    this.mstFacilitiesTitleService = mstFacilitiesTitleService;
    this.mstCaseService = mstCaseService;
    this.commonService = commonService;
  }



  /////////////////////// 取得系メソッド////////////////////////



  public List<MstFacilities> getFacilities() {
    return this.mstFacilitiesRepository.findAll();
  }

  public List<MstFacilitiesManagement> getFacilitiesManagements() {
    return this.mstFacilitiesManagementRepository.findAll();
  }

  public List<MstFacilitiesDetail> getFacilitiesDetails() {
    return this.mstFacilitiesDetailRepository.findAll();
  }

  // 指定した設備の設備詳細を一覧取得
  public List<MstFacilitiesDetail> getFacilitiesDetailsByFacilitiesId(Integer facilitiesId) {
    return this.mstFacilitiesDetailRepository.findByFacilitiesId(facilitiesId);
  }

  // 指定した設備管理の設備を一覧取得
  public List<MstFacilities> getFacilitiesByFacilitiesManagementId(Integer managementId) {
    return this.mstFacilitiesRepository.findByFacilitiesManagementId(managementId);
  }

  // 指定した案件の設備管理を取得
  public MstFacilitiesManagement getFacilitiesManagementByMatterId(Integer matterId) {
    return this.mstFacilitiesManagementRepository.findByMatterId(matterId).orElse(null);
  }

  // 登録されている設備だけ取得
  public MstFacilitiesManagementForm getFacilitiesManagementsRegistForm(Integer matterId) {
    MstFacilitiesManagement management =
        this.mstFacilitiesManagementRepository.findByMatterId(matterId).orElse(null);
    MstFacilitiesManagementForm managementForm = new MstFacilitiesManagementForm();
    managementForm.setCaseId(matterId);
    if (management != null) {
      managementForm.setId(management.getId());
    } else {
      managementForm.setId(null);
    }
    managementForm.setCaseName(this.mstCaseService.getCaseById(matterId).getMatterName());
    managementForm
        .setMstFacilitiesForms(this.getFacilitiesByManagementIdFromForm(management.getId()));
    return managementForm;
  }

  // 設備管理の情報をFormで一覧取得
  public List<MstFacilitiesManagementForm> getFacilitieManagementsFromForm() {
    List<MstMatterForm> matterForms = this.mstCaseService.viewCaseForm();
    List<MstFacilitiesManagementForm> facilitiesManagementForms =
        new ArrayList<MstFacilitiesManagementForm>();
    for (MstMatterForm caseForm : matterForms) {
      MstFacilitiesManagementForm managementForm =
          this.getFacilitieManagementByMatterIdFromForm(caseForm.getId());
      facilitiesManagementForms.add(managementForm);
    }
    return facilitiesManagementForms;
  }

  // mstFacilitiesManagementを案件idで検索し、Formで取得
  public MstFacilitiesManagementForm getFacilitieManagementByMatterIdFromForm(Integer matterId) {
    MstFacilitiesManagement management =
        this.mstFacilitiesManagementRepository.findByMatterId(matterId).orElse(null);
    MstFacilitiesManagementForm managementForm = new MstFacilitiesManagementForm();
    managementForm.setCaseId(matterId);
    if (management != null) {
      managementForm.setId(management.getId());
    } else {
      managementForm.setId(null);
    }
    managementForm.setCaseName(this.mstCaseService.getCaseById(matterId).getMatterName());
    if (management != null) {
      managementForm
          .setMstFacilitiesForms(this.getFacilitiesByManagementIdFromForm(management.getId()));
    } else {
      managementForm.setMstFacilitiesForms(new ArrayList<MstFacilitiesForm>());
    }

    return managementForm;
  }

  public List<MstFacilitiesForm> showFacilitiesForm() {
    List<MstFacilitiesTitle> mstFacilitieTitles =
        this.mstFacilitiesTitleService.getFacilitiesTitles();
    List<MstFacilitiesForm> facilitiesForms = new ArrayList<MstFacilitiesForm>();
    for (MstFacilitiesTitle facilitieTitle : mstFacilitieTitles) {
      facilitiesForms.add(this.convertFacilitieToForm(facilitieTitle, null));
    }
    return facilitiesForms;
  }

  // mstFacilitiesManagementのidから紐づいている設備項目をFormで一覧取得
  public List<MstFacilitiesForm> getFacilitiesByManagementIdFromForm(Integer managementId) {
    List<MstFacilitiesTitle> mstFacilitieTitles =
        this.mstFacilitiesTitleService.getFacilitiesTitles();
    List<MstFacilitiesForm> facilitiesForms = new ArrayList<MstFacilitiesForm>();
    for (MstFacilitiesTitle facilitieTitle : mstFacilitieTitles) {
      List<MstFacilities> mstFacilities = this.mstFacilitiesRepository
          .findByFacilitiesManagementIdAndTitleIdList(facilitieTitle.getId(), managementId);
      for (MstFacilities facilities : mstFacilities) {
        if (facilities != null) {
          facilitiesForms.add(this.convertFacilitieToForm(facilitieTitle, facilities));
        }
      }
    }
    return facilitiesForms;
  }

  public List<MstFacilitiesDetailForm> getFacilitiesDetailByFacilitiesIdFromForm(Integer titleId,
      Integer facilitiesId) {
    List<MstFacilitiesDetailTitle> detailTitles =
        this.mstFacilitiesTitleService.getFacilitiesDetailTitlesByFacilitiesTitleId(titleId);
    List<MstFacilitiesDetailForm> detailForms = new ArrayList<MstFacilitiesDetailForm>();
    for (MstFacilitiesDetailTitle detailTitle : detailTitles) {
      MstFacilitiesDetail facilitiesDetail = this.mstFacilitiesDetailRepository
          .findByFacilitiesIdAndTitleId(facilitiesId, detailTitle.getId()).orElse(null);
      detailForms.add(this.convertFacilitiesDetailToForm(detailTitle, facilitiesDetail));
    }
    return detailForms;
  }



  /////////////////////// 保存・更新メソッド//////////////////////////



  // セーブ
  public MstFacilitiesManagement save(MstFacilitiesManagementForm managementForm) {
    MstFacilitiesManagement management =
        this.convertMstFacilitiesManagementEntityToForm(managementForm);
    this.mstCaseService.updateFacilties(managementForm.getId());

    if (managementForm.getMstFacilitiesForms() != null) {
      management = this.mstFacilitiesManagementRepository.save(management);
      for (MstFacilitiesForm facilitieForm : managementForm.getMstFacilitiesForms()) {
        MstFacilities facilities =
            this.convertFacilitiesToEntity(facilitieForm, management.getId());
        facilities = this.mstFacilitiesRepository.save(facilities);
        // if (facilitieForm.getMstFacilitiesDetailForms() != null) {
        // for (MstFacilitiesDetailForm detailForm : facilitieForm.getMstFacilitiesDetailForms()) {
        // MstFacilitiesDetail detail =
        // this.convertFacilitiesToEntity(detailForm, facilities.getId());
        // this.mstFacilitiesDetailRepository.save(detail);
        // }
        // }
      }
    }
    return management;
  }

  public MstFacilities save(Integer matterId, Integer titleId) {
    MstFacilities facilitie = new MstFacilities();
    facilitie.setId(null);
    facilitie.setStatus(1);
    facilitie.setFacilitiesTitle(this.mstFacilitiesTitleService.getFacilitiesTitleById(titleId));
    MstFacilitiesManagement management = this.getFacilitiesManagementByMatterId(matterId);
    if (management != null) {
      facilitie.setFacilitiesManagementId(management.getId());
    } else {
      management = new MstFacilitiesManagement();
      management.setMatterId(matterId);
      management.setRegistrationDatetime(LocalDateTime.now());
      management.setUpdateDatetime(LocalDateTime.now());
      management.setUpdateUser(commonService.getLoginUserId());
      management = this.mstFacilitiesManagementRepository.save(management);
      facilitie.setFacilitiesManagementId(management.getId());
    }
    return this.mstFacilitiesRepository.save(facilitie);

  }



  /////////////////////// 変換系メソッド(Entity ⇔ Form)////////////////////////

  // 仮置き Entity ⇒ Form
  // titleとentityからFormに変換
  public MstFacilitiesForm convertFacilitieToForm(MstFacilitiesTitle title, MstFacilities entity) {
    MstFacilitiesForm form = new MstFacilitiesForm();
    Integer id = null;
    form.setTitle(title);
    if (entity != null) {
      id = entity.getId();
      form.setId(entity.getId());
      form.setStatus(entity.getStatus());
    } else {
      id = null;
      form.setId(null);
      form.setStatus(0);
    }

    // form.setMstFacilitiesDetailForms(
    // this.getFacilitiesDetailByFacilitiesIdFromForm(title.getId(), id));
    return form;
  }

  public MstFacilitiesDetailForm convertFacilitiesDetailToForm(MstFacilitiesDetailTitle title,
      MstFacilitiesDetail entity) {
    MstFacilitiesDetailForm detailForm = new MstFacilitiesDetailForm();
    detailForm.setTitle(title);
    if (entity != null) {
      detailForm.setId(entity.getId());
      detailForm.setValue(entity.getValue());
    } else {
      detailForm.setId(null);
      detailForm.setValue(null);
    }
    return detailForm;
  }

  // Form ⇒ Entity
  public MstFacilitiesManagement convertFacilitiesManagementToEntity(
      MstFacilitiesManagementForm form) {
    MstFacilitiesManagement entity = new MstFacilitiesManagement();
    entity.setId(form.getId());
    entity.setMatterId(form.getCaseId());
    entity.setRegistrationDatetime(form.getRegistrationDatetime());
    entity.setUpdateDatetime(form.getUpdateDatetime());
    entity.setUpdateUser(form.getUpdateUser());
    return entity;
  }

  public MstFacilities convertFacilitiesToEntity(MstFacilitiesForm form, Integer managementId) {
    MstFacilities entity = new MstFacilities();
    entity.setId(form.getId());
    entity.setFacilitiesManagementId(managementId);
    entity.setFacilitiesTitle(form.getTitle());
    entity.setStatus(form.getStatus());
    return entity;
  }

  public MstFacilitiesDetail convertFacilitiesToEntity(MstFacilitiesDetailForm form,
      Integer facilitiesId) {
    MstFacilitiesDetail entity = new MstFacilitiesDetail();
    entity.setId(form.getId());
    // entity.setFacilitiesId(facilitiesId);
    entity.setFacilitiesDetailTitle(form.getTitle());
    entity.setValue(form.getValue());
    entity.setStatus(1);
    return new MstFacilitiesDetail();
  }

  public MstFacilitiesManagement convertMstFacilitiesManagementEntityToForm(
      MstFacilitiesManagementForm form) {
    MstFacilitiesManagement entity = new MstFacilitiesManagement();
    entity.setId(form.getId());
    entity.setMatterId(form.getCaseId());
    entity.setRegistrationDatetime(form.getRegistrationDatetime());
    entity.setUpdateDatetime(form.getUpdateDatetime());
    entity.setUpdateUser(commonService.getLoginUserId());
    return entity;
  }
}
