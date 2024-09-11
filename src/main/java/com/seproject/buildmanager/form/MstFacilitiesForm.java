package com.seproject.buildmanager.form;

import java.util.List;
import lombok.Data;

@Data
public class MstFacilitiesForm {
  private Integer id;

  private Integer status;

  private List<MstFacilitiesDetailForm> mstFacilitiesDetailForms;

  private String name;
}
