package com.octo.rnd.perf.microservices.resources;


import com.codahale.metrics.annotation.Timed;
import com.google.common.base.Optional;
import com.octo.rnd.perf.microservices.Configuration;
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





    public long callDatabase(final long nbCalls) {
        for(long i = 0; i < nbCalls; i++) {
            //https://github.com/stevenalexander/dropwizard-jdbi
        }
        return  nbCalls;
    }

    /**
     *
     * @param targetTime Target elapse compute time in ms.
     * @return
     */
    public long cpuIntensiveCompute(final long targetTime) {
        final long start = System.nanoTime();
        Random random = new Random();
        //Capitalization compute
        BigDecimal futureValue;
        BigDecimal dailyRate;
        int nbOfDay;

        dailyRate = new BigDecimal(Math.abs(random.nextGaussian()));
        futureValue = new BigDecimal(Math.abs(random.nextGaussian()*10));
        nbOfDay = Math.abs(random.nextInt(25));
        BigDecimal now = BigDecimal.ZERO;
        long end = System.nanoTime();
        long elapse = end - start;

        while(elapse < targetTime * 1000000) {
            now = now.add(futureValue.multiply((dailyRate.add(BigDecimal.ONE)).pow(nbOfDay)));
            end = System.nanoTime();
            elapse = end - start;
        }

        logger.info("Compute result is {}", now);
        return elapse;
    }


}
