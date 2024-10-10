package com.seproject.buildmanager.controller;

import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.seproject.buildmanager.common.Constants;
import com.seproject.buildmanager.config.TransactionTokenCheck;
import com.seproject.buildmanager.entity.MstConstruction;
import com.seproject.buildmanager.form.MstConstructionForm;
import com.seproject.buildmanager.service.MstConstructionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("construction")
public class ConstructionController {
  private static final Logger logger = LoggerFactory.getLogger(SpringBootApplication.class);

  private final MstConstructionService mstConstructionService;

  // コンストラクタインジェクションを使用してMstCheckGroupeServiceの依存性を注入
  public ConstructionController(MstConstructionService mstConstructionService) {
    super();
    this.mstConstructionService = mstConstructionService;
  }

  @GetMapping("") // 工事区分分類管理画面を表示する
  public String viewconstruction(Model model, HttpServletRequest request) {

    // CSRFトークンをモデルに追加
    CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
    model.addAttribute("csrfToken", csrfToken.getToken());
    model.addAttribute("csrfHeaderName", csrfToken.getHeaderName());
    model.addAttribute("MstConstructionForm", mstConstructionService.mstConstructionForm());
    model.addAttribute("statusTrue", Constants.STATUS_TRUE);
    model.addAttribute("constructions", mstConstructionService.getAllConstructions());
    model.addAttribute("searchConstruction", mstConstructionService.mstConstructionForm());

    // 検索
    model.addAttribute("search", mstConstructionService.mstConstructionForm());
    return "construction/construction";
  }

  @PostMapping("poi")
  public String generateExcel(
      @ModelAttribute("searchConstruction") MstConstructionForm mstConstructionForm,
      HttpServletResponse response) {
    mstConstructionService.download(mstConstructionService.search(mstConstructionForm));
    return "redirect:/construction";
  }

  @GetMapping("search")
  public String search(HttpServletRequest request, Model model,
      @ModelAttribute("search") MstConstructionForm mstConstructionForm) {
    // CSRFトークンをモデルに追加
    CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
    model.addAttribute("csrfToken", csrfToken.getToken());
    model.addAttribute("csrfHeaderName", csrfToken.getHeaderName());
    model.addAttribute("MstConstructionForm", mstConstructionService.mstConstructionForm());
    model.addAttribute("statusTrue", Constants.STATUS_TRUE);
    model.addAttribute("constructions", mstConstructionService.search(mstConstructionForm));
    model.addAttribute("searchConstruction", mstConstructionForm);

    // 検索
    model.addAttribute("search", mstConstructionService.mstConstructionForm());
    return "construction/construction";

  }

  @GetMapping("register")
  public String registConstruction(HttpSession session, Model model) {
    String transactionToken = UUID.randomUUID().toString();
    session.setAttribute("transactionToken", transactionToken);
    model.addAttribute("transactionToken", transactionToken);
    model.addAttribute("MstConstructionForm", mstConstructionService.mstConstructionForm());
    return "construction/construction_register";
  }

  @PostMapping("register")
  public String registConstruction(
      @ModelAttribute("MstConstructionForm") MstConstructionForm mstConstructionForm,
      BindingResult bindingResult, HttpSession session, Model model) {
    String transactionToken = UUID.randomUUID().toString();
    session.setAttribute("transactionToken", transactionToken);
    model.addAttribute("transactionToken", transactionToken);
    model.addAttribute("mstConstructionForm", mstConstructionForm);
    return "construction/construction_register_confirm";
  }

  @PostMapping("save")
  public String saveConstruction(
      @ModelAttribute("MstConstructionForm") MstConstructionForm mstConstructionForm,
      HttpSession session, Model model) {
    String transactionToken = UUID.randomUUID().toString();
    session.setAttribute("transactionToken", transactionToken);
    model.addAttribute("transactionToken", transactionToken);
    model.addAttribute("mstConstructionForm", mstConstructionForm);
    mstConstructionService.saveConstructionRegister(mstConstructionForm);
    return "redirect:/construction/save";
  }

  @GetMapping("save")
  public String saveComplete() {
    return "construction/construction_register_end";
  }

  @GetMapping("update")
  public String updateConstructionForm(@RequestParam(value = "id") int id, HttpSession session,
      Model model) {
    String transactionToken = UUID.randomUUID().toString();
    session.setAttribute("transactionToken", transactionToken);
    model.addAttribute("transactionToken", transactionToken);
    MstConstruction mstConstruction = mstConstructionService.getConstructionById(id);
    model.addAttribute("mstConstruction",
        mstConstructionService.updateConstruction(mstConstruction));
    return "construction/construction_update";
  }

  @PostMapping("update")
  public String processUpdate(
      @ModelAttribute("mstConstruction") MstConstructionForm mstConstructionForm,
      BindingResult result, HttpSession session, Model model) {
    String transactionToken = UUID.randomUUID().toString();
    session.setAttribute("transactionToken", transactionToken);
    model.addAttribute("transactionToken", transactionToken);
    model.addAttribute("mstConstructionForm", mstConstructionForm);
    return "construction/construction_update_confirm";
  }

  @PostMapping("saveUpdate")
  @TransactionTokenCheck("saveUpdate")
  public String saveConstructionUpdate(
      @ModelAttribute("mstConstructionForm") MstConstructionForm mstConstructionForm) {
    mstConstructionService.saveConstructionRegister(mstConstructionForm);
    return "redirect:/construction/saveUpdate";
  }

  @GetMapping("saveUpdate")
  public String completeConstructionUpdate() {
    return "construction/construction_update_end";
  }



}
