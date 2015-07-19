package com.octo.rnd.perf.microservices.resources;

import com.octo.rnd.perf.microservices.jdbi.ProcStockDAO;
import junit.framework.Assert;
import org.h2.tools.Server;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.exceptions.UnableToExecuteStatementException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class ComputeResourceIntegrationTest {

    final static Logger logger = LoggerFactory.getLogger(ComputeResourceIntegrationTest.class);

    static Server server;

    @BeforeClass
    public static void doSetupH2Server() throws SQLException {
        server = Server.createTcpServer("-tcpPort", "9093").start();
    }

    @AfterClass
    public static void doTearDownH2Server() {
        if (server != null) server.stop();
    }

    /**
     * See https://github.com/jdbi/jdbi/blob/master/src/test/java/org/skife/jdbi/v2/TestCallable.java
     * but the correct syntax is now here http://www.h2database.com/h2.pdf
     *
     */
    public DBI doSetUp()  {

        //http://www.h2database.com/html/features.html#multiple_connections
        //Multi-threaded is experimental but for a single database, only one request can run simultaneously
        //So I choose to open one database per application thread in order to model the behaviour of a real DBMS
        final DBI dbi = new DBI("jdbc:h2:tcp://localhost:9093/mem:perfms" + UUID.randomUUID() + ":MULTI_THREADED=1");
        final Handle h = dbi.open();
        try {
            h.execute("drop alias sleep");
        } catch (Exception e) {
            // okay if not present
        }

        h.execute("CREATE ALIAS SLEEP " +
                "FOR \"java.lang.Thread.sleep\"");

        return dbi;
    }

    public static void doTearDown(final DBI dbi) {
        Handle h = dbi.open();
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
    public void testIntegrationForCallDatabase() throws InterruptedException {

        DbCaller dbc1 = new DbCaller();
        DbCaller dbc2 = new DbCaller();
        DbCaller dbc3 = new DbCaller();
        DbCaller dbc4 = new DbCaller();

        Thread t1 = new Thread(() -> dbc1.doDbCall(200));
        Thread t2 = new Thread(() -> dbc2.doDbCall(500));
        Thread t3 = new Thread(() -> dbc3.doDbCall(5000));
        Thread t4 = new Thread(() -> dbc4.doDbCall(20000));


        t1.start();
        t2.start();
        t3.start();
        t4.start();

        logger.debug("Wait for tread1 to finish");
        t1.join();
        assertThat(dbc1.error).isLessThan(0.3);

        logger.debug("Wait for thread2 to finish");
        t2.join();
        assertThat(dbc2.error).isLessThan(0.3);

        logger.debug("Wait for thread3 to finish");
        t3.join();
        assertThat(dbc3.error).isLessThan(0.3);

        logger.debug("Wait for thread4 to finish");
        t4.join();
        assertThat(dbc4.error).isLessThan(0.3);
}

    private class DbCaller {
        private double  error;

        private void doDbCall(long inputTime) {

            DBI dbi = doSetUp();
            ProcStockDAO dao = dbi.onDemand(ProcStockDAO.class);
            ComputeResource cr = new ComputeResource(dao);

            logger.debug("Start Calling a stored procedure of {} ms", inputTime);
            final int nbOfCalls = 1;
            Helper.MeasuredTime mt = Helper.measureTime(
                    l -> {
                        cr.callDatabase(nbOfCalls, inputTime);
                        return Optional.empty();
                    },
                    inputTime,
                    1);

            this.error = mt.PercentError;

            doTearDown(dbi);
        }
    }


}
