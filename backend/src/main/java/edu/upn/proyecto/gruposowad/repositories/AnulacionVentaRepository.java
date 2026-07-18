package edu.upn.proyecto.gruposowad.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import edu.upn.proyecto.gruposowad.models.AnulacionVenta;

@Repository
public interface AnulacionVentaRepository extends JpaRepository<AnulacionVenta,Long>{
    @Query("select a from AnulacionVenta a where a.venta.id_venta = :idVenta")
    Optional<AnulacionVenta> findByVentaIdVenta(@Param("idVenta") Long idVenta);
}
