package br.com.fiap.odontoprev.repository;

import br.com.fiap.odontoprev.model.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PacienteRepository extends JpaRepository<Paciente, Long> {
}
