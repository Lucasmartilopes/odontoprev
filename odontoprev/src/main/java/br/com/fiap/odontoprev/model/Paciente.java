package br.com.fiap.odontoprev.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "paciente")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Paciente {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank @Size(max = 120)
    private String nome;

    @NotBlank @Size(max = 14)
    @Column(unique = true)
    private String cpf;

    @NotBlank @Email @Size(max = 120)
    private String email;

    @Size(max = 20)
    private String telefone;

    @NotNull
    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;

    @OneToMany(mappedBy = "paciente", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Consulta> consultas;
}
