package br.com.fiap.odontoprev.service;

import br.com.fiap.odontoprev.model.Paciente;
import br.com.fiap.odontoprev.repository.PacienteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class PacienteService {

    private final PacienteRepository pacienteRepository;

    public PacienteService(PacienteRepository pacienteRepository) {
        this.pacienteRepository = pacienteRepository;
    }

    @Transactional(readOnly = true)
    public List<Paciente> listarTodos() {
        return pacienteRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Paciente buscarPorId(Long id) {
        return pacienteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Paciente não encontrado"));
    }

    @Transactional
    public Paciente salvar(Paciente paciente) {
        return pacienteRepository.save(paciente);
    }

    @Transactional
    public void excluir(Long id) {
        pacienteRepository.deleteById(id);
    }
}
