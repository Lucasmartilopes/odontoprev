package br.com.fiap.odontoprev.api;

import br.com.fiap.odontoprev.api.dto.ConsultaRequest;
import br.com.fiap.odontoprev.api.dto.ConsultaResponse;
import br.com.fiap.odontoprev.service.ConsultaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/consultas")
public class ConsultaRestController {

    private final ConsultaService consultaService;

    public ConsultaRestController(ConsultaService consultaService) {
        this.consultaService = consultaService;
    }

    @GetMapping
    public List<ConsultaResponse> listar() {
        return consultaService.listarParaApi();
    }

    @GetMapping("/{id}")
    public ConsultaResponse buscar(@PathVariable Long id) {
        return consultaService.buscarResponse(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ConsultaResponse criar(@RequestBody @Valid ConsultaRequest request) {
        return consultaService.criar(request);
    }

    @PutMapping("/{id}")
    public ConsultaResponse atualizar(@PathVariable Long id, @RequestBody @Valid ConsultaRequest request) {
        return consultaService.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {
        consultaService.excluir(id);
    }
}
