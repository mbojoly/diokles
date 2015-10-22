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
import com.octo.rnd.perf.diokles.jdbi.DAOImpl;
import com.octo.rnd.perf.diokles.jdbi.JDBIException;
import org.h2.tools.Server;
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
    static Configuration conf;
    static DAOImpl dao;
    static {
        conf = new Configuration();
        conf.setDbHost("localhost");
        conf.setDbPort((short) 9193);
        //Be sure to have only one DAO for all the application
        //In the contrary there will be several DB per thread as ThreadLocal is specific to a pair (instance, thread)
        dao = new DAOImpl(conf);
    }


    @BeforeClass
    public static void doSetupH2Server() throws SQLException {
        server = Server.createTcpServer("-tcpPort", Short.toString(conf.getDbPort())).start();
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

            //Force building the DB before measuring
            try {
                dao.callStoredProcedure(1);
            } catch (JDBIException e) {
                e.printStackTrace();
            }

            ComputeResource cr = new ComputeResource(dao, null, null);

            logger.debug("Start Calling a stored procedure of {} ms", inputTime);
            final int nbOfCalls = 1;
            try {
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
                this.error = mt.PercentError;
            } catch (Exception ex) {
                this.error = Double.MAX_VALUE; //Be sure that the test fail
                throw ex;
            }


            doTearDown(dao.getDbi());
        }
    }


}
