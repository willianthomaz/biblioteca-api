package com.willianthomaz.bibliotecaapi.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Emprestimo {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100)
    private String cliente;

    @Column(name = "cliente_email")
    private String clienteEmail;

    @JoinColumn(name = "id_livro")
    @ManyToOne
    private Livro livro;

    @Column
    private LocalDate dataEmprestimo;

    @Column
    private Boolean devolvido;
}

