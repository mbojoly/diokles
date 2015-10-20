package com.octo.rnd.perf.diokles.resources;

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
        //In order to avoid copy and paste the same description
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
