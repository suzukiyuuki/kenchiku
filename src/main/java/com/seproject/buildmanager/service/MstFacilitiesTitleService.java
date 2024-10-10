package com.seproject.buildmanager.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import com.seproject.buildmanager.entity.MstFacilitiesDetailTitle;
import com.seproject.buildmanager.entity.MstFacilitiesSubcategoryTitle;
import com.seproject.buildmanager.entity.MstFacilitiesTitle;
import com.seproject.buildmanager.entity.MstFacilitiesTitleEnrollments;
import com.seproject.buildmanager.entity.MstFormat;
import com.seproject.buildmanager.form.MstFacilitiesTitleEnrollmentsForm;
import com.seproject.buildmanager.form.MstFacilitiesTitleEnrollmentsSetForm;
import com.seproject.buildmanager.repository.MstFacilitiesDetailTitleRepositoy;
import com.seproject.buildmanager.repository.MstFacilitiesSubcategoryTitleRepository;
import com.seproject.buildmanager.repository.MstFacilitiesTitleEnrollmentsRepository;
import com.seproject.buildmanager.repository.MstFacilitiesTitleRepositoy;
import com.seproject.buildmanager.repository.MstFormatRepository;

@Service
public class MstFacilitiesTitleService {

  private final MstFacilitiesDetailTitleRepositoy mstFacilitiesDetailTitleRepositoy;
  private final MstFacilitiesSubcategoryTitleRepository mstFacilitiesSubcategoryTitleRepository;
  private final MstFacilitiesTitleRepositoy mstFacilitiesTitleRepositoy;
  private final MstFacilitiesTitleEnrollmentsRepository mstFacilitiesTitleEnrollmentsRepository;
  private final MstFormatRepository mstFormatRepository;
  private final CommonService commonService;


  // コンストラクタ
  public MstFacilitiesTitleService(
      MstFacilitiesDetailTitleRepositoy mstFacilitiesDetailTitleRepositoy,
      MstFacilitiesSubcategoryTitleRepository mstFacilitiesSubcategoryTitleRepository,
      MstFacilitiesTitleRepositoy mstFacilitiesTitleRepositoy,
      MstFormatRepository mstFormatRepository,
      MstFacilitiesTitleEnrollmentsRepository mstFacilitiesTitleEnrollmentsRepository,
      CommonService commonService) {
    this.mstFacilitiesDetailTitleRepositoy = mstFacilitiesDetailTitleRepositoy;
    this.mstFacilitiesTitleRepositoy = mstFacilitiesTitleRepositoy;
    this.mstFacilitiesSubcategoryTitleRepository = mstFacilitiesSubcategoryTitleRepository;
    this.mstFormatRepository = mstFormatRepository;
    this.mstFacilitiesTitleEnrollmentsRepository = mstFacilitiesTitleEnrollmentsRepository;
    this.commonService = commonService;
  }

  public MstFacilitiesTitle newFacilitiesTitles() {
    return new MstFacilitiesTitle();
  }

  public List<MstFormat> getFormat() {
    return mstFormatRepository.findAll();
  }

  public MstFacilitiesDetailTitle newFacilitiesDetailTitle() {
    return new MstFacilitiesDetailTitle();
  }

  public MstFacilitiesSubcategoryTitle newFacilitiesSubcategoryTitle() {
    return new MstFacilitiesSubcategoryTitle();
  }

  public MstFacilitiesTitle getFacilitiesTitleById(Integer id) {
    return this.mstFacilitiesTitleRepositoy.findById(id).orElse(null);
  }

  public void update(MstFacilitiesTitleEnrollmentsForm enrollments, String names, String formats,
      String pres, String posts) {
    for (MstFacilitiesTitleEnrollmentsSetForm setForm : enrollments.getFacilitiesTitleSet()) {
      this.subcategoryUpdeta(setForm.getSubcategoryTitle());
      this.detailUpdate(setForm.getDetailTitle());
    }
    this.save(enrollments.getFacilitiesTitle(), names, formats, pres, posts);
  }


  public void save(MstFacilitiesTitle title, String names, String formats, String pres,
      String posts) {

    String[] name = names.split(",");
    String[] format = formats.split(",");
    String[] pre = pres.split(",");
    String[] post = posts.split(",");

    List<MstFacilitiesSubcategoryTitle> subcategorys =
        new ArrayList<MstFacilitiesSubcategoryTitle>();
    for (int i = 0; i < name.length; i++) {
      subcategorys.add(subcategorySaveUnit(name[i], format[i], pre[i], post[i]));
    }

    List<MstFacilitiesDetailTitle> details = detailSave(names, formats, pres, posts);
    title.setRegistrationDatetime(LocalDateTime.now());
    title.setUpdateDatetime(LocalDateTime.now());
    title.setUpdateUser(commonService.getLoginUserId());
    title.setStatus(1);
    title = this.mstFacilitiesTitleRepositoy.save(title);

    MstFacilitiesTitleEnrollments enrollments = new MstFacilitiesTitleEnrollments();
    enrollments.setFacilitiesTitle(title.getId());
    for (MstFacilitiesDetailTitle detail : details) {
      enrollments.setFacilitiesDetailTitle(detail.getId());
      this.mstFacilitiesTitleEnrollmentsRepository.save(enrollments);
    }
  }

