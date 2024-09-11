package com.seproject.buildmanager.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.seproject.buildmanager.common.Constants;
import com.seproject.buildmanager.common.MstCodeEnums;
import com.seproject.buildmanager.entity.MstEstimateItem;
import com.seproject.buildmanager.entity.MstMatter;
import com.seproject.buildmanager.entity.MstUser;
import com.seproject.buildmanager.service.CommonService;
import com.seproject.buildmanager.service.MstCodeService;
import com.seproject.buildmanager.service.MstEstimateItemService;
import com.seproject.buildmanager.service.MstEstimateManagementService;
import com.seproject.buildmanager.service.MstFloorManagementService;
import com.seproject.buildmanager.service.MstMatterService;
import com.seproject.buildmanager.service.MstUserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("estimate")
public class EstimateManagementController {

  private static final Logger logger = LoggerFactory.getLogger(SpringBootApplication.class);

  @Autowired
  private MstEstimateManagementService mstEstimateManagementService;

  @Autowired
  private MstEstimateItemService mstEstimateItemService;

  @Autowired
  private MstCodeService mstCodeService;

  @Autowired
  private MstMatterService mstMatterService;

  @Autowired
  private MstFloorManagementService mstFloorManagementService;

  @Autowired
  private MstUserService mstUserService;

  @Autowired
  private CommonService commonService;

  // Enumから状況ステータスを取得するため
  private static final int SITUATION_STATUS = MstCodeEnums.SITUATION_STATUS.getValue();

  // Enumから種別を取得するためののcode_kindの値を取得
  private static final int TASK_SUBSTANCE = MstCodeEnums.TASK_SUBSTANCE.getValue();



  public EstimateManagementController(MstEstimateManagementService mstEstimateManagementService,
      MstCodeService mstCodeService) {
    super();
    this.mstEstimateManagementService = mstEstimateManagementService;
    this.mstCodeService = mstCodeService;
  }

  @GetMapping("")
  public String getEstmateManagement(Model model, HttpServletRequest request) {
    logger.info("--- CaseController.getCase START ---");
    CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
    model.addAttribute("csrfToken", csrfToken.getToken());
    model.addAttribute("csrfHeaderName", csrfToken.getHeaderName());
    model.addAttribute("statusTrue", Constants.STATUS_TRUE);

    model.addAttribute("estimateManagementInfo", mstMatterService.estimateMatter());
    // 状況ステータスを取得するためのリスト
    model.addAttribute("situation", mstCodeService.getCodeByKind(SITUATION_STATUS));
    // 種別を取得するためのリスト
    model.addAttribute("caseKind", mstCodeService.getCodeByKind(TASK_SUBSTANCE));

    return "estimateManagement/estimateManagement";
  }

  @GetMapping("/request")

  public String quotation_approval(@RequestParam(value = "id") String matterId, HttpSession session,
      Model model, HttpServletRequest request) {

    MstMatter mstCase = mstMatterService.findDisplayById(Integer.parseInt(matterId));
    model.addAttribute("mstCaseForm", mstMatterService.updateCaseForm(mstCase));

    // logger.info("--- UserController.getAllUsers START ---");

    CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
    model.addAttribute("csrfToken", csrfToken.getToken());
    model.addAttribute("csrfHeaderName", csrfToken.getHeaderName());
    model.addAttribute("matter", mstMatterService.findDisplay());
    model.addAttribute("statusTrue", Constants.STATUS_TRUE);
    model.addAttribute("estimateItem", mstEstimateItemService.findAll());

    String transactionToken = UUID.randomUUID().toString();
    session.setAttribute("transactionToken", transactionToken);
    model.addAttribute("transactionToken", transactionToken);

    model.addAttribute("matterId", matterId);

    List<MstEstimateItem> estimateItems = mstEstimateItemService.findAll();
    model.addAttribute("estimateItems", estimateItems);

    // logger.info("--- UserController.getAllUsers END ---");

    csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
    model.addAttribute("csrfToken", csrfToken.getToken());
    // model.addAttribute("");

    return "estimateManagement/quotation_approval_request";
  }


  @PostMapping("/request")
  public String quotationApproval(@RequestParam("email") String email, // 追加されたリクエストパラメータ
      @RequestParam("loginCd") String loginCd, HttpSession session, Model model) {

    // ログインユーザーの情報取得
    MstUser mstUser = mstUserService.getUserById(commonService.getLoginUserId());

    // ランダムなトークンを生成
    String emailToken = UUID.randomUUID().toString();

    // 承認依頼メールを送信
    mstEstimateManagementService.sendSimpleMail(email, emailToken, loginCd);

    // 見積もり承認リクエストページに遷移
    return "estimateManagement/quotation_approval_request";
  }



