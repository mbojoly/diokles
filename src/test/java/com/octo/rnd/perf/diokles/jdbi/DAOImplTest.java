package com.octo.rnd.perf.diokles.jdbi;

import org.hamcrest.core.StringEndsWith;
import org.hamcrest.core.StringStartsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Test;

/**
 * Created by mbojoly on 04/10/2015.
 */
public class DAOImplTest {

    @Test
    public void testBuildH2UrlDbHostIsInternal(){
        String url = DAOImpl.buildH2Url("INTERNAL", (short) 0, (short) 2);
        assertThat(url, StringStartsWith.startsWith("jdbc:h2:tcp://localhost:0/~/perfms-"));
        assertThat(url, StringEndsWith.endsWith(";TRACE_LEVEL_SYSTEM_OUT=2"));
    }
}
