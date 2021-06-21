package com.willianthomaz.bibliotecaapi.service;

import com.willianthomaz.bibliotecaapi.api.dto.EmprestimoFiltroDTO;
import com.willianthomaz.bibliotecaapi.model.entity.Emprestimo;
import com.willianthomaz.bibliotecaapi.model.entity.Livro;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface EmprestimoService {

    Emprestimo save(Emprestimo emprestimo );

    Optional<Emprestimo> getById(Long id);

    Emprestimo update(Emprestimo emprestimo);

    Page<Emprestimo> find(EmprestimoFiltroDTO filterDTO, Pageable pageable);

    Page<Emprestimo> getLoansByBook(Livro livro, Pageable pageable);

    List<Emprestimo> getAllLateLoans();

}
