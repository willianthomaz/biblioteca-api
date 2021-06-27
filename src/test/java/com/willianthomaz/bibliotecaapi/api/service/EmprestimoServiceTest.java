package com.willianthomaz.bibliotecaapi.api.service;

import com.willianthomaz.bibliotecaapi.api.dto.EmprestimoFiltroDTO;
import com.willianthomaz.bibliotecaapi.exception.BusinessException;
import com.willianthomaz.bibliotecaapi.model.entity.Emprestimo;
import com.willianthomaz.bibliotecaapi.model.entity.Livro;
import com.willianthomaz.bibliotecaapi.model.repository.EmprestimoRepository;
import com.willianthomaz.bibliotecaapi.service.EmprestimoService;
import com.willianthomaz.bibliotecaapi.service.impl.EmprestimoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
 class EmprestimoServiceTest {

        EmprestimoService service;

        @MockBean
        EmprestimoRepository repository;

        @BeforeEach
        public void setUp(){
            this.service = new EmprestimoServiceImpl(repository);
        }

        @Test
        @DisplayName("Deve salvar um empréstimo")
         void saveEmprestimoTest(){
            Livro livro = Livro.builder().id(1l).build();
            String cliente = "Fulano";

            Emprestimo savingEmprestimo =
                    Emprestimo.builder()
                            .livro(livro)
                            .cliente(cliente)
                            .dataEmprestimo(LocalDate.now())
                            .build();

            Emprestimo savedEmprestimo = Emprestimo.builder()
                    .id(1l)
                    .dataEmprestimo(LocalDate.now())
                    .cliente(cliente)
                    .livro(livro).build();


            when( repository.existsByLivroAndNotDevolvido(livro) ).thenReturn(false);
            when( repository.save(savingEmprestimo) ).thenReturn( savedEmprestimo );

            Emprestimo emprestimo = service.save(savingEmprestimo);

            assertThat(emprestimo.getId()).isEqualTo(savedEmprestimo.getId());
            assertThat(emprestimo.getLivro().getId()).isEqualTo(savedEmprestimo.getLivro().getId());
            assertThat(emprestimo.getCliente()).isEqualTo(savedEmprestimo.getCliente());
            assertThat(emprestimo.getDataEmprestimo()).isEqualTo(savedEmprestimo.getDataEmprestimo());
        }

        @Test
        @DisplayName("Deve lançar erro de negócio ao salvar um empréstimo com livro já emprestado")
         void loanedLivroSaveTest(){
            Livro livro = Livro.builder().id(1l).build();
            String cliente = "Fulano";

            Emprestimo savingEmprestimo =
                    Emprestimo.builder()
                            .livro(livro)
                            .cliente(cliente)
                            .dataEmprestimo(LocalDate.now())
                            .build();

            when(repository.existsByLivroAndNotDevolvido(livro)).thenReturn(true);

            Throwable exception = catchThrowable(() -> service.save(savingEmprestimo));

            assertThat(exception)
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("Livro já emprestado");

            verify(repository, never()).save(savingEmprestimo);

        }

        @Test
        @DisplayName(" Deve obter as informações de um empréstimo pelo ID")
         void getEmprestimoDetaisTest(){
            //cenário
            Long id = 1l;

            Emprestimo emprestimo = createEmprestimo();
            emprestimo.setId(id);

            Mockito.when( repository.findById(id) ).thenReturn(Optional.of(emprestimo));

            //execucao
            Optional<Emprestimo> result = service.getById(id);

            //verificacao
            assertThat(result.isPresent()).isTrue();
            assertThat(result.get().getId()).isEqualTo(id);
            assertThat(result.get().getCliente()).isEqualTo(emprestimo.getCliente());
            assertThat(result.get().getLivro()).isEqualTo(emprestimo.getLivro());
            assertThat(result.get().getDataEmprestimo()).isEqualTo(emprestimo.getDataEmprestimo());

            verify( repository ).findById(id);

        }

        @Test
        @DisplayName("Deve atualizar um empréstimo.")
        void updateEmprestimoTest(){
            Emprestimo emprestimo = createEmprestimo();
            emprestimo.setId(1l);
            emprestimo.setDevolvido(true);

            when( repository.save(emprestimo) ).thenReturn( emprestimo );

            Emprestimo updatedEmprestimo = service.update(emprestimo);

            assertThat(updatedEmprestimo.getDevolvido()).isTrue();
            verify(repository).save(emprestimo);
        }

        @Test
        @DisplayName("Deve filtrar empréstimos pelas propriedades")
         void findEmprestimoTest(){
            //cenario
            EmprestimoFiltroDTO emprestimoFiltroDTO = EmprestimoFiltroDTO.builder().cliente("Fulano").isbn("321").build();

            Emprestimo emprestimo = createEmprestimo();
            emprestimo.setId(1l);
            PageRequest pageRequest = PageRequest.of(0, 10);
            List<Emprestimo> lista = Arrays.asList(emprestimo);

            Page<Emprestimo> page = new PageImpl<Emprestimo>(lista, pageRequest, lista.size());
            when( repository.findByLivroIsbnOrCliente(
                    Mockito.anyString(),
                    Mockito.anyString(),
                    Mockito.any(PageRequest.class))
            )
                    .thenReturn(page);

            //execucao
            Page<Emprestimo> result = service.find( emprestimoFiltroDTO, pageRequest );


            //verificacoes
            assertThat(result.getTotalElements()).isEqualTo(1);
            assertThat(result.getContent()).isEqualTo(lista);
            assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
            assertThat(result.getPageable().getPageSize()).isEqualTo(10);
        }

        public static Emprestimo createEmprestimo(){
            Livro livro = Livro.builder().id(1l).build();
            String cliente = "Fulano";

            return Emprestimo.builder()
                    .livro(livro)
                    .cliente(cliente)
                    .dataEmprestimo(LocalDate.now())
                    .build();
        }
    }

