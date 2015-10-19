package com.octo.rnd.perf.diokles.jdbi;


import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlCall;
import org.skife.jdbi.v2.sqlobject.mixins.Transactional;

/**
 * See https://github.com/sahilm/dropwizard-snapci-sample/blob/master/src/main/java/com/snapci/microblog/jdbi/UserDAO.java
 * See http://skife.org/jdbi/java/library/sql/2011/03/16/jdbi-sql-objects.html
 */
public interface StoredProc extends Transactional<StoredProc> {

    //See https://github.com/jdbi/jdbi/blob/master/src/test/java/org/skife/jdbi/v2/sqlobject/TestSqlCall.java
    @SqlCall("call sleep(:millis)")
    public void callStoredProcedure(@Bind("millis") long millis);
}
