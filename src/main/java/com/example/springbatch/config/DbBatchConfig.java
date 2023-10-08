package com.example.springbatch.config;

import com.example.springbatch.model.User;
import com.example.springbatch.util.UserItemPreparedStatementSetter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import javax.sql.DataSource;

@Configuration
public class DbBatchConfig {

    @Autowired
    @Qualifier("sourceDataSource")
    private DataSource sourceDataSource;

    @Autowired
    @Qualifier("targetDataSource")
    private DataSource targetDataSource;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Bean
    public JdbcCursorItemReader<User> itemReader() {
        JdbcCursorItemReader<User> reader = new JdbcCursorItemReader<>();
        reader.setDataSource(sourceDataSource);
        reader.setSql("SELECT * FROM user");
        reader.setRowMapper(new BeanPropertyRowMapper<>(User.class));
        return reader;
    }

    @Bean
    public JdbcBatchItemWriter<User> itemWriter() {
        JdbcBatchItemWriter<User> writer = new JdbcBatchItemWriter<>();
        writer.setDataSource(targetDataSource);
        writer.setSql("INSERT INTO user (first_name, last_name, email, phone, active) VALUES (?, ?, ?, ?, ?)");
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
        writer.setItemPreparedStatementSetter(new UserItemPreparedStatementSetter());
        return writer;
    }

    @Bean
    public ItemProcessor<User, User> itemProcessor() {
        return sourceEntity -> {
            // Transform sourceEntity to targetEntity if needed
            return sourceEntity;
        };
    }

    @Bean
    public Step dataTransferStep() {
        return stepBuilderFactory.get("userTransferStep")
                .<User, User>chunk(100)
                .reader(itemReader())
                .processor(itemProcessor())
                .writer(itemWriter())
                .build();
    }

    @Bean("userTransferJob")
    public Job dataTransferJob() {
        return jobBuilderFactory.get("userTransferJob")
                .incrementer(new RunIdIncrementer())
                .start(dataTransferStep())
                .build();
    }
}
