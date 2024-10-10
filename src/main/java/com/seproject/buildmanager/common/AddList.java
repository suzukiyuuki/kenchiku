package com.seproject.buildmanager.common;

import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class AddList {

  public String addListString(List<String> list, String addNode) {
    list.add(addNode);
    return addNode;
  }

  // public MstFacilitiesSubcategoryTitle facilitiesAdd(List<MstFacilitiesSubcategoryTitle> list,
  // String name, String checkBox, String pre, String post) {
  //
  // }
}
