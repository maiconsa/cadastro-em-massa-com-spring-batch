package com.processos.config;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;

import javax.persistence.EntityManager;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Scope;

import com.processos.NovoUsuarioDtoFieldSetMapper;
import com.processos.dto.NovoUsuarioDto;
import com.processos.entity.UsuarioEntity;
import com.processos.step.ValidaArquivoTasklet;
import com.processos.utils.ResourceUtils;

@Configuration
@ConditionalOnBean(value = LocationProperties.class)
public class BatchConfiguration {

	@Autowired
	private StepBuilderFactory stepBuilderFactory;
		
	@Autowired
	private JobBuilderFactory jobBuilderFactory;
						
	@Bean
	@Scope("prototype")
	public Step validaArquivosPendentes(ValidaArquivoTasklet validaArquivoTasklet) {
		return  stepBuilderFactory.get("validaArquivosPendetes")
				.tasklet(validaArquivoTasklet)
				.build();
	}
		
	@Bean
	@Scope("prototype")
	public Step novosUsuarios(LocationProperties locationProperties , EntityManager entityManager ) throws Exception {
		Path pathDirArquivosValidados =  locationProperties.getBase().resolve("validados");
			return stepBuilderFactory.get("processaNovoUsuarios")
					.<NovoUsuarioDto,UsuarioEntity>chunk(2)
					.reader(multiResourceItemReader(pathDirArquivosValidados))
					.processor(compositeItemProcessor())
					.writer(jpaWriter(entityManager))
					.build()
					;
	}
	

	public MultiResourceItemReader<NovoUsuarioDto> multiResourceItemReader(Path path) throws IOException{
		MultiResourceItemReader<NovoUsuarioDto> multiItemReader = new MultiResourceItemReader<>();

		multiItemReader.setResources(ResourceUtils.loadFrom(path));
		multiItemReader.setDelegate(this.reader());
			
		return multiItemReader;
	}
	
	public FlatFileItemReader<NovoUsuarioDto> reader(){
		FlatFileItemReader<NovoUsuarioDto> itemReader  =  new FlatFileItemReader<NovoUsuarioDto>();
		itemReader.setLinesToSkip(1);
		
		DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
		lineTokenizer.setNames("nome","email");
		
		DefaultLineMapper<NovoUsuarioDto>  lineMapper = new DefaultLineMapper<NovoUsuarioDto>();
		lineMapper.setFieldSetMapper(new NovoUsuarioDtoFieldSetMapper());
		lineMapper.setLineTokenizer(lineTokenizer);

		itemReader.setLineMapper(lineMapper);
		return itemReader;
	}
	
	public ItemProcessor<NovoUsuarioDto, UsuarioEntity> compositeItemProcessor() throws Exception{
		CompositeItemProcessor<NovoUsuarioDto, UsuarioEntity> composite = new CompositeItemProcessor<>();
		
		ItemProcessor<NovoUsuarioDto, UsuarioEntity> converterItemProcessor =   (NovoUsuarioDto novoUsuario) ->{
				return new UsuarioEntity(null,novoUsuario.getNome(), novoUsuario.getEmail());
			};
		
		composite.setDelegates(Arrays.asList( converterItemProcessor));
		
		return composite;
	}
	
	
	
	public ItemWriter<UsuarioEntity> jpaWriter(EntityManager entityManager){
		JpaItemWriter<UsuarioEntity> jpaItemWrite = new JpaItemWriter<>();
		jpaItemWrite.setEntityManagerFactory(entityManager.getEntityManagerFactory());
		return jpaItemWrite;
		
	}		
	@Bean(name = "jobProcessaArquivosPendentes")
	@Scope("prototype")
	public Job jobProcessaArquivosPendentes(@Autowired Step novosUsuarios ,@Autowired Step validaArquivosPendentes) throws Exception {
		return jobBuilderFactory.get("jobProcessaUsuariosPendente")
				.incrementer(new RunIdIncrementer())
				.preventRestart()
				.start(validaArquivosPendentes)
				.next(novosUsuarios)
				.listener(new JobExecutionListener() {
					
					@Override
					public void beforeJob(JobExecution jobExecution) {
						System.out.println("Iniciando execução Job");
					}
					
					@Override
					public void afterJob(JobExecution jobExecution) {
						System.out.println("Encerrando execução Job");
					}
				})
				
				.build();
	}
	
	
}
