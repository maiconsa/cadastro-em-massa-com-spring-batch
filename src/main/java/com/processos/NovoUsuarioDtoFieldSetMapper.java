package com.processos;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

import com.processos.dto.NovoUsuarioDto;

public class NovoUsuarioDtoFieldSetMapper implements FieldSetMapper<NovoUsuarioDto> {
	@Override
	public NovoUsuarioDto mapFieldSet(FieldSet fieldSet) throws BindException {
		return NovoUsuarioDto.builder()
				.email(fieldSet.readString("email"))
				.nome(fieldSet.readString("nome"))
				.build();
	}
}
