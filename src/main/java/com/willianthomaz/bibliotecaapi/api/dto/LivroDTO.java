package com.willianthomaz.bibliotecaapi.api.dto;

import lombok.*;

import javax.validation.constraints.NotEmpty;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LivroDTO {

    private Long id;
    @NotEmpty
    private String titulo;
    @NotEmpty
    private String autor;
    @NotEmpty
    private String isbn;
}
