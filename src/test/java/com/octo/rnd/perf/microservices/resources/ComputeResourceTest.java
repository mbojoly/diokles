package com.octo.rnd.perf.microservices.resources;

import com.octo.rnd.perf.microservices.jdbi.DAO;
import com.octo.rnd.perf.microservices.jdbi.DAOImpl;
import com.octo.rnd.perf.microservices.jdbi.StoredProc;
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

        ComputeResource cr = new ComputeResource(dao, null);


        final int nbOfCalls = 1;
        Helper.MeasuredTime mt = Helper.measureTime(
                l -> {
                    cr.callDatabase(nbOfCalls, inputTime);
                    return Optional.empty();
                },
                inputTime,
                1);
        assertThat(mt.PercentError).isLessThan(0.3);
    }


    //@Test
    public void testCallDatabaseTwice() {

        final long inputTime = 100;
        DAO daoMock = createMock(DAO.class);
        daoMock.callStoredProcedure(inputTime);
        daoMock.callStoredProcedure(inputTime);

        ComputeResource cr = new ComputeResource(daoMock, null);

        replay(daoMock);

        cr.callDatabase(2, inputTime);
    }

    @Test
    public void testTimeForCallDatabaseTwice() {
        DAO dao = new DAOImpl(dbi);

        ComputeResource cr = new ComputeResource(dao, null);

        final long inputTime = 100;
        final int nbOfCalls = 1;
        Helper.MeasuredTime mt = Helper.measureTime(
                l -> {
                    cr.callDatabase(nbOfCalls, inputTime);
                    return Optional.empty();
                },
                inputTime,
                nbOfCalls);
        assertThat(mt.PercentError).isLessThan(0.3);
    }

    @Test
    public void testTimeForCpuIntensiveCompute() {
        ComputeResource cr = new ComputeResource(null, null);
        final long inputTime = 100;
        //Warm-up
        Helper.measureTime(l -> Optional.of(cr.cpuIntensiveCompute(l)), inputTime, 1);
        Helper.MeasuredTime mt = Helper.measureTime(l -> Optional.of(cr.cpuIntensiveCompute(l)), inputTime, 1);
        if (mt.InnerTimeMillis.isPresent()) assertThat(mt.InnerTimeMillis.get()).isLessThanOrEqualTo(inputTime);
        assertThat(mt.PercentError).isLessThan(0.2);
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

        EasyMock.expect(rsClientMock.target("http://localhost:8080")).andReturn(webTargetMock);
        EasyMock.expect(webTargetMock.path("compute")).andReturn(webTargetMock);

        replay(builderMock);
        replay(webTargetMock);
        replay(responseMock);
        replay(rsClientMock);

        ComputeResource cs = new ComputeResource(null, rsClientMock);
        String response = cs.callRestResource(cd);
        assertThat(response).contains(NOTHING_IT_IS_JUSTE_A_MOCK);

    }

}
