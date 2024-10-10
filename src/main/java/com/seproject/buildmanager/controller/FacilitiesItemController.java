package com.seproject.buildmanager.controller;

import java.util.ArrayList;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.seproject.buildmanager.common.Constants;
import com.seproject.buildmanager.entity.MstFacilitiesTitle;
import com.seproject.buildmanager.form.MstFacilitiesTitleEnrollmentsForm;
import com.seproject.buildmanager.service.MstFacilitiesTitleService;
import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("facilities-item")
public class FacilitiesItemController {

  private final MstFacilitiesTitleService mstFacilitiesTitleService;

  public FacilitiesItemController(MstFacilitiesTitleService mstFacilitiesTitleService) {
    this.mstFacilitiesTitleService = mstFacilitiesTitleService;
  }

  @GetMapping("")
  public String facilitiesList(HttpServletRequest request, Model model) {
    // CSRFトークンをモデルに追加
    CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
    model.addAttribute("csrfToken", csrfToken.getToken());
    model.addAttribute("csrfHeaderName", csrfToken.getHeaderName());

    model.addAttribute("mstFacilities", this.mstFacilitiesTitleService.getFacilitiesTitles());
    model.addAttribute("statusTrue", Constants.STATUS_TRUE);

    return "facilitiesList/list";
  }

  @GetMapping("register")
  public String registerGet(Model model) {
    model.addAttribute("mstFacilitiesTitle", this.mstFacilitiesTitleService.newFacilitiesTitles());
    model.addAttribute("mstFormat", this.mstFacilitiesTitleService.getFormat());
    model.addAttribute("addList", new ArrayList<String>());
    return "facilitiesList/regist";
  }

  @PostMapping("register")
  public String registerPost(@ModelAttribute("mstFacilitiesTitle") MstFacilitiesTitle mstFacilities,
      @RequestParam("detailName") String name, @RequestParam("detailFormat") String format,
      @RequestParam("detailpre") String pre, @RequestParam("detailpost") String post, Model model) {
    this.mstFacilitiesTitleService.save(mstFacilities, name, format, pre, post);
    return "redirect:/facilities-item";
  }

  @GetMapping("update")
  public String updateGet(@RequestParam("id") Integer id, Model model) {
    model.addAttribute("enrollments",
        this.mstFacilitiesTitleService.getFacilitiesEnrollmentsFormById(id));
    model.addAttribute("mstFormat", this.mstFacilitiesTitleService.getFormat());
    return "facilitiesList/update";
  }

  @PostMapping("update")
  public String updatePost(
      @ModelAttribute("enrollments") MstFacilitiesTitleEnrollmentsForm mstFacilities,
      @RequestParam("detailName") String name, @RequestParam("detailFormat") String format,
      @RequestParam("detailpre") String pre, @RequestParam("detailpost") String post, Model model) {
    this.mstFacilitiesTitleService.update(mstFacilities, name, format, pre, post);
    return "redirect:/facilities-item";
  }
}
