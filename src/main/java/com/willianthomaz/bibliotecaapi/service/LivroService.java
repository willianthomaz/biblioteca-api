package com.willianthomaz.bibliotecaapi.service;

import com.willianthomaz.bibliotecaapi.model.entity.Livro;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface LivroService {

    Livro save(Livro any);

    Optional<Livro> getById(Long id);

    void delete(Livro livro);

    Livro update(Livro livro);

    Page<Livro> find(Livro filter, Pageable pageRequest );

    Optional<Livro> getBookByIsbn(String isbn);
}