  public MstFacilitiesSubcategoryTitle subcategorySaveUnit(String name, String format, String pre,
      String post) {
    MstFacilitiesSubcategoryTitle d = new MstFacilitiesSubcategoryTitle();
    d.setName(name);
    d.setPreposition(pre);
    d.setPostposition(post);
    if (!format.isEmpty() && format != null) {
      MstFormat formatEntity =
          this.mstFormatRepository.findById(Integer.parseInt(format)).orElse(null);
      if (formatEntity != null) {
        d.setFormat_id(formatEntity);
      } else {
        d.setFormat_id(null);
      }
    } else {
      d.setFormat_id(null);
    }

    d.setId(null);
    d.setUpdateUser(commonService.getLoginUserId());
    d = this.mstFacilitiesSubcategoryTitleRepository.save(d);
    return d;
  }

  public List<MstFacilitiesSubcategoryTitle> subcategorySave(String names, String formats,
      String pres, String posts) {
    List<MstFacilitiesSubcategoryTitle> mstTitles = new ArrayList<MstFacilitiesSubcategoryTitle>();
    String[] name = names.split(",");
    String[] format = formats.split(",");
    String[] pre = pres.split(",");
    String[] post = posts.split(",");
    for (int i = 0; i < name.length; i++) {
      MstFacilitiesSubcategoryTitle d = new MstFacilitiesSubcategoryTitle();
      d.setName(name[i]);
      d.setPreposition(pre[i]);
      d.setPostposition(post[i]);
      if (!format[i].isEmpty() && format[i] != null) {
        MstFormat formatEntity =
            this.mstFormatRepository.findById(Integer.parseInt(format[i])).orElse(null);
        if (formatEntity != null) {
          d.setFormat_id(formatEntity);
        } else {
          d.setFormat_id(null);
        }
      } else {
        d.setFormat_id(null);
      }

      d.setId(null);
      d.setUpdateUser(commonService.getLoginUserId());
      this.mstFacilitiesSubcategoryTitleRepository.save(d);
      mstTitles.add(d);
    }
    return mstTitles;
  }

  public void subcategoryUpdeta(MstFacilitiesSubcategoryTitle subcategory) {
    if (subcategory != null) {
      subcategory.setUpdateDatetime(LocalDateTime.now());
      subcategory.setUpdateUser(commonService.getLoginUserId());
      MstFacilitiesSubcategoryTitle result =
          this.mstFacilitiesSubcategoryTitleRepository.save(subcategory);
    }

  }

  public MstFacilitiesDetailTitle detailSaveUnit(String name, String format, String pre,
      String post) {
    MstFacilitiesDetailTitle d = new MstFacilitiesDetailTitle();
    d.setName(name);
    d.setPreposition(pre);
    d.setPostposition(post);
    if (!format.isEmpty() && format != null) {
      MstFormat formatEntity =
          this.mstFormatRepository.findById(Integer.parseInt(format)).orElse(null);
      if (formatEntity != null) {
        d.setFormat_id(formatEntity);
      } else {
        d.setFormat_id(null);
      }
    } else {
      d.setFormat_id(null);
    }

    d.setId(null);
    d.setUpdateUser(commonService.getLoginUserId());
    d = this.mstFacilitiesDetailTitleRepositoy.save(d);
    return d;
  }

  public List<MstFacilitiesDetailTitle> detailSave(String names, String formats, String pres,
      String posts) {
    List<MstFacilitiesDetailTitle> mstTitles = new ArrayList<MstFacilitiesDetailTitle>();
    String[] name = names.split(",");
    String[] format = formats.split(",");
    String[] pre = pres.split(",");
    String[] post = posts.split(",");
    for (int i = 0; i < name.length; i++) {
      MstFacilitiesDetailTitle d = new MstFacilitiesDetailTitle();
      d.setName(name[i]);
      d.setPreposition(pre[i]);
      d.setPostposition(post[i]);
      if (!format[i].isEmpty() && format[i] != null) {
        MstFormat formatEntity =
            this.mstFormatRepository.findById(Integer.parseInt(format[i])).orElse(null);
        if (formatEntity != null) {
          d.setFormat_id(formatEntity);
        } else {
          d.setFormat_id(null);
        }
      } else {
        d.setFormat_id(null);
      }

      d.setId(null);
      d.setUpdateUser(commonService.getLoginUserId());
      this.mstFacilitiesDetailTitleRepositoy.save(d);
      mstTitles.add(d);
    }
    return mstTitles;
  }

