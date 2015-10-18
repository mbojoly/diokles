package com.octo.rnd.perf.microservices.resources;


import com.codahale.metrics.annotation.Timed;
import com.octo.rnd.perf.microservices.Application;
import com.octo.rnd.perf.microservices.Configuration;
import com.octo.rnd.perf.microservices.jdbi.DAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.Random;

@Path("/compute")
public class ComputeResource {
    final Logger logger = LoggerFactory.getLogger(ComputeResource.class);
    final DAO dao;
    final Client rsClient;
    final String httpTarget;

    public ComputeResource(final DAO dao, Client rsClient, @NotNull Configuration configuration) {
        this.dao = dao;
        this.rsClient = rsClient;
        this.httpTarget = configuration  != null ? "http://" + configuration.getHttpHost() + ":8080" : "No configuration";
    }

    @GET
    @Timed
    public String get() {
        return "POST on this compute ressource a description of the behaviour you want to simulate : " +
                "'{\"cpuIntensiveComputationsDuration\":60, \"databaseCallsNumber\":6, \"databaseCallDuration\":12, \"serviceCalls\":[{\"computationDescription\":{\"cpuIntensiveComputationsDuration\":60, \"databaseCallsNumber\":6, \"databaseCallDuration\":5}, \"callsNumber\":2 }, {\"computationDescription\":{\"cpuIntensiveComputationsDuration\":60, \"databaseCallsNumber\":6, \"databaseCallDuration\":5}, \"callsNumber\":2 }, {\"computationDescription\":{\"cpuIntensiveComputationsDuration\":60, \"databaseCallsNumber\":6, \"databaseCallDuration\":5}, \"callsNumber\":2 }, {\"computationDescription\":{\"cpuIntensiveComputationsDuration\":60, \"databaseCallsNumber\":6, \"databaseCallDuration\":5}, \"callsNumber\":2 }, {\"computationDescription\":{\"cpuIntensiveComputationsDuration\":60, \"databaseCallsNumber\":6, \"databaseCallDuration\":5}, \"callsNumber\":2 }]}'";
    }

    @POST
    @Timed
    public String compute(@Valid ComputationDescription computationDescription) {
        if (computationDescription == null) throw new IllegalArgumentException();

        final StringBuilder builder = new StringBuilder(System.lineSeparator());
        long time;

        builder.append(
                callRestResource(computationDescription)
        );

        builder.append(System.lineSeparator());

        time = callDatabase(computationDescription.getDatabaseCallsNumber(), computationDescription.getDatabaseCallDuration());
        builder.append("Call the database ")
                .append(computationDescription.getDatabaseCallsNumber())
                .append(" times during ")
                .append(computationDescription.getDatabaseCallDuration())
                .append(" ms. each for a total of ")
                .append(time)
                .append(" ms.");

        builder.append(System.lineSeparator());

        time = cpuIntensiveCompute(computationDescription.getCpuIntensiveComputationsDuration());
        builder.append("CPU intensive compute ").append(time).append("ms. ");


        return builder.toString();
    }

    /**
     * This method calls recursively through HTTP the computeResource in order to model a call to a service
     *
     * @param computationDescription Same description as for this resource
     * @return Time and HTTP response received
     */
    public String callRestResource(ComputationDescription computationDescription) {
        StringBuilder builder = new StringBuilder();
        builder.append("Call HTTP Ressources : ");
        final long begin = System.nanoTime();
        if (computationDescription.getServiceCalls() != null && computationDescription.getServiceCalls().size() > 0) {
            WebTarget target = rsClient.target(this.httpTarget).path("compute");

            //TODO : Check the client header in order to use a JSON format rather that plain old text
            for (ComputationDescription.ServiceCall sc : computationDescription.getServiceCalls()) {
                for (int i = 0; i < sc.getCallsNumber(); i++) {
                    Response response = target.request(MediaType.TEXT_PLAIN).post(Entity.entity(sc.getComputationDescription(), MediaType.APPLICATION_JSON_TYPE));
                    final String rspContent = response.readEntity(String.class);
                    final String[] rspLines = rspContent.split(System.lineSeparator());
                    builder.append("{").append(System.lineSeparator());
                    for(String l : rspLines) {
                        if(!"".equals(l)) { //Remove blank line added for visibility
                            builder.append('\t').append(l).append(System.lineSeparator());
                        }
                    }
                    builder.append("{,").append(System.lineSeparator());
                }
            }

        }

        final long end = System.nanoTime();
        final double duration = (end - begin) / Application.MS_IN_NS;
        builder.append("For an HTTP ressources total of ")
                .append(duration)
                .append(" ms.");
        return builder.toString();
    }

    /**
     * This method calls several time a stored procedure that only wait during the target time
     *
     * @param nbCalls          number of Stored Procedure Call to perform
     * @param targetUnitMillis Number of millisecond to sleep
     */
    public long callDatabase(final long nbCalls, final long targetUnitMillis) {
        final long begin = System.nanoTime();
        for (long i = 0; i < nbCalls; i++) {
            //https://github.com/stevenalexander/dropwizard-jdbi
            dao.callStoredProcedure(targetUnitMillis);
        }
        final long end = System.nanoTime();
        return (end - begin) / Application.MS_IN_NS;
    }

    /**
     * This method implements a simple compute intensive algorithm that loops during approximately the target time
     * Measure in the unit test shows a 20% difference with a 100 ms. target but it is not a SLA for the method
     *
     * @param targetMillis Target elapse compute time in ms.
     * @return Measured elapse compute time in ms. inside the method.
     */
    long cpuIntensiveCompute(final long targetMillis) {
        final long start = System.nanoTime();
        Random random = new Random();
        //Capitalization compute
        BigDecimal futureValue;
        BigDecimal dailyRate;
        int nbOfDay;

        dailyRate = new BigDecimal(Math.abs(random.nextGaussian()));
        futureValue = new BigDecimal(Math.abs(random.nextGaussian() * 10));
        nbOfDay = Math.abs(random.nextInt(10));
        BigDecimal actualValue = BigDecimal.ZERO;
        long end = System.nanoTime();
        long elapse = end - start;
        long maxIncElapse = elapse; //maximum elapse time by loop

        //Prefer to be below than above because function call especially at startup is costly
        while (elapse + maxIncElapse < targetMillis * Application.MS_IN_NS) {
            actualValue = actualValue.add(futureValue.multiply((dailyRate.add(BigDecimal.ONE)).pow(nbOfDay)));
            final long now = System.nanoTime();
            maxIncElapse = now - end > maxIncElapse ? now - end : maxIncElapse;
            logger.trace("Increment elapse is {} ns", maxIncElapse);
            end = now;
            elapse = end - start;
        }

        logger.debug("Compute result is {}", actualValue);
        return elapse / Application.MS_IN_NS;
    }


}
