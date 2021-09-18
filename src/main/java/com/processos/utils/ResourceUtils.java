package com.processos.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.batch.item.file.DefaultBufferedReaderFactory;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.IncorrectTokenCountException;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

public class ResourceUtils {
	private ResourceUtils() {

	}

	public static Resource[] loadFrom(Path path) {
		return ResourceUtils.loadFrom(path.toFile());
	}
 
	public static Resource[] loadFrom(File file) {
		File files[] = file.listFiles();
		if(files == null) {
			files = new File[] {};
		}
		List<File> arquivoPendentes = Arrays.asList(files);
		List<Resource> fileResources = StreamSupport.stream(arquivoPendentes.spliterator(), false)
				.map(FileSystemResource::new).collect(Collectors.toList());

		return fileResources.toArray(new Resource[fileResources.size()]);

	}

	public static  <T> List<T> read(Resource from,FieldSetMapper<T> fsm,String names[], int skipLine) throws Exception {
		DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
		lineTokenizer.setNames(names);
		
		DefaultLineMapper<T>  lineMapper = new DefaultLineMapper<T>();
		lineMapper.setFieldSetMapper(fsm);
		lineMapper.setLineTokenizer(lineTokenizer);
		
		
		DefaultBufferedReaderFactory reader = new DefaultBufferedReaderFactory();
		BufferedReader bufferedReader = reader.create(from, Charset.defaultCharset().name());
		
		List<T> list = new ArrayList<>();
		String line = 	bufferedReader.readLine();
		int count = 0;
		while(line != null) {

				if((skipLine == count) == false) list.add(lineMapper.mapLine(line, count));		
				line = 	bufferedReader.readLine();
				count++;
		}
		bufferedReader.close();
		return list;
	}

	public static <T> void write(Resource target, List<T> items, String[] extractByName) throws IOException {
		BeanWrapperFieldExtractor<T> fieldExtractor = new BeanWrapperFieldExtractor<>();
		fieldExtractor.setNames(extractByName);
		DelimitedLineAggregator<T> lineAggrator = new DelimitedLineAggregator<>();
		lineAggrator.setFieldExtractor(fieldExtractor);

		StringBuilder lines = new StringBuilder();
		for (T item : items) {
			lines.append(lineAggrator.aggregate(item)).append("\n");
		}

		if(!target.getFile().getParentFile().exists()) target.getFile().getParentFile().mkdir();
		Files.write(target.getFile().toPath(), lines.toString().getBytes());

	}
}
