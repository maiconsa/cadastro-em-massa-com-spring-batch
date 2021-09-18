package com.processos.step;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import com.processos.NovoUsuarioDtoFieldSetMapper;
import com.processos.config.LocationProperties;
import com.processos.dto.NovoUsuarioDto;
import com.processos.repository.UsuarioRepository;
import com.processos.utils.ResourceUtils;

@Component
@Scope("prototype")
public class ValidaArquivoTasklet implements Tasklet {
					
	@Autowired
	private LocationProperties locationProperties;
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	private Resource[] resources;
	
	private int current_index = 0;
	
	
	@PostConstruct
	public void postConstructor() {
		this.current_index = 0 ;
		this.resources = ResourceUtils.loadFrom(locationProperties.getBase().resolve("pendentes").toFile());
				
	}
	
	@Override
	public RepeatStatus execute(StepContribution step, ChunkContext chunckContext) throws Exception {
		
		if(this.resources.length == 0 ) return RepeatStatus.FINISHED;
		
		List<NovoUsuarioDto> listaUsuarios = ResourceUtils.read(getCurrentResource(), new NovoUsuarioDtoFieldSetMapper(), new String[]{"nome","email"}, 0);
				
		boolean invalid = listaUsuarios.parallelStream().map(usuario -> validaUsuario(usuario)).filter(usuario -> usuario.invalido()).findFirst().isPresent();
		System.out.println(listaUsuarios.toString());

		if(!invalid) {
			File target = locationProperties.getBase().resolve("validados").resolve(getCurrentResourceFilename()).toFile();		
			  if(!target.getParentFile().exists()) target.getParentFile().mkdir();
			  if(!target.exists()) target.createNewFile();
			  Files.move(getCurrentResource().getFile().toPath(), target.toPath(),StandardCopyOption.REPLACE_EXISTING);
			 
		}else {
			Resource resource = getInvalidaResource(getCurrentResourceFilename());
			ResourceUtils.<NovoUsuarioDto>write(resource, listaUsuarios, new String[] {"nome","email","validacoes"});	
			getCurrentResource().getFile().delete();
		}
		
	
		
		return ++current_index < resources.length  ?  RepeatStatus.CONTINUABLE :RepeatStatus.FINISHED;
	}

	private NovoUsuarioDto validaUsuario(NovoUsuarioDto usuario) {
		Set<ConstraintViolation<NovoUsuarioDto>> validations =  Validation.buildDefaultValidatorFactory().getValidator().validate(usuario);
		 if(!validations.isEmpty()) {
			 usuario.addValidacoes(validations.stream().map(validacao -> validacao.getMessage()).collect(Collectors.toList()));
		 }
		 if(this.usuarioRepository.existsByEmail(usuario.getEmail())) {
			 usuario.addValidacao("E-mail já registrado");
		 }
		 return usuario;
	}
	
	private Resource getCurrentResource() {
		return this.resources[this.current_index];
	}
	
	private String getCurrentResourceFilename() {
		return this.getCurrentResource().getFilename();
	}
	
	private Resource getInvalidaResource( String filename) {
		File file = this.locationProperties.getBase().resolve("invalidos").resolve(filename).toFile();
		file.setWritable(true);
		return new FileSystemResource(file);
	}
	

}
