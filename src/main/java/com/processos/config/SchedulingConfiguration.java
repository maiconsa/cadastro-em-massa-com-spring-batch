package com.processos.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@Import(BatchConfiguration.class)
public class SchedulingConfiguration {
	
	@Autowired
	private JobLauncher jobLauncher;
	

	@Autowired
	private ApplicationContext context;
	
	@Scheduled(fixedDelay = 10000)
	@DependsOn(value = {"jobLauncher","jobProcessaArquivosPendentes" })
	public void processaArquivosPendentes( ) throws JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException, JobParametersInvalidException {
		
        JobParameters jobParameters = 
                new JobParametersBuilder()
                .addLong("time",System.currentTimeMillis()).toJobParameters();
        
        Job jobProcessaArquivosPendentes = context.getBean("jobProcessaArquivosPendentes",Job.class);
		jobLauncher.run(jobProcessaArquivosPendentes, jobParameters);
	}
	
}
