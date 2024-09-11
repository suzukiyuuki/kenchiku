package com.seproject.buildmanager.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.seproject.buildmanager.entity.MstFacilitiesManagement;

public interface MstFacilitiesManagementRepository
    extends JpaRepository<MstFacilitiesManagement, Integer> {
  @EntityGraph(value = "MstUser.withAllAssociations", type = EntityGraph.EntityGraphType.FETCH)
  public List<MstFacilitiesManagement> findAll();

  @Query(value = "SELECT id, case_id, equipment_bit, value FROM mst_facilities_management WHERE"
      + " case_id = :caseId", nativeQuery = true)
  public Optional<MstFacilitiesManagement> findByCaseId(@Param("caseId") Integer caseId);
}
