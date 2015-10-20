package com.octo.rnd.perf.diokles;

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


import org.easymock.EasyMock;
import org.h2.tools.Server;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.sql.SQLException;

import static org.easymock.EasyMock.createMock;
import static org.powermock.api.easymock.PowerMock.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest( { Server.class })
public class ApplicationTest {

    @Test
    public void testInternalServerStartWithDefaultConf() throws SQLException {
        mockStatic(Server.class);
        Server h2ServerMock = createMock(Server.class);
        EasyMock.expect(
                Server.createTcpServer("-tcpPort", Short.toString(Application.H2_DEFAULT_TCP_PORT))
        ).andReturn(h2ServerMock);
        EasyMock.expect(h2ServerMock.start()).andReturn(h2ServerMock);

        Application app = new Application();
        Configuration conf = new Configuration();
        conf.setDbHost(Application.DEFAULT_HOST);


        EasyMock.replay(h2ServerMock);
        PowerMock.replay(Server.class);

        app.startH2IfNeeded(conf);

        PowerMock.verify(Server.class);
        EasyMock.verify(h2ServerMock);

    }
}

