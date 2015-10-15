package com.octo.rnd.perf.microservices.jdbi;


import com.octo.rnd.perf.microservices.Application;
import com.octo.rnd.perf.microservices.Configuration;
import org.h2.jdbcx.JdbcConnectionPool;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.exceptions.DBIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import javax.validation.constraints.NotNull;

public class DAOImpl implements DAO {

    final static Logger logger = LoggerFactory.getLogger(DAOImpl.class);


    //For testing purpose
    private static ThreadLocal<DBI> dbi;

    /**
     * For testing purpose
     * @param dbi To be instancated by unit test
     */
    public DAOImpl(@NotNull final DBI dbi) {
        this.dbi = new ThreadLocal<>();
        this.dbi.set(dbi);
    }

    public DAOImpl(@NotNull final Configuration configuration) {
        final String dbHost = configuration.getDbHost();
        final short dbPort = configuration.getDbPort();
        final short traceLevelSystem = configuration.getTraceLevel();

        if(dbi == null) {
            dbi=
            new ThreadLocal<DBI>() {
                @Override protected DBI initialValue() {
                    final String h2Url = buildH2Url(dbHost, dbPort, traceLevelSystem);
                    DataSource ds = JdbcConnectionPool.create(h2Url, "", "");
                    final DBI localDbi = new DBI(ds);
                    final Handle h = localDbi.open();
                    h.begin();
                    try {
                        h.execute("drop alias sleep");
                    } catch (Exception e) {
                        // okay if not present
                    }

                    h.execute("CREATE ALIAS SLEEP " +
                            "FOR \"java.lang.Thread.sleep\"");
                    logger.debug("SLEEP ProcStock created for {}", h2Url);

                    h.commit();
                    h.close();

                    return localDbi;
                }
            };
        }
    }

    /**
     * Static for testing purpose
     **/
    static String buildH2Url(String dbHost, short dbPort, short traceLevelSystem) {
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

    public final void callStoredProcedure(long millis) {
        StoredProc sp = getDbi().onDemand(StoredProc.class);
        sp.begin();
        try {
            sp.callStoredProcedure(millis);
            sp.commit();
        } catch(DBIException dbiex) {
            sp.rollback();
            throw dbiex;
        }
    }
}
