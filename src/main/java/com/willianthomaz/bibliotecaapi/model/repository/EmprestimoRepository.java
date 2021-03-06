package com.willianthomaz.bibliotecaapi.model.repository;

import com.willianthomaz.bibliotecaapi.model.entity.Emprestimo;
import com.willianthomaz.bibliotecaapi.model.entity.Livro;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface EmprestimoRepository extends JpaRepository<Emprestimo, Long> {

    @Query(value = " select case when ( count(l.id) > 0 ) then true else false end " +
            " from Emprestimo l where l.livro = :livro and ( l.devolvido is null or l.devolvido is false ) ")
    boolean existsByLivroAndNotDevolvido( @Param("livro") Livro livro );

    @Query( value = " select l from Emprestimo as l join l.livro as b where b.isbn = :isbn or l.cliente =:cliente ")
    Page<Emprestimo> findByLivroIsbnOrCliente(
            @Param("isbn") String isbn,
            @Param("cliente") String cliente,
            Pageable pageable
    );

    Page<Emprestimo> findByLivro( Livro livro, Pageable pageable );

    @Query(" select l from Emprestimo l where l.dataEmprestimo <= :haTresDias and ( l.devolvido is null or l.devolvido is false ) ")
    List<Emprestimo> findByEmprestimoDateLessThanAndNotDevolvido(@Param("haTresDias") LocalDate haTresDias );
}
