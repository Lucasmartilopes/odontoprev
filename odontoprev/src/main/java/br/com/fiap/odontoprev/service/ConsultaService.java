package br.com.fiap.odontoprev.service;

import br.com.fiap.odontoprev.api.dto.ConsultaRequest;
import br.com.fiap.odontoprev.api.dto.ConsultaResponse;
import br.com.fiap.odontoprev.model.Consulta;
import br.com.fiap.odontoprev.model.Paciente;
import br.com.fiap.odontoprev.repository.ConsultaRepository;
import br.com.fiap.odontoprev.repository.PacienteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ConsultaService {

    private final ConsultaRepository consultaRepository;
    private final PacienteRepository pacienteRepository;

    public ConsultaService(ConsultaRepository consultaRepository, PacienteRepository pacienteRepository) {
        this.consultaRepository = consultaRepository;
        this.pacienteRepository = pacienteRepository;
    }

    @Transactional(readOnly = true)
    public List<Consulta> listarTodos() {
        return consultaRepository.findAllComPaciente();
    }

    @Transactional(readOnly = true)
    public Consulta buscarPorId(Long id) {
        return consultaRepository.findByIdComPaciente(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Consulta não encontrada"));
    }

    @Transactional
    public Consulta salvar(Consulta consulta) {
        return consultaRepository.save(consulta);
    }

    @Transactional
    public void excluir(Long id) {
        consultaRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<ConsultaResponse> listarParaApi() {
        return listarTodos().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public ConsultaResponse buscarResponse(Long id) {
        return toResponse(buscarPorId(id));
    }

    @Transactional
    public ConsultaResponse criar(ConsultaRequest request) {
        Paciente paciente = pacienteRepository.findById(request.pacienteId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Paciente não encontrado"));
        Consulta c = Consulta.builder()
                .paciente(paciente)
                .dataHora(request.dataHora())
                .procedimento(request.procedimento())
                .dentista(request.dentista())
                .status(request.status())
                .build();
        return toResponse(consultaRepository.save(c));
    }

    @Transactional
    public ConsultaResponse atualizar(Long id, ConsultaRequest request) {
        Consulta existente = consultaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Consulta não encontrada"));
        Paciente paciente = pacienteRepository.findById(request.pacienteId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Paciente não encontrado"));
        existente.setPaciente(paciente);
        existente.setDataHora(request.dataHora());
        existente.setProcedimento(request.procedimento());
        existente.setDentista(request.dentista());
        existente.setStatus(request.status());
        return toResponse(consultaRepository.save(existente));
    }

    private ConsultaResponse toResponse(Consulta c) {
        return new ConsultaResponse(
                c.getId(),
                c.getPaciente().getId(),
                c.getPaciente().getNome(),
                c.getDataHora(),
                c.getProcedimento(),
                c.getDentista(),
                c.getStatus()
        );
    }
}
