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
import java.util.concurrent.TimeUnit;

public class Day5 {

    public static void main(String[] args) throws IOException, URISyntaxException {
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


        long lowestLocation = Long.MAX_VALUE;
        for (Map.Entry<Long, Long> seedEntry : seedNumbers.entrySet()) {
            for (long i = seedEntry.getKey(); i < seedEntry.getKey() + seedEntry.getValue(); i++) {
//                StopWatch stopWatch = StopWatch.createStarted();
                long seedLocation = getLocation(i, mappingsChain);
//                stopWatch.stop();
//                System.out.println(stopWatch.getTime(TimeUnit.MICROSECONDS));
                if (lowestLocation > seedLocation) lowestLocation = seedLocation;
            }
        }
        System.out.println(lowestLocation);
    }

    static long getLocation(long seedNumber, List<Mapping> mappingsChain) {
        long currentOutput = seedNumber;
        for (Mapping mapping : mappingsChain) {
            currentOutput = mapping.getDestination(currentOutput);
        }
        return currentOutput;
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

    static class SpecialMappingRange {
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
    }
}
