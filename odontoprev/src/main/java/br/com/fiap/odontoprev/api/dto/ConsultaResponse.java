package br.com.fiap.odontoprev.api.dto;

import java.time.LocalDateTime;

public record ConsultaResponse(
        Long id,
        Long pacienteId,
        String pacienteNome,
        LocalDateTime dataHora,
        String procedimento,
        String dentista,
        String status
) {
}
