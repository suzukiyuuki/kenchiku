package com.seproject.buildmanager.entity;

import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "mst_facilities")
@Data
@NoArgsConstructor
public class MstFacilities {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "status")
  private Integer status;

  @Column(name = "equipment_bit")
  private Integer equipmentBit;

  @Column(name = "name")
  private String name;

  @Column(name = "equipment_detail_bit")
  private Integer equipmentDetailBit;

  @Column(name = "registration_datetime")
  private LocalDateTime registrationDatetime; // 登録日

  @Column(name = "update_datetime")
  private LocalDateTime updateDatetime; // 最終更新日

  @Column(name = "update_user")
  private Integer updateUser; // 最終更新者
}
