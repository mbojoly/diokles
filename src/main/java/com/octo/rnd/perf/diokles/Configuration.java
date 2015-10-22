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


import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.client.JerseyClientConfiguration;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * Application configuration
 */
@SuppressWarnings("unused")
public class Configuration extends io.dropwizard.Configuration {

    /** Default configuration **/
    public Configuration() { super(); }

    @NotEmpty
    private String template;

    @NotEmpty
    private String defaultName = "Stranger";

    @NotEmpty
    private String dbHost;

    @NotEmpty
    private String httpHost;

    private short httpPort;

    private short dbPort;

    @NotNull
    private short traceLevel;

    /** Getter/Setter **/
    @JsonProperty
    public String getTemplate() {
        return template;
    }

    @JsonProperty
    public void setTemplate(String pTemplate) {
        this.template = pTemplate;
    }

    @JsonProperty
    public String getDbHost() {
        return dbHost;
    }

    @JsonProperty
    public void setDbHost(String pDbHost) {
        this.dbHost = pDbHost;
    }


    @JsonProperty
    public short getDbPort() { return dbPort; }

    @JsonProperty
    public void setDbPort(short pDbPort) { this.dbPort = pDbPort; }

    @JsonProperty
    public String getHttpHost() { return httpHost; }

    @JsonProperty
    public void setHttpHost(String pHttpHost) { this.httpHost = pHttpHost; }

    @JsonProperty
    public short getHttpPort() { return httpPort; }

    @JsonProperty
    public void setHttpPort(short pHttpPort) { this.httpPort = pHttpPort; }

    @JsonProperty
    public String getDefaultName() {
        return defaultName;
    }

    @JsonProperty
    public void setDefaultName(String name) {
        this.defaultName = name;
    }

    @JsonProperty
    public short getTraceLevel() { return traceLevel; }

    @JsonProperty
    public void setTraceLevel(short pTraceLevel) { this.traceLevel = pTraceLevel; }

    @Valid
    @NotNull
    private JerseyClientConfiguration httpClient = new JerseyClientConfiguration();

    @JsonProperty("httpClient")
    public JerseyClientConfiguration getJerseyClientConfiguration() {
        return httpClient;
    }

    @JsonProperty("httpClient")
    public void setJerseyClientConfiguration(JerseyClientConfiguration conf) { this.httpClient = conf; }
}

