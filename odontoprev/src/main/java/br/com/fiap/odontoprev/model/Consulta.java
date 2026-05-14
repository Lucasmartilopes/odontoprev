package br.com.fiap.odontoprev.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "consulta")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Consulta {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "paciente_id")
    @JsonBackReference
    private Paciente paciente;

    @NotNull
    @Column(name = "data_hora")
    private LocalDateTime dataHora;

    @NotBlank @Size(max = 120)
    private String procedimento;

    @NotBlank @Size(max = 120)
    private String dentista;

    @NotBlank @Size(max = 20)
    private String status; // AGENDADA, REALIZADA, CANCELADA
}
