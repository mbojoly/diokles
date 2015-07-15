package com.octo.rnd.perf.microservices.resources;


import com.codahale.metrics.annotation.Timed;
import com.google.common.base.Optional;
import com.octo.rnd.perf.microservices.Configuration;
import com.octo.rnd.perf.microservices.jdbi.ProcStockDAO;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.setup.Environment;
import org.skife.jdbi.v2.DBI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.math.BigDecimal;
import java.util.Random;

@Path("/compute")
public class ComputeResource {
    final Logger logger = LoggerFactory.getLogger(ComputeResource.class);
    final ProcStockDAO dao;

    public ComputeResource(final ProcStockDAO dao) {
        this.dao = dao;
    }

    @GET
    @Timed
    public String get() {
        return "Creating a new resource is volontary doing the worst thing a performance application can do. " +
                "Just PUT and See...";
    }

    @POST
    @Timed
    public String compute(@Valid ComputationDescription computationDescription) {
        cpuIntensiveCompute(10000);
        return computationDescription.toString();
    }


    /**
     * This method calls several time a stored procedure that only wait during the target time
     * @param nbCalls number of Stored Procedure Call to perform
     * @param targetUnitMillis Number of millisecond to sleep
     */
    public void callDatabase(final long nbCalls, final long targetUnitMillis) {
        for(long i = 0; i < nbCalls; i++) {
            //https://github.com/stevenalexander/dropwizard-jdbi
            dao.callStoredProcedure(targetUnitMillis);
        }
    }

    /**
     * This method implements a simple compute intensive algorithm that loops during approximately the target time
     * Measure in the unit test shows a 20% difference with a 100 ms. target but it is not a SLA for the method
     * @param targetMillis Target elapse compute time in ms.
     * @return Measured elapse compute time in ms. inside the method.
     */
    long cpuIntensiveCompute(final long targetMillis) {
        final long NS_TO_MS = 1000000;
        final long start = System.nanoTime();
        Random random = new Random();
        //Capitalization compute
        BigDecimal futureValue;
        BigDecimal dailyRate;
        int nbOfDay;

        dailyRate = new BigDecimal(Math.abs(random.nextGaussian()));
        futureValue = new BigDecimal(Math.abs(random.nextGaussian()*10));
        nbOfDay = Math.abs(random.nextInt(10));
        BigDecimal actualValue = BigDecimal.ZERO;
        long end = System.nanoTime();
        long elapse = end - start;
        long maxIncElapse = 0; //maximum elapse time by loop

        //Prefer to be below than above because function call especially at startup is costly
        while(elapse + maxIncElapse < targetMillis * NS_TO_MS) {
            //actualValue = actualValue.add(futureValue.multiply((dailyRate.add(BigDecimal.ONE)).pow(nbOfDay)));
            final long now = System.nanoTime();
            maxIncElapse = now - end > maxIncElapse ? now - end : maxIncElapse;
            logger.trace("Increment elapse is {} ns", maxIncElapse);
            end = now;
            elapse = end - start;
        }

        logger.debug("Compute result is {}", actualValue);
        return elapse / NS_TO_MS;
    }


}
