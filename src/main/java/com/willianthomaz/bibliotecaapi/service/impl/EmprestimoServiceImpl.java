package com.willianthomaz.bibliotecaapi.service.impl;

import com.willianthomaz.bibliotecaapi.api.dto.EmprestimoFiltroDTO;
import com.willianthomaz.bibliotecaapi.exception.BusinessException;
import com.willianthomaz.bibliotecaapi.model.entity.Emprestimo;
import com.willianthomaz.bibliotecaapi.model.entity.Livro;
import com.willianthomaz.bibliotecaapi.model.repository.EmprestimoRepository;
import com.willianthomaz.bibliotecaapi.service.EmprestimoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmprestimoServiceImpl implements EmprestimoService {


    private EmprestimoRepository repository;


    public EmprestimoServiceImpl(EmprestimoRepository repository) {
        this.repository = repository;
    }

    @Override
    public Emprestimo save(Emprestimo emprestimo ) {
       if( repository.existsByLivroAndNotDevolvido(emprestimo.getLivro()) ){
            throw new BusinessException("Book already loaned");
        }
        return repository.save(emprestimo);
    }

    @Override
    public Optional<Emprestimo> getById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Emprestimo update(Emprestimo emprestimo) {
        return repository.save(emprestimo);
    }

    @Override
    public Page<Emprestimo> find(EmprestimoFiltroDTO filterDTO, Pageable pageable) {
        return repository.findByLivroIsbnOrCliente( filterDTO.getIsbn(), filterDTO.getCliente(), pageable );
    }

    @Override
    public Page<Emprestimo> getLoansByBook(Livro livro, Pageable pageable) {
        return repository.findByLivro(livro, pageable);
    }

    @Override
    public List<Emprestimo> getAllLateLoans() {
        final Integer loanDays = 4;
        LocalDate threeDaysAgo = LocalDate.now().minusDays(loanDays);
        return repository.findByEmprestimoDateLessThanAndNotDevolvido(threeDaysAgo);
    }
}
