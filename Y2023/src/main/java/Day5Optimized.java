import org.apache.commons.lang3.time.StopWatch;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class Day5Optimized {
    static volatile long lowestLocation = Long.MAX_VALUE;
    static Day5.SpecialMappingRange straightMappingRange;

    public static void main(String[] args) throws IOException, URISyntaxException,
            InterruptedException {
        URL url = Thread.currentThread().getContextClassLoader().getResource("Day5.txt");
        Path path = Paths.get(url.toURI());
        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);

        Map<Long, Long> seedNumbers = new HashMap<>();
        String[] seedsAndNameArray = lines.get(0).split(":");
        String[] seedsArray = seedsAndNameArray[1].trim().split(" ");
        for (int i = 0; i < seedsArray.length; i += 2) {
            seedNumbers.put(Long.parseLong(seedsArray[i]), Long.parseLong(seedsArray[i + 1]));
        }

        List<Mapping> mappingsChain = new ArrayList<>();
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.isBlank()) {
                continue;
            }
            if (!Character.isDigit(line.toCharArray()[0])) {
                Mapping mapping = new Mapping();
                while (true) {
                    i++;
                    try {
                        line = lines.get(i);
                    } catch (Exception e) {
                        break;
                    }
                    if (line.isBlank()) {
                        break;
                    }
                    String[] specialMappingArray = line.split(" ");
                    SpecialMappingRange mappingRange = new SpecialMappingRange(
                            Long.parseLong(specialMappingArray[1]),
                            Long.parseLong(specialMappingArray[0]),
                            Long.parseLong(specialMappingArray[2])
                    );
                    mapping.specialMappings.add(mappingRange);
                }
                mappingsChain.add(mapping);
            }
        }
        
        Executor executor = Executors.newWorkStealingPool();
        CountDownLatch latch = new CountDownLatch(10);
        for (Map.Entry<Long, Long> seedEntry : seedNumbers.entrySet()) {
            executor.execute(() -> {
                for (long i = seedEntry.getKey(); i < seedEntry.getKey() + seedEntry.getValue(); i++) {
                    long seedLocation = getLocation(i, mappingsChain);
                    if (lowestLocation > seedLocation) lowestLocation = seedLocation;
                }
                latch.countDown();
            });
        }
        latch.await();
        System.out.println(lowestLocation);
    }

    static long getLocation(long seedNumber, List<Mapping> mappingsChain) {
        long currentOutput = seedNumber;
        for (Mapping mapping : mappingsChain) {
            currentOutput = mapping.getDestination(currentOutput);
        }
        return currentOutput;
    }

    static Day5.SpecialMappingRange findStraightMappings(List<Mapping> mappingsChain) {
        Mapping mapping = mappingsChain.get(0);
        mapping.specialMappings.stream().sorted();
        for (SpecialMappingRange range : mapping.specialMappings) {

        }
        return null;
    }

    static class Mapping {
        Set<SpecialMappingRange> specialMappings = new HashSet<>();

        long getDestination(long source) {
            long destination = source;
            for (SpecialMappingRange specialMapping : specialMappings) {
                if (source >= specialMapping.sourceStart && source < specialMapping.getSourceEndExclusive()) {
                    destination = Math.abs(source - specialMapping.sourceStart) + specialMapping.destinationStart;
                    break;
                }
            }

            return destination;
        }
    }

    static class SpecialMappingRange implements Comparable<Day5.SpecialMappingRange> {
        long sourceStart;
        long destinationStart;
        long length;

        public SpecialMappingRange(long sourceStart, long destinationStart, long length) {
            this.sourceStart = sourceStart;
            this.destinationStart = destinationStart;
            this.length = length;
        }

        long getSourceEndExclusive() {
            return sourceStart + length;
        }

        @Override
        public int compareTo(Day5.SpecialMappingRange o) {
            return (int) (sourceStart - o.sourceStart);
        }
    }
}
