package br.com.fiap.odontoprev.repository;

import br.com.fiap.odontoprev.model.Consulta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ConsultaRepository extends JpaRepository<Consulta, Long> {

    @Query("SELECT DISTINCT c FROM Consulta c JOIN FETCH c.paciente ORDER BY c.dataHora DESC")
    List<Consulta> findAllComPaciente();

    @Query("SELECT c FROM Consulta c JOIN FETCH c.paciente WHERE c.id = :id")
    Optional<Consulta> findByIdComPaciente(@Param("id") Long id);
}
