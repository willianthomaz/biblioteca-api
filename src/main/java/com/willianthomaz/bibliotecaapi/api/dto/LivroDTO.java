package com.willianthomaz.bibliotecaapi.api.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LivroDTO {

    private Long id;
    private String titulo;
    private String autor;
    private String isbn;
}