  public void detailUpdate(List<MstFacilitiesDetailTitle> details) {
    if (details != null) {
      for (MstFacilitiesDetailTitle detail : details) {
        detail.setUpdateDatetime(LocalDateTime.now());
        detail.setUpdateUser(commonService.getLoginUserId());
        this.mstFacilitiesDetailTitleRepositoy.save(detail);
      }
    }
  }
  /////////////////////// 取得系メソッド////////////////////////

  public List<MstFacilitiesTitle> getFacilitiesTitles() {
    return this.mstFacilitiesTitleRepositoy.findAll();
  }

  public List<MstFacilitiesSubcategoryTitle> getFacilitiesSubcategoryTitles() {
    return this.mstFacilitiesSubcategoryTitleRepository.findAll();
  }

  public List<MstFacilitiesDetailTitle> getFacilitiesDetailTitles() {
    return this.mstFacilitiesDetailTitleRepositoy.findAll();
  }

  public List<MstFacilitiesDetailTitle> getFacilitiesDetailTitlesByFacilitiesTitleId(
      Integer titleId) {
    return this.mstFacilitiesDetailTitleRepositoy.findByFacilitiesTitleId(titleId);
  }

  public List<MstFacilitiesSubcategoryTitle> getFacilitiesSubcategoryTitlesByFacilitiesTitleId(
      Integer titleId) {
    return this.mstFacilitiesSubcategoryTitleRepository.findByFacilitiesTitleId(titleId);
  }

  public List<MstFacilitiesTitleEnrollmentsForm> getFacilitiesEnrollmentsForm() {
    List<MstFacilitiesTitleEnrollmentsForm> form =
        new ArrayList<MstFacilitiesTitleEnrollmentsForm>();
    List<MstFacilitiesTitle> facilitiesTitle = this.getFacilitiesTitles();
    for (MstFacilitiesTitle f : facilitiesTitle) {
      MstFacilitiesTitleEnrollmentsForm enrollments = new MstFacilitiesTitleEnrollmentsForm();
      List<MstFacilitiesSubcategoryTitle> subcategoryTitle =
          this.getFacilitiesSubcategoryTitlesByFacilitiesTitleId(f.getId());
      List<MstFacilitiesTitleEnrollmentsSetForm> setForms =
          new ArrayList<MstFacilitiesTitleEnrollmentsSetForm>();
      for (MstFacilitiesSubcategoryTitle sub : subcategoryTitle) {
        MstFacilitiesTitleEnrollmentsSetForm setForm = new MstFacilitiesTitleEnrollmentsSetForm();
        setForm.setSubcategoryTitle(sub);

        List<MstFacilitiesDetailTitle> detailTitle =
            this.getFacilitiesDetailTitlesByFacilitiesTitleId(sub.getId());
        setForm.setDetailTitle(detailTitle);

        setForms.add(setForm);
      }

      enrollments.setFacilitiesTitleSet(setForms);
      enrollments.setFacilitiesTitle(f);
      form.add(enrollments);
    }
    return form;
  }

  public MstFacilitiesTitleEnrollmentsForm getFacilitiesEnrollmentsFormById(Integer facilitiesId) {
    MstFacilitiesTitleEnrollmentsForm form = new MstFacilitiesTitleEnrollmentsForm();
    MstFacilitiesTitle facilitiesTitle = this.getFacilitiesTitleById(facilitiesId);

    List<MstFacilitiesSubcategoryTitle> subcategoryTitle =
        this.getFacilitiesSubcategoryTitlesByFacilitiesTitleId(facilitiesId);
    List<MstFacilitiesTitleEnrollmentsSetForm> setForms =
        new ArrayList<MstFacilitiesTitleEnrollmentsSetForm>();
    for (MstFacilitiesSubcategoryTitle sub : subcategoryTitle) {
      MstFacilitiesTitleEnrollmentsSetForm setForm = new MstFacilitiesTitleEnrollmentsSetForm();
      setForm.setSubcategoryTitle(sub);

      List<MstFacilitiesDetailTitle> detailTitle =
          this.getFacilitiesDetailTitlesByFacilitiesTitleId(sub.getId());
      setForm.setDetailTitle(detailTitle);

      setForms.add(setForm);
    }

    form.setFacilitiesTitleSet(setForms);
    form.setFacilitiesTitle(facilitiesTitle);
    return form;
  }
}
