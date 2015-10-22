package com.octo.rnd.perf.diokles.jdbi;

/**
 * When driver can not have been loaded
 */
public class JDBIException extends Exception {
    public JDBIException(String s)  {
        super(s);
    }
}
