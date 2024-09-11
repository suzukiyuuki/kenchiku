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
@Table(name = "mst_facilities_detail")
@Data
@NoArgsConstructor
public class MstFacilitiesDetail {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "equipment_detail_bit")
  private Integer equipmentDetailBit;

  @Column(name = "name")
  private String name;

  @Column(name = "preposition")
  private String preposition;

  @Column(name = "postposition")
  private String postposition;
}
