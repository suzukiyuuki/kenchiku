package com.seproject.buildmanager.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "mst_facilities_management")
@Data
@NoArgsConstructor
public class MstFacilitiesManagement {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "case_id")
  private Integer caseId;

  @Column(name = "equipment_bit")
  private Integer equipmentBit;

  @Column(name = "value")
  private String value;
}
