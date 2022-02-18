package com.demo.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.demo.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class JobsDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(JobsDTO.class);
        JobsDTO jobsDTO1 = new JobsDTO();
        jobsDTO1.setId(1L);
        JobsDTO jobsDTO2 = new JobsDTO();
        assertThat(jobsDTO1).isNotEqualTo(jobsDTO2);
        jobsDTO2.setId(jobsDTO1.getId());
        assertThat(jobsDTO1).isEqualTo(jobsDTO2);
        jobsDTO2.setId(2L);
        assertThat(jobsDTO1).isNotEqualTo(jobsDTO2);
        jobsDTO1.setId(null);
        assertThat(jobsDTO1).isNotEqualTo(jobsDTO2);
    }
}
