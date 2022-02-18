package com.demo.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class JobsMapperTest {

    private JobsMapper jobsMapper;

    @BeforeEach
    public void setUp() {
        jobsMapper = new JobsMapperImpl();
    }
}
