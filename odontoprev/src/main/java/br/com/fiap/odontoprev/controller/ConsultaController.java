package br.com.fiap.odontoprev.controller;

import br.com.fiap.odontoprev.model.Consulta;
import br.com.fiap.odontoprev.service.ConsultaService;
import br.com.fiap.odontoprev.service.PacienteService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/consultas")
public class ConsultaController {

    private final ConsultaService consultaService;
    private final PacienteService pacienteService;

    public ConsultaController(ConsultaService consultaService, PacienteService pacienteService) {
        this.consultaService = consultaService;
        this.pacienteService = pacienteService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("consultas", consultaService.listarTodos());
        return "consultas/lista";
    }

    @GetMapping("/nova")
    public String nova(Model model) {
        model.addAttribute("consulta", new Consulta());
        model.addAttribute("pacientes", pacienteService.listarTodos());
        return "consultas/form";
    }

    @PostMapping
    public String salvar(@Valid @ModelAttribute Consulta consulta, BindingResult br, Model model) {
        if (br.hasErrors()) {
            model.addAttribute("pacientes", pacienteService.listarTodos());
            return "consultas/form";
        }
        consultaService.salvar(consulta);
        return "redirect:/consultas";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("consulta", consultaService.buscarPorId(id));
        model.addAttribute("pacientes", pacienteService.listarTodos());
        return "consultas/form";
    }

    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Long id) {
        consultaService.excluir(id);
        return "redirect:/consultas";
    }
}
