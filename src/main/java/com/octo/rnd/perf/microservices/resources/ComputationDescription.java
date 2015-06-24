package com.octo.rnd.perf.microservices.resources;


import com.fasterxml.jackson.annotation.JsonProperty;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;
import java.util.LinkedList;
import java.util.List;

@SuppressWarnings("unused")
@Consumes(MediaType.APPLICATION_JSON)
public class ComputationDescription {
    @Consumes(MediaType.APPLICATION_JSON)
    static class ServiceCall {
        private ComputationDescription computationDescription;
        private int callsNumber;

        public ServiceCall() {
            //Jackson deserialization
        }

        public ServiceCall(ComputationDescription computationDescription, int callsNumber) {
            this.computationDescription = computationDescription;
            this.callsNumber = callsNumber;
        }

        @JsonProperty
        public ComputationDescription getComputationDescription() {
            return computationDescription;
        }

        @JsonProperty
        public int getCallsNumber() {
            return callsNumber;
        }

        @Override
        public String toString() {
            return "ServiceCall{" +
                    "computationDescription=" + computationDescription +
                    ", callsNumber=" + callsNumber +
                    '}';
        }
    }
    private int cpuIntensiveComputationsDuration;
    private int databaseCallsNumber;
    private int databaseCallDuration;
    private List<ServiceCall> serviceCalls = new LinkedList<ServiceCall>();

    public ComputationDescription(int cpuIntensiveComputationsDuration, int databaseCallsNumber, int databaseCallDuration, int serviceCallsNumber, List<ServiceCall> serviceCalls) {
        this.cpuIntensiveComputationsDuration = cpuIntensiveComputationsDuration;
        this.databaseCallsNumber = databaseCallsNumber;
        this.databaseCallDuration = databaseCallDuration;
        this.serviceCalls = serviceCalls;
    }

    public ComputationDescription() {
        // Jackson deserialization
    }

    @JsonProperty
    public int getCpuIntensiveComputationsDuration() {
        return cpuIntensiveComputationsDuration;
    }

    @JsonProperty
    public int getDatabaseCallsNumber() {
        return databaseCallsNumber;
    }

    @JsonProperty
    public int getDatabaseCallDuration() {
        return databaseCallDuration;
    }

    @JsonProperty
    public List<ServiceCall> getServiceCalls() {
        return serviceCalls;
    }

    @Override
    public String toString() {
        return "ComputationDescription{" +
                "cpuIntensiveComputationsDuration=" + cpuIntensiveComputationsDuration +
                ", databaseCallsNumber=" + databaseCallsNumber +
                ", databaseCallDuration=" + databaseCallDuration +
                ", serviceCalls=" + serviceCalls +
                '}';
    }
}
