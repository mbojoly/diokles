package com.octo.rnd.perf.microservices;

import com.octo.rnd.perf.microservices.health.TemplateHealthCheck;
import com.octo.rnd.perf.microservices.jdbi.DAOFactoryImpl;
import com.octo.rnd.perf.microservices.resources.ComputeResource;
import com.octo.rnd.perf.microservices.resources.HelloWorldResource;
import de.thomaskrille.dropwizard.environment_configuration.EnvironmentConfigurationFactoryFactory;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.h2.tools.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import java.sql.SQLException;

public class Application extends io.dropwizard.Application<Configuration> {

    final static Logger logger = LoggerFactory.getLogger(Application.class);

    public static final long MS_IN_NS = 1000000;
    static final String DEFAULT_HOST = "INTERNAL";
    static final short H2_DEFAULT_TCP_PORT = 9093;

    public static void main(String[] args) throws Exception {
        new Application().run(args);
    }

    @Override
    public String getName() {
        return "hello-world";
    }

    @Override
    public void initialize(Bootstrap<Configuration> bootstrap) {
        if(bootstrap != null) {
            bootstrap.setConfigurationFactoryFactory(new EnvironmentConfigurationFactoryFactory<>());
        }
    }

    @Override
    public void run(Configuration configuration,
                    Environment environment) throws SQLException {

        startH2IfNeeded(configuration);

        final HelloWorldResource resource = new HelloWorldResource(
                configuration.getTemplate(),
                configuration.getDefaultName()
        );
        environment.jersey().register(resource);

        final DAOFactoryImpl daoFactory = new DAOFactoryImpl(configuration);

        final Client client = new JerseyClientBuilder(environment).using(configuration.getJerseyClientConfiguration())
                .build(getName());

        final ComputeResource computeResource = new ComputeResource(daoFactory, client);
        environment.jersey().register(computeResource);

        final TemplateHealthCheck healthCheck =
                new TemplateHealthCheck(configuration.getTemplate());
        environment.healthChecks().register("template", healthCheck);
        environment.jersey().register(resource);
    }

    void startH2IfNeeded(Configuration configuration) throws SQLException {
        if(DEFAULT_HOST.equals(configuration.getDbHost())) {
            final String port = Short.toString(Application.H2_DEFAULT_TCP_PORT);
            logger.info("Starting internal H2 server with port {}", port);
            Server s = Server.createTcpServer("-tcpPort", port);
            s.start();
        }
    }

}
