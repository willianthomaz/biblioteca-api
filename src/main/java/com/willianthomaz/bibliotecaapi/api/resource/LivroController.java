package com.willianthomaz.bibliotecaapi.api.resource;

import com.willianthomaz.bibliotecaapi.api.dto.EmprestimoDTO;
import com.willianthomaz.bibliotecaapi.api.dto.LivroDTO;
import com.willianthomaz.bibliotecaapi.model.entity.Emprestimo;
import com.willianthomaz.bibliotecaapi.model.entity.Livro;
import com.willianthomaz.bibliotecaapi.service.LivroService;
import com.willianthomaz.bibliotecaapi.service.EmprestimoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/livros")
@RequiredArgsConstructor
@Api("Livro API")
@Slf4j
public class LivroController {

    private final LivroService service;
    private final ModelMapper modelMapper;
    private final EmprestimoService emprestimoService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation("Cria um livro")
    public LivroDTO create( @RequestBody @Valid LivroDTO dto ){
        log.info(" criando um livro para isbn: {} ", dto.getIsbn());
        Livro entity = modelMapper.map( dto, Livro.class );
        entity = service.save(entity);
        return modelMapper.map(entity, LivroDTO.class);
    }

    @GetMapping("{id}")
    @ApiOperation("Obtenha os detalhes de um livro por id")
    public LivroDTO get( @PathVariable Long id ){
        log.info(" obtendo detalhes para id do livro: {} ", id);
        return service
                .getById(id)
                .map( livro -> modelMapper.map(livro, LivroDTO.class)  )
                .orElseThrow( () -> new ResponseStatusException(HttpStatus.NOT_FOUND) );
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiOperation("Exclui um livro por id")
    public void delete(@PathVariable Long id){
        log.info(" deleting book of id: {} ", id);
        Livro livro = service.getById(id).orElseThrow( () -> new ResponseStatusException(HttpStatus.NOT_FOUND) );
        service.delete(livro);
    }

    @PutMapping("{id}")
    @ApiOperation("Atualiza um livro")
    public LivroDTO update( @PathVariable Long id, @RequestBody @Valid LivroDTO dto){
        log.info(" atualizando livro de id: {} ", id);
        return service.getById(id).map( livro -> {

            livro.setAutor(dto.getAutor());
            livro.setTitulo(dto.getTitulo());
            livro = service.update(livro);
            return modelMapper.map(livro, LivroDTO.class);

        }).orElseThrow( () -> new ResponseStatusException(HttpStatus.NOT_FOUND) );
    }

    @GetMapping
    @ApiOperation("Lista livros por parametro")
    public Page<LivroDTO> find(LivroDTO dto, Pageable pageRequest ){
        Livro filter = modelMapper.map(dto, Livro.class);
        Page<Livro> result = service.find(filter, pageRequest);
        List<LivroDTO> list = result.getContent()
                .stream()
                .map(entity -> modelMapper.map(entity, LivroDTO.class))
                .collect(Collectors.toList());

        return new PageImpl<LivroDTO>( list, pageRequest, result.getTotalElements() );
    }

    @GetMapping("{id}/emprestimos")
    public Page<EmprestimoDTO> loansByBook(@PathVariable Long id, Pageable pageable ){
        Livro livro = service.getById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        Page<Emprestimo> result = emprestimoService.getLoansByBook(livro, pageable);
        List<EmprestimoDTO> list = result.getContent()
                .stream()
                .map(emprestimo -> {
                    Livro emprestimoLivro = emprestimo.getLivro();
                    LivroDTO livroDTO = modelMapper.map(emprestimoLivro, LivroDTO.class);
                    EmprestimoDTO emprestimoDTO = modelMapper.map(emprestimo, EmprestimoDTO.class);
                    emprestimoDTO.setLivro(livroDTO);
                    return emprestimoDTO;
                }).collect(Collectors.toList());
        return new PageImpl<EmprestimoDTO>(list, pageable, result.getTotalElements());
    }

}
