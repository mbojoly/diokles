package com.octo.rnd.perf.microservices;

import com.octo.rnd.perf.microservices.resources.ComputeResource;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import com.octo.rnd.perf.microservices.resources.HelloWorldResource;
import com.octo.rnd.perf.microservices.health.TemplateHealthCheck;
import org.skife.jdbi.v2.DBI;

public class Application extends io.dropwizard.Application<Configuration> {
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
                    Environment environment) {
        final HelloWorldResource resource = new HelloWorldResource(
                configuration.getTemplate(),
                configuration.getDefaultName()
        );
        environment.jersey().register(resource);

        final DBIFactory factory = new DBIFactory();
        final DBI jdbi = factory.build(environment, configuration.getDataSourceFactory(), "h2");
        //final UserDAO dao = jdbi.onDemand(UserDAO.class);
        //environment.jersey().register(new UserResource(dao));

        final ComputeResource computeResource = new ComputeResource();
        environment.jersey().register(computeResource);

        final TemplateHealthCheck healthCheck =
                new TemplateHealthCheck(configuration.getTemplate());
        environment.healthChecks().register("template", healthCheck);
        environment.jersey().register(resource);
    }

}