  @GetMapping("/cancel")

  public String approval_request(@RequestParam(value = "id") String matterId, HttpSession session,
      Model model, HttpServletRequest request) {

    MstMatter mstCase = mstMatterService.findDisplayById(Integer.parseInt(matterId));
    model.addAttribute("mstCaseForm", mstMatterService.updateCaseForm(mstCase));

    CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
    model.addAttribute("csrfToken", csrfToken.getToken());
    // model.addAttribute("");

    return "estimateManagement/approval_request_cancel";
  }

  @GetMapping("/detail")
  public String detail(@RequestParam(value = "id") String matterId,
      @RequestParam(value = "varsion", required = false) Integer varsion, HttpSession session,
      Model model, HttpServletRequest request) {

    if (varsion != null) {
      model.addAttribute("varsion", varsion);
    } else {
      model.addAttribute("varsion",
          mstEstimateManagementService.newestVarsion(Integer.parseInt(matterId)));
    }
    model.addAttribute("newestvarsion",
        mstEstimateManagementService.newestVarsion(Integer.parseInt(matterId)));
    MstMatter mstCase = mstMatterService.findDisplayById(Integer.parseInt(matterId));
    model.addAttribute("mstCaseForm", mstMatterService.updateCaseForm(mstCase));

    // logger.info("--- UserController.getAllUsers START ---");

    CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
    model.addAttribute("csrfToken", csrfToken.getToken());
    model.addAttribute("csrfHeaderName", csrfToken.getHeaderName());
    model.addAttribute("matter", mstMatterService.findDisplay());
    model.addAttribute("statusTrue", Constants.STATUS_TRUE);
    model.addAttribute("estimateItem", mstEstimateItemService.findAll());

    String transactionToken = UUID.randomUUID().toString();
    session.setAttribute("transactionToken", transactionToken);
    model.addAttribute("transactionToken", transactionToken);

    model.addAttribute("totals", this.mstEstimateManagementService.mekeEstimateTotals());

    model.addAttribute("matterId", matterId);

    List<MstEstimateItem> estimateItems = mstEstimateItemService.findAll();
    model.addAttribute("estimateItems", estimateItems);

    // logger.info("--- UserController.getAllUsers END ---");
    return "estimateManagement/detail";
  }



  @GetMapping("/consent")
  public String consent(@RequestParam("id") String matterId, HttpSession session, Model model,
      HttpServletRequest request) {

    // logger.info("--- UserController.getAllUsers START ---");

    CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
    model.addAttribute("csrfToken", csrfToken.getToken());
    model.addAttribute("csrfHeaderName", csrfToken.getHeaderName());
    model.addAttribute("matter", mstMatterService.findDisplayById(Integer.parseInt(matterId)));
    model.addAttribute("floor", mstFloorManagementService.viewFloorForm());
    model.addAttribute("estimate",
        mstEstimateManagementService.getEstimateId(Integer.parseInt(matterId)));
    model.addAttribute("estimateItem",
        mstEstimateItemService.findAllById(Integer.parseInt(matterId)));
    model.addAttribute("statusTrue", Constants.STATUS_TRUE);

    String transactionToken = UUID.randomUUID().toString();
    session.setAttribute("transactionToken", transactionToken);
    model.addAttribute("transactionToken", transactionToken);

    model.addAttribute("matterId", matterId);

    MstMatter matter = mstMatterService.findDisplayById(Integer.parseInt(matterId));
    LocalDateTime scheduledVisitDatetime = matter.getScheduledVisitDatetime();

    model.addAttribute("formattedDate", formatDateForJapaneseEra(scheduledVisitDatetime));
    model.addAttribute("formattedTime", formatTime(scheduledVisitDatetime));

    return "estimateManagement/consentForm";
  }


  private String formatDateForJapaneseEra(LocalDateTime dateTime) {
    int year = dateTime.getYear() - 2018;
    int month = dateTime.getMonthValue();
    int day = dateTime.getDayOfMonth();
    return String.format("令和%d年%d月%d日", year, month, day);
  }

  private String formatTime(LocalDateTime dateTime) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
    return dateTime.format(formatter);
  }

}
