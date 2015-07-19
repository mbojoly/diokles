package com.octo.rnd.perf.microservices.resources;


import com.codahale.metrics.annotation.Timed;
import com.octo.rnd.perf.microservices.Application;
import com.octo.rnd.perf.microservices.jdbi.DAOFactory;
import com.octo.rnd.perf.microservices.jdbi.StoredProcDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;
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
    final DAOFactory daoFactory;
    final Client rsClient;

    public ComputeResource(final DAOFactory daoFactory, Client rsClient) {
        this.daoFactory = daoFactory;
        this.rsClient = rsClient;
    }

    @GET
    @Timed
    public String get() {
        return "Creating a new resource is volontary doing the worst thing a performance application can do. " +
                "Just POST and See...";
    }

    @POST
    @Timed
    public String compute(@Valid ComputationDescription computationDescription) {
        if (computationDescription == null) throw new IllegalArgumentException();

        StringBuilder builder = new StringBuilder();

        long time;

        time = callDatabase(computationDescription.getDatabaseCallsNumber(), computationDescription.getDatabaseCallDuration());
        builder.append("Call the database ")
                .append(computationDescription.getDatabaseCallsNumber())
                .append(" times during ")
                .append(computationDescription.getDatabaseCallDuration())
                .append(" ms. each for a total of ")
                .append(time)
                .append(" ms.")
                .append(System.lineSeparator());

        time = cpuIntensiveCompute(computationDescription.getCpuIntensiveComputationsDuration());
        builder.append("CPU intensive compute ").append(time).append("ms. ").append(System.lineSeparator());

        builder.append(
                callRestResource(computationDescription)
        );

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
        builder.append("Call HTTP Ressources :");
        final long begin = System.nanoTime();
        if (computationDescription.getServiceCalls() != null && computationDescription.getServiceCalls().size() > 0) {
            WebTarget target = rsClient.target("http://localhost:8080").path("compute");

            //TODO : Check the log
            //TODO : Check the client header (see log of dropwizard for recursive call)
            for (ComputationDescription.ServiceCall sc : computationDescription.getServiceCalls()) {
                for (int i = 0; i < sc.getCallsNumber(); i++) {
                    Response response = target.request(MediaType.TEXT_PLAIN).post(Entity.entity(sc.getComputationDescription(), MediaType.APPLICATION_JSON_TYPE));
                    builder.append('{').append(System.lineSeparator()).append(response.readEntity(String.class))
                    .append('}').append(System.lineSeparator());
                }
            }

        }

        final long end = System.nanoTime();
        final double duration = (end - begin) / Application.MS_IN_NS;
        builder.append(" For a total of ")
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
        StoredProcDAO storedProcDAO = daoFactory.getProcStockDAO();
        for (long i = 0; i < nbCalls; i++) {
            //https://github.com/stevenalexander/dropwizard-jdbi
            storedProcDAO.callStoredProcedure(targetUnitMillis);
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
        long maxIncElapse = 0; //maximum elapse time by loop

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
