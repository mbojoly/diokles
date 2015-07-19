package com.octo.rnd.perf.microservices.jdbi;

import org.skife.jdbi.v2.DBI;


/**
 * This interface allows to simply mock the factory without add testing special code
 */
public interface DAOFactory {
    public DBI getDbi();

    public StoredProcDAO getProcStockDAO();
}
