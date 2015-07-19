package com.octo.rnd.perf.microservices.resources;

import com.octo.rnd.perf.microservices.jdbi.ProcStockDAO;
import static org.easymock.EasyMock.*;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.exceptions.UnableToExecuteStatementException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;


public class ComputeResourceTest {

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
        dbi = new DBI("jdbc:h2:mem:" + UUID.randomUUID() + ":MULTI_THREADED=1");
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
        ProcStockDAO dao = dbi.onDemand(ProcStockDAO.class);
        ComputeResource cr = new ComputeResource(dao);

        final long inputTime = 100;
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


    @Test
    public void testCallDatabaseTwice() {

        ProcStockDAO daoMock = createMock(ProcStockDAO.class);
        ComputeResource cr = new ComputeResource(daoMock);

        final long inputTime = 100;
        daoMock.callStoredProcedure(inputTime);
        daoMock.callStoredProcedure(inputTime);

        replay(daoMock);
        cr.callDatabase(2, inputTime);
    }

    @Test
    public void testTimeForCallDatabaseTwice() {
        ProcStockDAO dao = dbi.onDemand(ProcStockDAO.class);
        ComputeResource cr = new ComputeResource(dao);

        final long inputTime = 100;
        final int nbOfCalls = 2;
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
        ComputeResource cr = new ComputeResource(null);
        final long inputTime = 100;
        Helper.MeasuredTime mt = Helper.measureTime(l -> Optional.of(cr.cpuIntensiveCompute(l)), inputTime, 1);
        if (mt.InnerTimeMillis.isPresent()) assertThat(mt.InnerTimeMillis.get()).isLessThanOrEqualTo(inputTime);
        assertThat(mt.PercentError).isLessThan(0.2);
    }

}
