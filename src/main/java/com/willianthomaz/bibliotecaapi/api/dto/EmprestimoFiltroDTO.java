package com.willianthomaz.bibliotecaapi.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmprestimoFiltroDTO {

    private String isbn;
    private String cliente;
}
