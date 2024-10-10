package com.seproject.buildmanager.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.seproject.buildmanager.entity.MstFacilitiesSubcategory;

public interface MstFacilitiesSubcategoryRepository
    extends JpaRepository<MstFacilitiesSubcategory, Integer> {
  @EntityGraph(value = "MstUser.withAllAssociations", type = EntityGraph.EntityGraphType.FETCH)
  public List<MstFacilitiesSubcategory> findAll();

  @Query(
      value = "SELECT id, facilities_id, facilities_subcategory_title_id, status, value FROM mst_facilities_subcategory d "
          + "WHERE facilities_id=:facilitiesId",
      nativeQuery = true)
  public List<MstFacilitiesSubcategory> findByFacilitiesId(
      @Param("facilitiesId") Integer facilitiesId);

  @Query(
      value = "SELECT id, facilities_id, facilities_subcategory_title_id, status, value FROM mst_facilities_subcategory d "
          + "WHERE facilities_id=:facilitiesId AND facilities_subcategory_title_id = :titleId",
      nativeQuery = true)
  public Optional<MstFacilitiesSubcategory> findByFacilitiesIdAndTitleId(
      @Param("facilitiesId") Integer facilitiesId, @Param("titleId") Integer titleId);
}
