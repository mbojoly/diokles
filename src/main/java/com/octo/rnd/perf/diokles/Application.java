package com.octo.rnd.perf.diokles;

/*
 * #%L
 * diokles
 * %%
 * Copyright (C) 2015 OCTO Technology
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
import com.octo.rnd.perf.diokles.jdbi.DAOImpl;
import com.octo.rnd.perf.diokles.resources.ComputeResource;
import de.thomaskrille.dropwizard.environment_configuration.EnvironmentConfigurationFactoryFactory;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.h2.tools.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import java.sql.SQLException;

/**
 * Entry point
 */
public class Application extends io.dropwizard.Application<Configuration> {

    /**
     * Default constructor
     */
    public Application() { super();}

    private final static Logger LOGGER = LoggerFactory.getLogger(Application.class);

    public static final long MS_IN_NS = 1000000;
    public static final String DEFAULT_HOST = "INTERNAL";
    public static final short H2_DEFAULT_TCP_PORT = 9093;

    /**
     *
     * @param args Nothing
     * @throws Exception for any problem
     */
    public static void main(String[] args) throws Exception {
        new Application().run(args);
    }

    @Override
    public String getName() {
        return "diokles";
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

        final DAOImpl dao = new DAOImpl(configuration);

        final Client client = new JerseyClientBuilder(environment).using(configuration.getJerseyClientConfiguration())
                .build(getName());

        final ComputeResource computeResource = new ComputeResource(dao, client, configuration);
        environment.jersey().register(computeResource);
    }

    /**
     *
     * @param configuration conf
     * @throws SQLException if not correctly initialized
     */
    public void startH2IfNeeded(Configuration configuration) throws SQLException {
        if(DEFAULT_HOST.equals(configuration.getDbHost())) {
            final String port = Short.toString(Application.H2_DEFAULT_TCP_PORT);
            LOGGER.info("Starting internal H2 server with port {}", port);
            Server s = Server.createTcpServer("-tcpPort", port);
            s.start();
        }
    }

}
