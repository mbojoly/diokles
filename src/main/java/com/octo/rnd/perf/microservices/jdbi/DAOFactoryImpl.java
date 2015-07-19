package com.octo.rnd.perf.microservices.jdbi;


import com.octo.rnd.perf.microservices.Application;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DAOFactoryImpl implements  DAOFactory {

    final static Logger logger = LoggerFactory.getLogger(DAOFactoryImpl.class);

    //http://www.h2database.com/html/features.html#multiple_connections
    //Multi-threaded is experimental but for a single database, only one request can run simultaneously
    //So I choose to open one database per application thread in order to model the behaviour of a real DBMS


    //For testing purpose
    private static final ThreadLocal<DBI> dbi =
            new ThreadLocal<DBI>() {
                @Override protected DBI initialValue() {
                    final String h2Url = "jdbc:h2:tcp://localhost:" + Application.H2_TCP_PORT + "/~/perfms-" + Thread.currentThread().getId();
                    final DBI localDbi = new DBI(h2Url);
                    final Handle h = localDbi.open();
                    try {
                        h.execute("drop alias sleep");
                    } catch (Exception e) {
                        // okay if not present
                    }

                    h.execute("CREATE ALIAS SLEEP " +
                            "FOR \"java.lang.Thread.sleep\"");
                    logger.debug("SLEEP ProcStock created for {}", h2Url);

                    if (h != null) h.close();

                    return localDbi;
                };
            };

    private static final ThreadLocal<StoredProcDAO> procStockDao =
            new ThreadLocal<StoredProcDAO>() {
                @Override protected StoredProcDAO initialValue() {
                    return dbi.get().onDemand(StoredProcDAO.class);
                };
    };

    /**
     * For testing purpose only
     * @return
     */
    public final DBI getDbi() {
        return dbi.get();
    }

    public final StoredProcDAO getProcStockDAO() {
        return procStockDao.get();
    }
}
