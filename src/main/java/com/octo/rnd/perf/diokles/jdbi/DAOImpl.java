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



import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.octo.rnd.perf.diokles.Application;
import com.octo.rnd.perf.diokles.Configuration;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;
import java.beans.PropertyVetoException;

/**
 * DAO for sp call
 */
public class DAOImpl implements DAO {

    private final static Logger LOGGER = LoggerFactory.getLogger(DAOImpl.class);


    //For testing purpose
    private static ThreadLocal<DBI> dbi;

    /**
     * For testing purpose
     * @param pDbi To be instancated by unit test
     */
    public DAOImpl(@NotNull final DBI pDbi) {
        this.dbi = new ThreadLocal<>();
        this.dbi.set(pDbi);
    }

    public DAOImpl(@NotNull final Configuration configuration) {
        final String dbHost = configuration.getDbHost();
        final short dbPort = configuration.getDbPort();
        final short traceLevelSystem = configuration.getTraceLevel();

        if(dbi == null) {
            dbi=
            new ThreadLocal<DBI>()  {
                @Override protected DBI initialValue() {
                    final String h2Url = buildH2Url(dbHost, dbPort, traceLevelSystem);
                    ComboPooledDataSource ds = new ComboPooledDataSource ();
                    try {
                        ds.setDriverClass("org.h2.Driver");
                    } catch (PropertyVetoException e) {
                        LOGGER.error("Unable to load the org.h2.Driver", e);
                        return null;
                    }
                    ds.setJdbcUrl(h2Url);
                    ds.setAcquireIncrement(1);
                    ds.setInitialPoolSize(0);
                    ds.setMaxPoolSize(1);

                    final DBI localDbi = new DBI(ds);
                    final Handle h = localDbi.open();
                    try {
                        h.execute("drop alias sleep");
                        //org.h2.jdbc.JdbcSQLException: Function alias "SLEEP" not found; SQL statement: is thrown
                        //but is not declared. strange ?
                    } catch (Throwable e) {
                        LOGGER.debug("Drop alias sleep failed (normal not present)", e);
                    }

                    h.execute("CREATE ALIAS SLEEP " +
                            "FOR \"java.lang.Thread.sleep\"");
                    LOGGER.debug("SLEEP ProcStock created for {}", h2Url);

                    h.close();

                    return localDbi;
                }
            };
        }
    }

    /**
     * Static for testing purpose
     **/
    public static String buildH2Url(String dbHost, short dbPort, short traceLevelSystem) {
        return "jdbc:h2:tcp://" + (Application.DEFAULT_HOST.equals(dbHost) ? "localhost" : dbHost) + ":" + dbPort + "/~/perfms-" + Thread.currentThread().getId() + ";TRACE_LEVEL_SYSTEM_OUT=" + Short.toString(traceLevelSystem);
    }

    //http://www.h2database.com/html/features.html#multiple_connections
    //Multi-threaded is experimental but for a single database, only one request can run simultaneously
    //So I choose to open one database per application thread in order to model the behaviour of a real DBMS


    /**
     * For testing purpose only
     * @return DBI from threadlocal
     */
    public final DBI getDbi() {
        return dbi.get();
    }

    public final void callStoredProcedure(long millis) throws JDBIException {
        if(getDbi() != null) {
            StoredProc sp = getDbi().onDemand(StoredProc.class);
            sp.callStoredProcedure(millis);
        }
        else {
            throw new JDBIException("Unable to get DB access");
        }
    }
}
