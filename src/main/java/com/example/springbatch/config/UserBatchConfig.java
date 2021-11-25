package com.example.springbatch.config;

import com.example.springbatch.model.User;
import com.example.springbatch.repository.UserRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration
public class UserBatchConfig {

    @Autowired
    private UserRepository userRepository;

    @Bean(name = "userLoadJob")
    public Job userLoadJob(JobBuilderFactory jobBuilderFactory,
                           @Qualifier("userLoadStep") Step userLoadStep) {
        return jobBuilderFactory.get("user-load-job")
                .incrementer(new RunIdIncrementer())
//                .flow(step)
//                .next(step)
                .start(userLoadStep)
                .build();
    }

    @Bean(name = "userLoadStep")
    public Step userLoadStep(StepBuilderFactory stepBuilderFactory,
                             @Qualifier("userItemReader") ItemReader userItemReader,
                             @Qualifier("userItemProcessor") ItemProcessor userItemProcessor,
                             @Qualifier("userItemWriter") ItemWriter userItemWriter) {
         return stepBuilderFactory.get("user-load-step")
                 .chunk(100)
                 .reader(userItemReader)
                 .processor(userItemProcessor)
                 .writer(userItemWriter)
                 .build();

    }

    @Bean("userItemReader")
    public FlatFileItemReader<User> userItemReader(@Value("${data.file.users}") Resource resource) {
        FlatFileItemReader<User> flatFileItemReader = new FlatFileItemReader<>();
        flatFileItemReader.setResource(resource);
        flatFileItemReader.setName("user-csv-reader");
        flatFileItemReader.setLinesToSkip(1);
        flatFileItemReader.setLineMapper(lineMapper());

        return flatFileItemReader;
    }

    private LineMapper<User> lineMapper() {
        DefaultLineMapper<User> defaultLineMapper = new DefaultLineMapper<>();
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();

        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames(new String[] {"firstName", "lastName", "email", "phone", "active"});

        BeanWrapperFieldSetMapper<User> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(User.class);

        defaultLineMapper.setLineTokenizer(lineTokenizer);
        defaultLineMapper.setFieldSetMapper(fieldSetMapper);
        return defaultLineMapper;
    }

    @Bean(name = "userItemProcessor")
    public ItemProcessor<User, User> userItemProcessor() {
        return user -> {
            user.setActive(Boolean.valueOf(user.getActive()));
            return user;
        };
    }

    @Bean(name = "userItemWriter")
    public ItemWriter<User>  userItemWriter() {
        return users -> {
            userRepository.saveAll(users);
        };
    }
}
