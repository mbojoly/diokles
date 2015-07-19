package com.octo.rnd.perf.microservices.resources;

import com.octo.rnd.perf.microservices.Application;
import com.octo.rnd.perf.microservices.jdbi.DAOFactoryImpl;
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

import static org.assertj.core.api.Assertions.assertThat;

public class ComputeResourceIntegrationTest {

    final static Logger logger = LoggerFactory.getLogger(ComputeResourceIntegrationTest.class);

    static Server server;
    //Be sure to have only one DAOFactory for all the application
    //In the contrary there will be several DB per thread as ThreadLocal is specific to a pair (instance, thread)
    static final DAOFactoryImpl daoFactory = new DAOFactoryImpl();

    @BeforeClass
    public static void doSetupH2Server() throws SQLException {
        server = Server.createTcpServer("-tcpPort", new Short(Application.H2_TCP_PORT).toString()).start();
    }


    public static void doTearDownH2Server() {
        if (server != null) server.stop();
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
        Thread t3 = new Thread(() -> dbc3.doDbCall(750));
        Thread t4 = new Thread(() -> dbc4.doDbCall(1000));


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

        //@AfterClass seems to not function correctly in a multi-threaded environment
        doTearDownH2Server();
    }

    private class DbCaller {
        private double error;

        private void doDbCall(long inputTime) {

            //Force to build the DB before measuring
            daoFactory.getProcStockDAO();

            ComputeResource cr = new ComputeResource(daoFactory);

            logger.debug("Start Calling a stored procedure of {} ms", inputTime);
            final int nbOfCalls = 1;
            try {
                Helper.MeasuredTime mt = Helper.measureTime(
                        l -> {
                            cr.callDatabase(nbOfCalls, inputTime);
                            return Optional.empty();
                        },
                        inputTime,
                        1);
                this.error = mt.PercentError;
            } catch (Exception ex) {
                this.error = Double.MAX_VALUE; //Be sure that the test fail
                throw ex;
            }


            doTearDown(daoFactory.getDbi());
        }
    }


}
