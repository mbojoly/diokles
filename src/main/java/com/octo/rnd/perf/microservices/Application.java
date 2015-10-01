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

import javax.ws.rs.client.Client;
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
        if(bootstrap != null) {
            bootstrap.setConfigurationFactoryFactory(new EnvironmentConfigurationFactoryFactory<>());
        }
    }

    @Override
    public void run(Configuration configuration,
                    Environment environment) throws SQLException {

        Server.createTcpServer("-tcpPort", Short.toString(Application.H2_TCP_PORT)).start();

        final HelloWorldResource resource = new HelloWorldResource(
                configuration.getTemplate(),
                configuration.getDefaultName()
        );
        environment.jersey().register(resource);

        final DAOFactoryImpl daoFactory = new DAOFactoryImpl();

        final Client client = new JerseyClientBuilder(environment).using(configuration.getJerseyClientConfiguration())
                .build(getName());

        final ComputeResource computeResource = new ComputeResource(daoFactory, client);
        environment.jersey().register(computeResource);

        final TemplateHealthCheck healthCheck =
                new TemplateHealthCheck(configuration.getTemplate());
        environment.healthChecks().register("template", healthCheck);
        environment.jersey().register(resource);
    }

}
