package com.seproject.buildmanager.controller;

import java.util.UUID;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.seproject.buildmanager.entity.MstMatter;
import com.seproject.buildmanager.service.MstCheckService;
import com.seproject.buildmanager.service.MstMatterService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("front")
public class FrontController {
  private final MstMatterService mstMatterService;
  private final MstCheckService mstCheckService;

  public FrontController(MstMatterService mstMatterService, MstCheckService mstCheckService) {
    super();
    this.mstMatterService = mstMatterService;
    this.mstCheckService = mstCheckService;
  }

  @GetMapping("")
  public String getAllFront(HttpServletRequest request, Model model) {
    // CSRFトークンをモデルに追加
    CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
    model.addAttribute("csrfToken", csrfToken.getToken());
    model.addAttribute("csrfHeaderName", csrfToken.getHeaderName());
    // 表示用
    model.addAttribute("matterForms", mstMatterService.viewCaseFormByTask());
    return "front/anken";
  }

  @GetMapping("operation")
  public String operationSelect(@RequestParam("id") Integer id, HttpSession session, Model model) {
    // CSRFトークンをモデルに追加
    String transactionToken = UUID.randomUUID().toString();
    session.setAttribute("transactionToken", transactionToken);
    model.addAttribute("transactionToken", transactionToken);
    // 表示
    MstMatter mstCase = mstMatterService.findDisplayById(id);
    model.addAttribute("matterForm", mstMatterService.updateCaseForm(mstCase));
    return "front/operation";
  }

  @GetMapping("check")
  public String matterCheck(@RequestParam("id") Integer id, HttpSession session, Model model,
      HttpServletRequest request) {
    // CSRFトークンをモデルに追加
    CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
    model.addAttribute("csrfToken", csrfToken.getToken());
    model.addAttribute("csrfHeaderName", csrfToken.getHeaderName());
    // 表示
    MstMatter mstCase = mstMatterService.findDisplayById(id);
    model.addAttribute("matterForm", mstMatterService.updateCaseForm(mstCase));
    model.addAttribute("checks", this.mstCheckService.getAllChecks());
    return "front/matterCheck";
  }
}
