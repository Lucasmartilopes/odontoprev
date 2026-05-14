package br.com.fiap.odontoprev.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record ConsultaRequest(
        @NotNull Long pacienteId,
        @NotNull LocalDateTime dataHora,
        @NotBlank @Size(max = 120) String procedimento,
        @NotBlank @Size(max = 120) String dentista,
        @NotBlank
        @Pattern(regexp = "AGENDADA|REALIZADA|CANCELADA")
        @Size(max = 20)
        String status
) {
}
