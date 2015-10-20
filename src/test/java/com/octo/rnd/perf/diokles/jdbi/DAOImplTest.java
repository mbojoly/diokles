package com.octo.rnd.perf.diokles.jdbi;

/*
 * #%L
 * diokles
 * %%
 * Copyright (C) 2015 OCTO Technology
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


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
