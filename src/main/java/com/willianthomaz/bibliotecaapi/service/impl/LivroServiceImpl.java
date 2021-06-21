package com.willianthomaz.bibliotecaapi.service.impl;

import com.willianthomaz.bibliotecaapi.exception.BusinessException;
import com.willianthomaz.bibliotecaapi.model.entity.Livro;
import com.willianthomaz.bibliotecaapi.model.repository.LivroRepository;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LivroServiceImpl implements com.willianthomaz.bibliotecaapi.service.LivroService {

    private LivroRepository repository;

    public LivroServiceImpl(LivroRepository repository) {
        this.repository = repository;
    }

    @Override
    public Livro save(Livro livro) {
        if( repository.existsByIsbn(livro.getIsbn()) ){
            throw new BusinessException("Isbn já cadastrado.");
        }
        return repository.save(livro);
    }

    @Override
    public Optional<Livro> getById(Long id) {
        return this.repository.findById(id);
    }

    @Override
    public void delete(Livro livro) {
        if(livro == null || livro.getId() == null){
            throw new IllegalArgumentException("Book id cant be null.");
        }
        this.repository.delete(livro);
    }

    @Override
    public Livro update(Livro livro) {
        if(livro == null || livro.getId() == null){
            throw new IllegalArgumentException("Book id cant be null.");
        }
        return this.repository.save(livro);
    }

    @Override
    public Page<Livro> find(Livro filter, Pageable pageRequest ) {
        Example<Livro> example = Example.of(filter,
                ExampleMatcher
                        .matching()
                        .withIgnoreCase()
                        .withIgnoreNullValues()
                        .withStringMatcher( ExampleMatcher.StringMatcher.CONTAINING )
        ) ;
        return repository.findAll(example, pageRequest);
    }

    @Override
    public Optional<Livro> getBookByIsbn(String isbn) {
        return repository.findByIsbn(isbn);
    }

}
