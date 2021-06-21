package com.willianthomaz.bibliotecaapi.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class EmprestimoDevolvidoDTO {
    private Boolean devolvido;
}
