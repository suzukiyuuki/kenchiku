package com.seproject.buildmanager.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "mst_facilities_subcategory")
@Data
@NoArgsConstructor
public class MstFacilitiesSubcategory {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "facilities_id")
  private Integer facilitiesId;

  @OneToOne
  @JoinColumn(name = "facilities_subcategory_title_id")
  private MstFacilitiesSubcategoryTitle facilitiesSubcategoryTitle;

  @Column(name = "status")
  private Integer status;

  @Column(name = "value")
  private String value;
}
