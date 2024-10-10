package com.seproject.buildmanager.form;

import java.util.List;
import com.seproject.buildmanager.entity.MstFacilitiesSubcategoryTitle;
import lombok.Data;

@Data
public class MstFacilitiesSubcategoryForm {
  private Integer id;

  private String value;

  private MstFacilitiesSubcategoryTitle title;

  private List<MstFacilitiesDetailForm> mstFacilitiesDetailForms;
}
