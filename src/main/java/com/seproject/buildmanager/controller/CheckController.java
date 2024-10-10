package com.seproject.buildmanager.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import com.seproject.buildmanager.common.Constants;
import com.seproject.buildmanager.form.MstCheckForm;
import com.seproject.buildmanager.service.MstCheckService;
import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("check")
public class CheckController {

  private static final Logger logger = LoggerFactory.getLogger(CheckController.class);

  @Autowired
  private MstCheckService mstCheckService;

  // コンストラクタインジェクションを使用してMstCheckGroupeServiceの依存性を注入
  public CheckController(MstCheckService mstCheckService) {
    this.mstCheckService = mstCheckService;
  }

  @GetMapping("")
  public String getAllChecks(Model model, HttpServletRequest request) {

    logger.info("--- CheckController.getAllChecks START ---");

    CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
    model.addAttribute("csrfToken", csrfToken.getToken());
    model.addAttribute("csrfHeaderName", csrfToken.getHeaderName());

    model.addAttribute("mstCheckForm", mstCheckService.mstCheckForm());
    model.addAttribute("checks", mstCheckService.getAllChecks());
    model.addAttribute("statusTrue", Constants.STATUS_TRUE);

    logger.info("--- CheckController.getAllChecks END ---");

    return "check/check";
  }

  @GetMapping("search")
  public String search(HttpServletRequest request, Model model,
      @ModelAttribute("mstCheckForm") MstCheckForm mstCheckForm) {
    CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
    model.addAttribute("csrfToken", csrfToken.getToken());
    model.addAttribute("csrfHeaderName", csrfToken.getHeaderName());
    model.addAttribute("mstCheckForm", mstCheckService.mstCheckForm());
    model.addAttribute("constructions", mstCheckService.getAllChecks());
    model.addAttribute("checks", mstCheckService.search(mstCheckForm));
    model.addAttribute("statusTrue", Constants.STATUS_TRUE);
    return "check/check";
  }
}
