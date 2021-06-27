package com.willianthomaz.bibliotecaapi.service;

import com.willianthomaz.bibliotecaapi.exception.BusinessException;
import com.willianthomaz.bibliotecaapi.model.entity.Livro;
import com.willianthomaz.bibliotecaapi.model.repository.LivroRepository;
import com.willianthomaz.bibliotecaapi.service.LivroService;
import com.willianthomaz.bibliotecaapi.service.impl.LivroServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
class LivroServiceTest {


    LivroService service;

    @MockBean
    LivroRepository repository;

    @BeforeEach
    public void setUp() {
        this.service = new LivroServiceImpl(repository);
    }

    @Test
    @DisplayName("Deve salvar um livro")
    void saveLivroTest() {
        //cenario
        Livro livro = createValidLivro();
        when(repository.existsByIsbn(Mockito.anyString())).thenReturn(false);

        when(repository.save(livro)).thenReturn(
                Livro.builder().id(1l)
                        .isbn("123")
                        .autor("Fulano")
                        .titulo("As aventuras").build()
        );

        //execucao
        Livro savedLivro = service.save(livro);

        //verificacao
        assertThat(savedLivro.getId()).isNotNull();
        assertThat(savedLivro.getIsbn()).isEqualTo("123");
        assertThat(savedLivro.getTitulo()).isEqualTo("As aventuras");
        assertThat(savedLivro.getAutor()).isEqualTo("Fulano");
    }

    private Livro createValidLivro() {
        return Livro.builder()
                .isbn("123")
                .autor("Fulano")
                .titulo("As aventuras")
                .build();
    }

    @Test
    @DisplayName("Deve lançar erro de negocio ao tentar salvar um livro com isbn duplicado")
    void shouldNotSaveALivroWithDuplicatedISBN() {
        //cenario
        Livro livro = createValidLivro();
        when(repository.existsByIsbn(Mockito.anyString())).thenReturn(true);

        //execucao
        Throwable exception = Assertions.catchThrowable(() -> service.save(livro));

        //verificacoes
        assertThat(exception)
                .isInstanceOf(BusinessException.class)
                .hasMessage("Isbn já cadastrado.");

        Mockito.verify(repository, Mockito.never()).save(livro);

    }

    @Test
    @DisplayName("Deve obter um livro por Id")
    void getByIdTest() {
        Long id = 1l;
        Livro livro = createValidLivro();
        livro.setId(id);

        when(repository.findById(id)).thenReturn(Optional.of(livro));

        //execucao
        Optional<Livro> foundLivro = service.getById(id);

        //verificacoes
        assertThat(foundLivro.isPresent()).isTrue();
        assertThat(foundLivro.get().getId()).isEqualTo(id);
        assertThat(foundLivro.get().getAutor()).isEqualTo(livro.getAutor());
        assertThat(foundLivro.get().getIsbn()).isEqualTo(livro.getIsbn());
        assertThat(foundLivro.get().getTitulo()).isEqualTo(livro.getTitulo());
    }

    @Test
    @DisplayName("Deve retornar vazio ao obter um livro por Id quando ele não existe na base.")
    void livroNotFoundByIdTest() {
        Long id = 1l;
        when(repository.findById(id)).thenReturn(Optional.empty());

        //execucao
        Optional<Livro> livro = service.getById(id);

        //verificacoes
        assertThat(livro.isPresent()).isFalse();

    }

    @Test
    @DisplayName("Deve deletar um livro.")
    void deleteLivroTest() {
        Livro livro = Livro.builder().id(1l).build();

        //execucao
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> service.delete(livro));

        //verificacoes
        Mockito.verify(repository, Mockito.times(1)).delete(livro);
    }

    @Test
    @DisplayName("Deve ocorrer erro ao tentar deletar um livro inexistente.")
    void deleteInvalidLivroTest() {
        Livro livro = new Livro();

        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> service.delete(livro));

        Mockito.verify(repository, Mockito.never()).delete(livro);
    }

    @Test
    @DisplayName("Deve ocorrer erro ao tentar atualizar um livro inexistente.")
    void updateInvalidLivroTest() {
        Livro livro = new Livro();

        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> service.update(livro));

        Mockito.verify(repository, Mockito.never()).save(livro);
    }

    @Test
    @DisplayName("Deve atualizar um livro.")
    void updateLivroTest() {
        //cenário
        long id = 1l;

        //livro a atualizar
        Livro updatingLivro = Livro.builder().id(id).build();

        //simulacao
        Livro updatedLivro = createValidLivro();
        updatedLivro.setId(id);
        when(repository.save(updatingLivro)).thenReturn(updatedLivro);

        //exeucao
        Livro livro = service.update(updatingLivro);

        //verificacoes
        assertThat(livro.getId()).isEqualTo(updatedLivro.getId());
        assertThat(livro.getTitulo()).isEqualTo(updatedLivro.getTitulo());
        assertThat(livro.getIsbn()).isEqualTo(updatedLivro.getIsbn());
        assertThat(livro.getAutor()).isEqualTo(updatedLivro.getAutor());

    }

    @Test
    @DisplayName("Deve filtrar livros pelas propriedades")
    void findLivroTest() {
        //cenario
        Livro livro = createValidLivro();

        PageRequest pageRequest = PageRequest.of(0, 10);

        List<Livro> lista = Arrays.asList(livro);
        Page<Livro> page = new PageImpl<Livro>(lista, pageRequest, 1);
        when(repository.findAll(Mockito.any(Example.class), Mockito.any(PageRequest.class)))
                .thenReturn(page);

        //execucao
        Page<Livro> result = service.find(livro, pageRequest);


        //verificacoes
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).isEqualTo(lista);
        assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(result.getPageable().getPageSize()).isEqualTo(10);
    }

    @Test
    @DisplayName("deve obter um livro pelo isbn")
    void getLivroByIsbnTest() {
        String isbn = "1230";
        when(repository.findByIsbn(isbn)).thenReturn(Optional.of(Livro.builder().id(1l).isbn(isbn).build()));

        Optional<Livro> book = service.getLivroByIsbn(isbn);

        assertThat(book.isPresent()).isTrue();
        assertThat(book.get().getId()).isEqualTo(1l);
        assertThat(book.get().getIsbn()).isEqualTo(isbn);

        verify(repository, times(1)).findByIsbn(isbn);
    }
}


