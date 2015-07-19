package com.octo.rnd.perf.microservices;

import com.octo.rnd.perf.microservices.health.TemplateHealthCheck;
import com.octo.rnd.perf.microservices.jdbi.DAOFactoryImpl;
import com.octo.rnd.perf.microservices.resources.ComputeResource;
import com.octo.rnd.perf.microservices.resources.HelloWorldResource;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.h2.tools.Server;

import java.sql.SQLException;

public class Application extends io.dropwizard.Application<Configuration> {

    public static final long MS_IN_NS = 1000000;
    public static final short H2_TCP_PORT = 9093;

    public static void main(String[] args) throws Exception {
        new Application().run(args);
    }

    @Override
    public String getName() {
        return "hello-world";
    }

    @Override
    public void initialize(Bootstrap<Configuration> bootstrap) {
        // nothing to do yet
    }

    @Override
    public void run(Configuration configuration,
                    Environment environment) throws SQLException {

        Server.createTcpServer("-tcpPort", new Short(Application.H2_TCP_PORT).toString()).start();

        final HelloWorldResource resource = new HelloWorldResource(
                configuration.getTemplate(),
                configuration.getDefaultName()
        );
        environment.jersey().register(resource);

        final DAOFactoryImpl daoFactory = new DAOFactoryImpl();

        final ComputeResource computeResource = new ComputeResource(daoFactory);
        environment.jersey().register(computeResource);

        final TemplateHealthCheck healthCheck =
                new TemplateHealthCheck(configuration.getTemplate());
        environment.healthChecks().register("template", healthCheck);
        environment.jersey().register(resource);
    }

}
