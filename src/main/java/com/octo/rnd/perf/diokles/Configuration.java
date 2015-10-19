package com.octo.rnd.perf.diokles;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.client.JerseyClientConfiguration;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@SuppressWarnings("unused")
public class Configuration extends io.dropwizard.Configuration {
    @NotEmpty
    private String template;

    @NotEmpty
    private String defaultName = "Stranger";

    @NotEmpty
    private String dbHost;

    @NotEmpty
    private String httpHost;

    @NotNull
    private short dbPort;

    @NotNull
    private short traceLevel;

    @JsonProperty
    public String getTemplate() {
        return template;
    }

    @JsonProperty
    public void setTemplate(String template) {
        this.template = template;
    }

    @JsonProperty
    public String getDbHost() {
        return dbHost;
    }

    @JsonProperty
    public void setDbHost(String dbHost) {
        this.dbHost = dbHost;
    }

    @JsonProperty
    public short getDbPort() { return dbPort; }

    @JsonProperty
    public void setDbPort(short dbPort) { this.dbPort = dbPort; }

    @JsonProperty
    public String getHttpHost() { return httpHost; }

    @JsonProperty
    public void setHttpHost(String httpHost) { this.httpHost = httpHost; }

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
    public void setTraceLevel(short traceLevel) { this.traceLevel = traceLevel; }

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

