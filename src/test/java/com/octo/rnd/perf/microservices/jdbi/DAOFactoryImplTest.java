package com.octo.rnd.perf.microservices.jdbi;

import com.octo.rnd.perf.microservices.Application;
import junit.framework.Assert;
import org.hamcrest.core.StringStartsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Test;

/**
 * Created by mbojoly on 04/10/2015.
 */
public class DAOFactoryImplTest {

    @Test
    public void testBuildH2UrlDbHostIsInternal(){
        String url = DAOFactoryImpl.buildH2Url("INTERNAL", (short) 0);
        assertThat(url, StringStartsWith.startsWith("jdbc:h2:tcp://localhost:0/~/perfms-"));
    }
}
