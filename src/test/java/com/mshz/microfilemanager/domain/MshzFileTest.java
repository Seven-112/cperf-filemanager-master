package com.mshz.microfilemanager.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import com.mshz.microfilemanager.web.rest.TestUtil;

public class MshzFileTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(MshzFile.class);
        MshzFile mshzFile1 = new MshzFile();
        mshzFile1.setId(1L);
        MshzFile mshzFile2 = new MshzFile();
        mshzFile2.setId(mshzFile1.getId());
        assertThat(mshzFile1).isEqualTo(mshzFile2);
        mshzFile2.setId(2L);
        assertThat(mshzFile1).isNotEqualTo(mshzFile2);
        mshzFile1.setId(null);
        assertThat(mshzFile1).isNotEqualTo(mshzFile2);
    }
}
