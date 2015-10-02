package com.octo.rnd.perf.microservices;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.client.JerseyClientConfiguration;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@SuppressWarnings("unused")
public class Configuration extends io.dropwizard.Configuration {
    @NotEmpty
    private String template;

    @NotEmpty
    private String defaultName = "Stranger";

    @NotEmpty
    @Pattern(regexp="([\\da-z\\.-]+)")
    private String dbHost;

    @NotNull
    private short dbPort;

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
    public String getDefaultName() {
        return defaultName;
    }

    @JsonProperty
    public void setDefaultName(String name) {
        this.defaultName = name;
    }

    @Valid
    @NotNull
    private JerseyClientConfiguration httpClient = new JerseyClientConfiguration();

    @JsonProperty("httpClient")
    public JerseyClientConfiguration getJerseyClientConfiguration() {
        return httpClient;
    }
}
