package br.com.sildu.ponto.api.dtos;

import lombok.EqualsAndHashCode;

import lombok.Data;

@Data
@EqualsAndHashCode
public class EmpresaDto {
	private Long id;
	private String razaoSocial;
	private String cnpj;
}
