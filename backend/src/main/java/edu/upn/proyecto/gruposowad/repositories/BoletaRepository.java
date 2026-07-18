package edu.upn.proyecto.gruposowad.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import edu.upn.proyecto.gruposowad.models.Boleta;

@Repository
public interface BoletaRepository extends JpaRepository<Boleta,Long>{

    @Query(value = "select coalesce(max(cast(substring(numero_boleta, 6) as unsigned)), 0) "
            + "from boleta "
            + "where numero_boleta like concat(:serie, '-%') "
            + "and numero_boleta regexp concat('^', :serie, '-[0-9]{8}$')",
            nativeQuery = true)
    Integer maxCorrelativoPorSerie(@Param("serie") String serie);

    @Query("select b from Boleta b where b.venta.id_venta = :idVenta")
    Optional<Boleta> findByVentaIdVenta(@Param("idVenta") Long idVenta);

}
