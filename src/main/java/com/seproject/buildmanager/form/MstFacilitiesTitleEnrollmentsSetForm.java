package com.seproject.buildmanager.form;

import java.util.List;
import com.seproject.buildmanager.entity.MstFacilitiesDetailTitle;
import com.seproject.buildmanager.entity.MstFacilitiesSubcategoryTitle;
import lombok.Data;

@Data
public class MstFacilitiesTitleEnrollmentsSetForm {
  private MstFacilitiesSubcategoryTitle subcategoryTitle;
  private List<MstFacilitiesDetailTitle> detailTitle;
}
