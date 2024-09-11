package com.seproject.buildmanager.form;

import java.util.List;
import lombok.Data;

@Data
public class MstFacilitiesManagementForm {
  private Integer id;

  private Integer caseId;

  private String caseName;

  private List<MstFacilitiesForm> mstFacilitiesForms;
}
