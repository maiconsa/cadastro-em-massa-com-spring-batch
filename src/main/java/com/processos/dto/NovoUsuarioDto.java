package com.processos.dto;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

import org.springframework.util.ObjectUtils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class NovoUsuarioDto   {
	
	@NotEmpty(message = "O campo nome é obrigatório!!!")
	private String nome;
	
	@Email(message = "E-mail inválido!!!")
	@NotEmpty(message = "O campo email é obrigatório")
	private String email;
	
	@Builder.Default
	private List<String> validacoes;
	
	public NovoUsuarioDto() {
		this.validacoes = new ArrayList<>();
	}
	
	public void addValidacao(String validacao) {
		if(this.validacoes == null) {
			this.validacoes = new ArrayList<>();
		}
		this.validacoes.add(validacao);
	}
	
	public void addValidacoes(List<String> validacoes) {
		this.validacoes  = validacoes;
	}

	public List<String> getValidacoes() {
		return validacoes;
	}

	public void setValidacoes(List<String> validacoes) {
		this.validacoes = validacoes;
	}
	
	
	public boolean invalido() {
		return ObjectUtils.isEmpty(this.validacoes) == false;
	}
	
	
}
