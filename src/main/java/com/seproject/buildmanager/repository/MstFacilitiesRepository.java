package com.seproject.buildmanager.repository;

import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.seproject.buildmanager.entity.MstFacilities;

public interface MstFacilitiesRepository extends JpaRepository<MstFacilities, Integer> {
  @EntityGraph(value = "MstUser.withAllAssociations", type = EntityGraph.EntityGraphType.FETCH)
  public List<MstFacilities> findAll();

  @Query(
      value = "SELECT f.id, f.status, f.equipment_bit, f.name, f.equipment_detail_bit, f.registration_datetime, f.update_datetime, f.update_user FROM mst_facilities f, mst_facilities_management m "
          + "WHERE m.id = :managementId AND m.equipment_bit & f.equipment_bit != 0",
      nativeQuery = true)
  public List<MstFacilities> findFacilitiesByEquipmentBit(
      @Param("managementId") Integer managementId);
}
