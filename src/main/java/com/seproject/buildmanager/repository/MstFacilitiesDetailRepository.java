package com.seproject.buildmanager.repository;

import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.seproject.buildmanager.entity.MstFacilitiesDetail;

public interface MstFacilitiesDetailRepository extends JpaRepository<MstFacilitiesDetail, Integer> {
  @EntityGraph(value = "MstUser.withAllAssociations", type = EntityGraph.EntityGraphType.FETCH)
  public List<MstFacilitiesDetail> findAll();

  @Query(
      value = "SELECT d.id, d.equipment_detail_bit, d.name, d.preposition, d.postposition FROM mst_facilities_detail d, mst_facilities f "
          + "WHERE f.id=:facilitiesId AND d.equipment_detail_bit&f.equipment_detail_bit!=0",
      nativeQuery = true)
  public List<MstFacilitiesDetail> findFacilitiesDetailByEquipmentDetailBit(
      @Param("facilitiesId") Integer facilitiesId);

  // @Query(
  // value = "SELECT id, equipment_detail_bit, name, preposition, postposition FROM
  // mst_facilities_detail",
  // nativeQuery = true)
  // public List<MstFacilitiesDetail> findFacilitiesDetailByEquipmentDetailBit(
  // @Param("facilitiesId") Integer facilitiesId);
}
