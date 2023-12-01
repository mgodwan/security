package org.opensearch.security.benchmarks;

import org.openjdk.jmh.annotations.*;
import org.opensearch.security.support.Base64Helper;

import java.io.IOException;
import java.io.Serializable;
import java.util.AbstractMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(3)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
public class HeaderSerdeBench {

    private Map<String, String> data;

    @Setup
    public void createObject() {
        String commonString = generateRandomChars(DEFAULT_CANDIDATE_CHARS, 100); // Parametrize length
        // Parametrize map size
        data = IntStream.range(0, 1000).mapToObj(i -> new AbstractMap.SimpleEntry<>(generateRandomChars(DEFAULT_CANDIDATE_CHARS, 50), commonString))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Benchmark
    public String runJdkSerialization() {
        return Base64Helper.serializeObject((Serializable) data, true);
    }

    @Benchmark
    public String runCustomSerialization() {
        return Base64Helper.serializeObject((Serializable) data, false);
    }

    private static final String DEFAULT_CANDIDATE_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
    private static String generateRandomChars(String sourceSet, int length) {
        StringBuilder builder = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            builder.append(sourceSet.charAt(random.nextInt(sourceSet.length())));
        }
        return builder.toString();
    }
}
