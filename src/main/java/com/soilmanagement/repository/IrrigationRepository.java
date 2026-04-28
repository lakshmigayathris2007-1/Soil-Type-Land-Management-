package com.soilmanagement.repository;

import com.soilmanagement.domain.Irrigation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface IrrigationRepository extends JpaRepository<Irrigation, Long> {
    List<Irrigation> findByIrrigatedLand_Id(Long landId);
    List<Irrigation> findByIrrigationMethod(String method);

    @Query("SELECT i FROM Irrigation i WHERE i.irrigationDate BETWEEN :from AND :to")
    List<Irrigation> findByDateRange(@Param("from") LocalDate from, @Param("to") LocalDate to);
}
