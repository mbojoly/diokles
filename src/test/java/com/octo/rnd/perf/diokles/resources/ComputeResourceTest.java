package com.octo.rnd.perf.diokles.resources;

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


import com.octo.rnd.perf.diokles.Configuration;
import com.octo.rnd.perf.diokles.jdbi.DAO;
import com.octo.rnd.perf.diokles.jdbi.DAOImpl;
import com.octo.rnd.perf.diokles.jdbi.JDBIException;
import org.easymock.EasyMock;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.exceptions.UnableToExecuteStatementException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;


public class ComputeResourceTest {

    public static final String NOTHING_IT_IS_JUSTE_A_MOCK = "Nothing it is juste a mock";
    final static Logger logger = LoggerFactory.getLogger(ComputeResourceTest.class);
    private static DBI dbi;
    private static Handle h;

    /**
     * See https://github.com/jdbi/jdbi/blob/master/src/test/java/org/skife/jdbi/v2/TestCallable.java
     * but the correct syntax is now here http://www.h2database.com/h2.pdf
     *
     * @throws Exception
     */
    @BeforeClass
    public static void doSetUp() throws Exception {
        dbi = new DBI("jdbc:h2:mem:" + UUID.randomUUID());
        h = dbi.open();
        try {
            h.execute("drop alias sleep");
        } catch (Exception e) {
            // okay if not present
        }

        h.execute("CREATE ALIAS SLEEP " +
                "FOR \"java.lang.Thread.sleep\"");
    }

    @AfterClass
    public static void doTearDown() throws Exception {
        try {
            h.execute("drop alias sleep");
        } catch (UnableToExecuteStatementException e) {
            if (e.getMessage() != null && e.getMessage().contains("Function alias \"SLEEP\" not found")) {
                logger.trace("Alias sleep has not been created");
            } else {
                throw e;
            }
        }

        if (h != null) h.close();
    }


    @Test
    public void testTimeForCallDatabaseOnce() {
        DAO dao = new DAOImpl(dbi);
        final long inputTime = 100;

        ComputeResource cr = new ComputeResource(dao, null, null);


        final int nbOfCalls = 1;
        Helper.MeasuredTime mt = Helper.measureTime(
                l -> {
                    try {
                        cr.callDatabase(nbOfCalls, inputTime);
                    } catch (JDBIException e) {
                        e.printStackTrace();
                    }
                    return Optional.empty();
                },
                inputTime,
                1);
        assertThat(mt.PercentError).isLessThan(0.3);
    }


    //@Test
    public void testCallDatabaseTwice() throws JDBIException {

        final long inputTime = 100;
        DAO daoMock = createMock(DAO.class);
        daoMock.callStoredProcedure(inputTime);
        daoMock.callStoredProcedure(inputTime);

        ComputeResource cr = new ComputeResource(daoMock, null, null);

        replay(daoMock);

        cr.callDatabase(2, inputTime);
    }

    @Test
    public void testTimeForCallDatabaseTwice() {
        DAO dao = new DAOImpl(dbi);

        ComputeResource cr = new ComputeResource(dao, null, null);

        final long inputTime = 100;
        final int nbOfCalls = 1;
        Helper.MeasuredTime mt = Helper.measureTime(
                l -> {
                    try {
                        cr.callDatabase(nbOfCalls, inputTime);
                    } catch (JDBIException e) {
                        e.printStackTrace();
                    }
                    return Optional.empty();
                },
                inputTime,
                nbOfCalls);
        assertThat(mt.PercentError).isLessThan(1.5); //0.3 is accessible locally but not on travis
    }

    @Test
    public void testTimeForCpuIntensiveCompute() {
        ComputeResource cr = new ComputeResource(null, null, null);
        final long inputTime = 100;
        //Warm-up
        Helper.measureTime(l -> Optional.of(cr.cpuIntensiveCompute(l)), inputTime, 1);
        Helper.MeasuredTime mt = Helper.measureTime(l -> Optional.of(cr.cpuIntensiveCompute(l)), inputTime, 1);
        if (mt.InnerTimeMillis.isPresent()) assertThat(mt.InnerTimeMillis.get()).isLessThanOrEqualTo(inputTime);
        assertThat(mt.PercentError).isLessThan(1.5); //0.3 is accessible locally but not on travis
    }

    @Test
    public void testRestCall() {
        ComputationDescription cd = new ComputationDescription(0, 0, 0, 2,
                Arrays.asList(
                        new ComputationDescription.ServiceCall(
                                new ComputationDescription(),
                                2
                        )
                )
        );
        Client rsClientMock = createMock(Client.class);
        Configuration conf = new Configuration();
        conf.setHttpHost("localhost");
        conf.setHttpPort((short) 9090);
        Invocation.Builder builderMock = createMock(Invocation.Builder.class);
        WebTarget webTargetMock = createMock(WebTarget.class);
        Response responseMock = createMock(Response.class);

        for(ComputationDescription.ServiceCall sc : cd.getServiceCalls())
        {
            for(int i = 0 ; i < sc.getCallsNumber(); i++) {
                EasyMock.expect(webTargetMock.request(MediaType.TEXT_PLAIN)).andReturn(builderMock);
                EasyMock.expect(builderMock.post(Entity.entity(sc.getComputationDescription(), MediaType.APPLICATION_JSON_TYPE))).andReturn(responseMock);
                EasyMock.expect(responseMock.readEntity(String.class)).andReturn(NOTHING_IT_IS_JUSTE_A_MOCK);
            }
        }

        EasyMock.expect(rsClientMock.target("http://localhost:9090")).andReturn(webTargetMock);
        EasyMock.expect(webTargetMock.path("compute")).andReturn(webTargetMock);

        replay(builderMock);
        replay(webTargetMock);
        replay(responseMock);
        replay(rsClientMock);

        ComputeResource cs = new ComputeResource(null, rsClientMock, conf);
        String response = cs.callRestResource(cd);
        assertThat(response).contains(NOTHING_IT_IS_JUSTE_A_MOCK);

    }

}
