package com.processos.entity;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UsuarioEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private UUID codigo;
	
	@Column
	private String nome;
	
	@Column
	private String email;	
}
