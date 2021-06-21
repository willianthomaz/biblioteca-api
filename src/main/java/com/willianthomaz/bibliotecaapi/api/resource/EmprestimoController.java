package com.willianthomaz.bibliotecaapi.api.resource;

import com.willianthomaz.bibliotecaapi.api.dto.EmprestimoDTO;
import com.willianthomaz.bibliotecaapi.api.dto.EmprestimoDevolvidoDTO;
import com.willianthomaz.bibliotecaapi.api.dto.EmprestimoFiltroDTO;
import com.willianthomaz.bibliotecaapi.api.dto.LivroDTO;
import com.willianthomaz.bibliotecaapi.model.entity.Emprestimo;
import com.willianthomaz.bibliotecaapi.model.entity.Livro;
import com.willianthomaz.bibliotecaapi.service.EmprestimoService;
import com.willianthomaz.bibliotecaapi.service.LivroService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class EmprestimoController {

    private final EmprestimoService service;
    private final LivroService livroService;
    private final ModelMapper modelMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Long create(@RequestBody EmprestimoDTO dto) {
        Livro livro = livroService
                .getBookByIsbn(dto.getIsbn())
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.BAD_REQUEST, "Livro não encontrado para isbn aprovado"));
        Emprestimo entity = Emprestimo.builder()
                .livro(livro)
                .cliente(dto.getCliente())
                .dataEmprestimo(LocalDate.now())
                .build();

        entity = service.save(entity);
        return entity.getId();
    }

    @PatchMapping("{id}")
    public void returnBook(
            @PathVariable Long id,
            @RequestBody EmprestimoDevolvidoDTO dto) {
        Emprestimo emprestimo = service.getById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        emprestimo.setDevolvido(dto.getDevolvido());
        service.update(emprestimo);
    }

    @GetMapping
    public Page<EmprestimoDTO> find(EmprestimoFiltroDTO dto, Pageable pageRequest) {
        Page<Emprestimo> result = service.find(dto, pageRequest);
        List<EmprestimoDTO> loans = result
                .getContent()
                .stream()
                .map(entity -> {

                    Livro livro = entity.getLivro();
                    LivroDTO livroDTO = modelMapper.map(livro, LivroDTO.class);
                    EmprestimoDTO emprestimoDTO = modelMapper.map(entity, EmprestimoDTO.class);
                    emprestimoDTO.setLivro(livroDTO);
                    return emprestimoDTO;

                }).collect(Collectors.toList());
        return new PageImpl<EmprestimoDTO>(loans, pageRequest, result.getTotalElements());
    }
}
