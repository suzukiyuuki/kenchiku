package com.seproject.buildmanager.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Service;
import com.seproject.buildmanager.common.MstCodeEnums;
import com.seproject.buildmanager.entity.MstAcceptingOrder;
import com.seproject.buildmanager.entity.MstAcceptingOrderDetail;
import com.seproject.buildmanager.entity.MstAcceptingOrderHistory;
import com.seproject.buildmanager.entity.MstCode;
import com.seproject.buildmanager.entity.MstMatter;
import com.seproject.buildmanager.form.MstAcceptingOrderDetailForm;
import com.seproject.buildmanager.form.MstAcceptingOrderForm;
import com.seproject.buildmanager.form.MstMatterForm;
import com.seproject.buildmanager.repository.MstAcceptingOrderDetailRepository;
import com.seproject.buildmanager.repository.MstAcceptingOrderHistoryRepository;
import com.seproject.buildmanager.repository.MstAcceptingOrderRepository;
import com.seproject.buildmanager.repository.MstCodeRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class MstAcceptingOrderService implements MstSearchService<MstMatterForm, MstMatterForm> {

  private static final Logger logger = LoggerFactory.getLogger(SpringBootApplication.class);

  private final MstAcceptingOrderRepository mstAcceptingOrderRepository;

  private final MstAcceptingOrderDetailRepository mstAcceptingOrderDetailRepository;

  private final MstAcceptingOrderHistoryRepository mstAcceptingOrderHistoryRepository;

  private final MstCodeService mstCodeService;

  private final MstSupplierManagementService mstSupplierManagementService;

  private final MstCodeRepository mstCodeRepesitory;

  private final CommonService commonService;

  private final MstMatterService mstMatterService;

  private final MstEstimateItemService mstEstimateItemService;

  // Enumから種別を取得するためののcode_kindの値を取得
  private static final int TASK_SUBSTANCE = MstCodeEnums.TASK_SUBSTANCE.getValue();

  // 状況ステータス取得
  private static final int SITUATION_STATUS = MstCodeEnums.SITUATION_STATUS.getValue();

  private static final int ORDER_STATUS = MstCodeEnums.ORDER_STATUS.getValue();

  private static final int UNIT = MstCodeEnums.UNIT.getValue();



  private MstAcceptingOrderService(MstAcceptingOrderRepository mstAcceptingOrderRepository,
      MstAcceptingOrderDetailRepository mstAcceptingOrderDetailRepository,
      MstAcceptingOrderHistoryRepository mstAcceptingOrderHistoryRepository,
      MstCodeService mstCodeService, MstSupplierManagementService mstSupplierManagementService,
      MstCodeRepository mstCodeRepesitory, CommonService commonService,
      MstMatterService mstMatterService, MstEstimateItemService mstEstimateItemService) {
    this.mstAcceptingOrderRepository = mstAcceptingOrderRepository;
    this.mstAcceptingOrderDetailRepository = mstAcceptingOrderDetailRepository;
    this.mstAcceptingOrderHistoryRepository = mstAcceptingOrderHistoryRepository;
    this.mstMatterService = mstMatterService;
    this.mstCodeService = mstCodeService;
    this.mstSupplierManagementService = mstSupplierManagementService;
    this.mstCodeRepesitory = mstCodeRepesitory;
    this.commonService = commonService;
    this.mstEstimateItemService = mstEstimateItemService;
  }

  ////////////////// 一覧表示////////////////////////////
  // 発注一覧表示
  public List<MstAcceptingOrder> findDisplay() {
    List<MstAcceptingOrder> OrderInfoList = mstAcceptingOrderRepository.findAll();
    return OrderInfoList;
  }

  // 案件一覧表示
  public List<MstMatter> findCaseDisplay() {
    return mstMatterService.findDisplay();
  }

  //////////////////////////////////////////////////////
  ///////////////// id検索///////////////////////////////
  // 発注id検索
  public List<MstAcceptingOrder> getAllOrder(int id) {
    List<MstAcceptingOrder> acception = mstAcceptingOrderRepository.getAcceptingOrder(id);
    return acception;
  }

  // 表示用発注id
  public List<MstAcceptingOrderForm> viewAcceptionOrderForm(int id) {
    List<MstAcceptingOrder> acception = getAllOrder(id);
    List<MstAcceptingOrderForm> mstOrderForm = new ArrayList<MstAcceptingOrderForm>();
    for (MstAcceptingOrder order : acception) {
      mstOrderForm.add(EntityToForm(order));
    }
    return mstOrderForm;
  }

  //////////////////////////////////////////////////////////
  ////////// エンティティからフォーム変換/////////////////////////////

  // 発注のエンティティからフォームに変換
  public MstAcceptingOrderForm EntityToForm(MstAcceptingOrder mstAcceptingOrder) {
    MstAcceptingOrderForm tmp = new MstAcceptingOrderForm();
    tmp.setId(mstAcceptingOrder.getId().toString());
    tmp.setMatterId(mstMatterService.updateCaseForm(mstAcceptingOrder.getMatterId()));
    tmp.setEstimateItemId(
        mstEstimateItemService.updateEstimateItemForm(mstAcceptingOrder.getEstimateItemId()));
    tmp.setSuppliermanagementId(mstAcceptingOrder.getSuppliermanagerId().toString());
    tmp.setSuppliermanagementName(
        mstSupplierManagementService.getSuppliereById(mstAcceptingOrder.getId()).getVenderName());
    MstCode mstCode = mstCodeRepesitory
        .findByCodeKindAndBranchNum(ORDER_STATUS, mstAcceptingOrder.getOrderStatus())
        .orElse(new MstCode());
    tmp.setOrderStatus(mstCode.getCodeName());
    tmp.setConStartDate(mstAcceptingOrder.getStartDate());
    tmp.setConEndDate(mstAcceptingOrder.getEndDate());
    tmp.setSubtotal(mstAcceptingOrder.getSubtotal().toString());
    tmp.setConsumptionTax(mstAcceptingOrder.getConsumptionTax().toString());
    tmp.setTotal(mstAcceptingOrder.getTotal().toString());
    tmp.setKeyLocation(mstAcceptingOrder.getKeyLocation());
    tmp.setRegisteredUserId(this.commonService.getLoginUserId());
    tmp.setRegistrationDatetime(mstAcceptingOrder.getRegistrationDatetime());
    tmp.setLastUpdatedDatetime(mstAcceptingOrder.getLastUpdatedDatetime());
    return tmp;
  }

  // 発注詳細エンティティからフォーム変換
  public MstAcceptingOrderDetailForm EntityToDetailForm(
      MstAcceptingOrderDetail mstAcceptingOrderDetail) {
    MstAcceptingOrderDetailForm tmp = new MstAcceptingOrderDetailForm();
    tmp.setId(mstAcceptingOrderDetail.getId());
    tmp.setMatterId(mstAcceptingOrderDetail.getMatterId().toString());
    tmp.setOrderId(EntityToForm(mstAcceptingOrderDetail.getOrderId()));
    tmp.setEstimateItemId(mstAcceptingOrderDetail.getEstimateItemId().toString());
    tmp.setContent(mstAcceptingOrderDetail.getContent());
    tmp.setManufacturerName(mstAcceptingOrderDetail.getManufacturerName());
    tmp.setModelNumber(mstAcceptingOrderDetail.getModelNumber());
    MstCode mstCode = mstCodeRepesitory
        .findByCodeKindAndBranchNum(UNIT, mstAcceptingOrderDetail.getUnit()).orElse(new MstCode());
    tmp.setUnit(mstCode.getCodeName());
    tmp.setUnitPrice(mstAcceptingOrderDetail.getUnitPrice().toString());
    tmp.setVolume(mstAcceptingOrderDetail.getVolume().toString());
    tmp.setSubtotal(mstAcceptingOrderDetail.getSubtotal().toString());
    tmp.setNote(mstAcceptingOrderDetail.getNote());
    tmp.setRegisteredUserId(this.commonService.getLoginUserId());
    tmp.setRegistrationDatetime(mstAcceptingOrderDetail.getRegistrationDatetime());
    tmp.setLastUpdatedDatetime(mstAcceptingOrderDetail.getLastUpdatedDatetime());
    return tmp;
  }

  ////////////////////////////////////////////////////////////
  ///////// 表示用//////////////////////////////////////////////

  // 発注詳細表示
  public List<MstAcceptingOrderDetail> getAllOrderDetail(int id) {
    List<MstAcceptingOrderDetail> detail =
        mstAcceptingOrderDetailRepository.getAcceptingOrderDetail(id);
    return detail;
  }

  // 表示用発注詳細
  public List<MstAcceptingOrderDetailForm> viewAcceptionOrderDetailForm(int id) {
    List<MstAcceptingOrderDetail> detail = getAllOrderDetail(id);
    List<MstAcceptingOrderDetailForm> mstOrderDetailForm =
        new ArrayList<MstAcceptingOrderDetailForm>();
    for (MstAcceptingOrderDetail order : detail) {
      mstOrderDetailForm.add(EntityToDetailForm(order));
    }
    return mstOrderDetailForm;
  }

  // 表示用発注
  public List<MstAcceptingOrderForm> viewOrderForm() {
    List<MstAcceptingOrder> mstOrder = findDisplay();
    List<MstAcceptingOrderForm> mstOrderForm = new ArrayList<MstAcceptingOrderForm>();
    for (MstAcceptingOrder orderNum : mstOrder) {
      mstOrderForm.add(EntityToForm(orderNum));
    }
    return mstOrderForm;
  }
  ////////////////////////////////////////////////////////////////

  // 案件表示(ステータスが受注から請求済みまで)
  public List<MstMatterForm> viewCaseForm() {
    List<MstMatter> mstCase = findCaseDisplay();
    List<MstMatterForm> mstCaseForm = new ArrayList<MstMatterForm>();
    MstCode code = mstCodeService.getCodeByKindAndName("受注", SITUATION_STATUS);
    for (MstMatter caseNum : mstCase) {
      if (caseNum.getSituationStatus() == code.getCodeBranchNum())
        mstCaseForm.add(mstMatterService.updateCaseForm(caseNum));
    }

    return mstCaseForm;

  }

  public List<MstMatterForm> selectMatterBySituationStatus(List<MstMatterForm> matterFormList) {
    List<MstMatterForm> mstCaseForm = new ArrayList<MstMatterForm>();
    MstCode code = mstCodeService.getCodeByKindAndName("受注", SITUATION_STATUS);
    for (MstMatterForm caseNum : matterFormList) {
      if (caseNum.getSituationStatus().equals(code.getCodeName()))
        mstCaseForm.add(caseNum);
    }
    return mstCaseForm;
  }

  ///////////// 検索/////////////////////////////////////////////
  // 案件検索
  public List<MstMatterForm> search(MstMatterForm mstMatterForm) {
    return mstMatterService.search(mstMatterForm);
  }

  // 発注検索
  public List<MstAcceptingOrderForm> orderSearch(MstAcceptingOrderForm mstAcceptingOrderForm,
      Integer id) {
    String orderStatus = "";
    String suppliermanagementName = "";
    String cStart = "";
    String cEnd = "";

    MstAcceptingOrderForm order = mstAcceptingOrderForm;
    if (order.getOrderStatus() == null || order.getOrderStatus().equals("")) {
      orderStatus += "";
    } else {
      MstCode mstCode = mstCodeService.getCodeByKindAndName(order.getOrderStatus(), ORDER_STATUS);
      order.setOrderStatus(mstCode.getCodeBranchNum().toString());
      orderStatus += order.getOrderStatus();
    }
    suppliermanagementName += nullCheck(order.getSuppliermanagementName());
    cStart += nullCheck(order.getCStart());
    cEnd += nullCheck(order.getCEnd());

    List<MstAcceptingOrder> a =
        mstAcceptingOrderRepository.search(orderStatus, suppliermanagementName, cStart, cEnd, id);

    List<MstAcceptingOrderForm> orderFormList = new ArrayList<MstAcceptingOrderForm>();
    for (MstAcceptingOrder order1 : a) {
      orderFormList.add(EntityToForm(order1));
    }
    return orderFormList;

  }
  ////////////////////////////////////////////////////////////////
  ///////////////// インスタンス///////////////////////////////////////

  // 発注インスタンス
  public MstAcceptingOrderForm showAcceptingOrderForm() {
    MstAcceptingOrderForm tmp = new MstAcceptingOrderForm();
    return tmp;
  }

  // 発注詳細インスタンス
  public MstAcceptingOrderDetailForm showAcceptingOrderDetailForm() {
    logger.info("--- MstAcceptingOrderService.showAcceptingOrderDetailForm START ---");

    MstAcceptingOrderDetailForm tmp = new MstAcceptingOrderDetailForm();

    logger.info("--- MstAcceptingOrderService.showAcceptingOrderDetailForm END ---");
    return tmp;
  }

  ///////////////////////////////////////////////////////////////

  // 工事開始日
  public LocalDateTime getEarliestOrderStartDates(int i) {
    List<Object[]> results = mstAcceptingOrderRepository.findOrderStartDateByMatter();
    LocalDateTime earliestDates = null;
    for (Object[] result : results) {
      // Timestamp を LocalDate に変換する
      if (i == (int) result[0]) {
        Timestamp timestamp = (Timestamp) result[1];
        LocalDateTime earliestDate = timestamp.toLocalDateTime();
        earliestDates = earliestDate;
      }
    }
    return earliestDates;
  }

  // 工事終了日
  public LocalDateTime getLattermostOrderEndDates(int i) {
    List<Object[]> results = mstAcceptingOrderRepository.findOrderEndDateByMatter();
    LocalDateTime lattermostDates = null;
    for (Object[] result : results) {
      // Timestamp を LocalDate に変換する
      if (i == (int) result[0]) {
        Timestamp timestamp = (Timestamp) result[1];
        LocalDateTime lattermostDate = timestamp.toLocalDateTime();
        lattermostDates = lattermostDate;
      }

    }
    return lattermostDates;
  }


  public void orderStatus(int orderId, int status) {
    Optional<MstAcceptingOrder> order = mstAcceptingOrderRepository.findById(orderId);
    if (order.isPresent()) {
      order.get().setOrderStatus(status);
      order.get().setLastUpdatedDatetime(LocalDateTime.now());
      mstAcceptingOrderRepository.save(order.get());
    } else {
      throw new EntityNotFoundException("Order not found");
    }
  }

  public void addOrderHistory(int orderId, String updateContent) {
    MstAcceptingOrderHistory history = new MstAcceptingOrderHistory();
    history.setOrderId(orderId);
    history.setNote(updateContent);
    history.setRegistrationDatetime(LocalDateTime.now()); // 現在時刻を設定
    history.setLastUpdatedDatetime(LocalDateTime.now());

    try {
      mstAcceptingOrderHistoryRepository.save(history); // データベースに保存
    } catch (Exception e) {
      // エラーログを出力
      e.printStackTrace();
      throw new RuntimeException("Failed to add order history: " + e.getMessage());
    }
  }

  public List<MstMatterForm> selectViewMatterForm(List<MstMatterForm> selectView) {
    List<MstMatterForm> form = new ArrayList<MstMatterForm>();
    for (MstMatterForm a : selectView) {
      if (a.getSelectViewMatterForm() >= 6 && a.getSelectViewMatterForm() < 11) {
        a.setConStartDate(getEarliestOrderStartDates(a.getId()));
        a.setConEndDate(getLattermostOrderEndDates(a.getId()));
        form.add(a);
      }
    }
    return form;

  }
}
