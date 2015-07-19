package com.octo.rnd.perf.microservices.resources;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.function.Function;


public class Helper {
    final static Logger logger = LoggerFactory.getLogger(Helper.class);

    public static class MeasuredTime {
        Optional<Long> InnerTimeMillis; // Time given returned by the operationToMeasure in ms
        double PercentError; //Error between input time and measured time

        public MeasuredTime(long percentError) {
            PercentError = percentError;
        }

        public MeasuredTime(Optional<Long> innerTimeMillis, double percentError) {
            InnerTimeMillis = innerTimeMillis;
            PercentError = percentError;
        }
    }

    public static MeasuredTime measureTime(Function<Long, Optional<Long>> operationToMeasure, final long UnitInputTime, int nbOfCalls) {
        final long totalTime = UnitInputTime * nbOfCalls;
        final long start = System.nanoTime();
        final Optional<Long> result = operationToMeasure.apply(UnitInputTime);
        final long end = System.nanoTime();
        final long elapse = (end - start) / 1000000; //Convert ns to ms
        logger.debug("Elapse is {} ms", elapse);
        final double error = (double) (Math.abs(totalTime - elapse)) / totalTime;
        logger.debug("Error between input and measure is {}", error);
        return new MeasuredTime(result, error);
    }
}
