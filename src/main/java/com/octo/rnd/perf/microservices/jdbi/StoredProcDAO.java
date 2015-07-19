package com.octo.rnd.perf.microservices.jdbi;


import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlCall;

/**
 * See https://github.com/sahilm/dropwizard-snapci-sample/blob/master/src/main/java/com/snapci/microblog/jdbi/UserDAO.java
 */
public interface StoredProcDAO {

    //See https://github.com/jdbi/jdbi/blob/master/src/test/java/org/skife/jdbi/v2/sqlobject/TestSqlCall.java
    @SqlCall("call sleep(:millis)")
    public void callStoredProcedure(@Bind("millis") long millis);
}
