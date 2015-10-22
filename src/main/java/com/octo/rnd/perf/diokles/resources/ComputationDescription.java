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

/**
 * JSON computation description
 */
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

        public ServiceCall(ComputationDescription pComputationDescription, int pCallsNumber) {
            this.computationDescription = pComputationDescription;
            this.callsNumber = pCallsNumber;
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

    /**
     *
     * @param pCpuIntensiveComputationsDuration in ms.
     * @param pDatabaseCallsNumber nb. of calls
     * @param pDatabaseCallDuration in ms.
     * @param pServiceCallsNumber nb. of calls
     * @param pServiceCalls calls descriptions
     */
    public ComputationDescription(int pCpuIntensiveComputationsDuration, int pDatabaseCallsNumber, int pDatabaseCallDuration, int pServiceCallsNumber, List<ServiceCall> pServiceCalls) {
        this.cpuIntensiveComputationsDuration = pCpuIntensiveComputationsDuration;
        this.databaseCallsNumber = pDatabaseCallsNumber;
        this.databaseCallDuration = pDatabaseCallDuration;
        this.serviceCalls = pServiceCalls;
    }

    /**
     * Jackson deserialization
     */
    public ComputationDescription() {
        // Jackson deserialization
    }

    /**
     * Getter
     * @return getter
     */
    @JsonProperty
    public int getCpuIntensiveComputationsDuration() {
        return cpuIntensiveComputationsDuration;
    }

    /**
     * Getter
     * @return getter
     */
    @JsonProperty
    public int getDatabaseCallsNumber() {
        return databaseCallsNumber;
    }

    /**
     * Getter
     * @return getter
     */
    @JsonProperty
    public int getDatabaseCallDuration() {
        return databaseCallDuration;
    }

    /**
     * Getter
     * @return getter
     */
    @JsonProperty
    public List<ServiceCall> getServiceCalls() {
        return serviceCalls;
    }

    /**
     * To String
     * @return string representation
     */
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
