package com.octo.rnd.perf.diokles.jdbi;

import org.skife.jdbi.v2.DBI;


/**
 * This interface allows to simply mock the DAO without add testing special code
 */
public interface DAO {
    DBI getDbi();

    void callStoredProcedure(long millis);
}
