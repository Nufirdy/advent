package kgnilov;

import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Day9 {

    public static void main(String[] args) throws IOException, URISyntaxException {
        URL url = Thread.currentThread().getContextClassLoader().getResource(resourceName());
        Path path = Paths.get(url.toURI());
        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);

        part1(lines);
        part2(lines);
    }

    static String resourceName() {
        return "Day9";
    }

    static void part1(List<String> lines) {
        DiskMap diskMap = new DiskMap(lines.get(0));
        diskMap.compactBlocks();
        System.out.println(diskMap.checksum());
    }

    static void part2(List<String> lines) {
        DiskMap diskMap = new DiskMap(lines.get(0));
        diskMap.compactFiles();
        System.out.println(diskMap.checksum());
    }

    static class DiskMap {
        int[] denseMap;
        int[] map;

        List<Pair<Integer, Integer>> freeSpaces;
        List<Pair<Integer, Integer>> files;

        public DiskMap(String stringDiskMap) {
            char[] chars = stringDiskMap.toCharArray();
            denseMap = new int[chars.length];

            int diskSize = 0;
            for (int i = 0; i < chars.length; i++) {
                char c = chars[i];
                int space = Character.digit(c, 10);
                denseMap[i] = space;
                diskSize += space;
            }

            map = new int[diskSize];
            int blockIndex = 0;
            for (int i = 0; i < denseMap.length; i++) {
                int space = denseMap[i];
                for (int j = blockIndex; blockIndex < j + space; blockIndex++) {
                    map[blockIndex] = i % 2 == 0 ? i / 2 : -1;
                }
            }

            calcFreeSpaces();
            calcFiles();
        }

        void compactBlocks() {
            for (int i = 0, j = map.length - 1; i < map.length; i++) {
                int block = map[i];
                if (block == -1) {
                    int lastBlock = map[j];
                    if (lastBlock != -1) {
                        map[i] = lastBlock;
                    } else {
                        i--;
                    }
                    map[j] = -2;
                    j--;
                } else if (block == -2) {
                    break;
                }
            }
        }

        void compactFiles() {
            for (int i = files.size() - 1; i >= 0; i--) {
                Pair<Integer, Integer> file = files.get(i);
                int fileSize = file.getRight() - file.getLeft();
                for (int j = 0; j < freeSpaces.size(); j++) {
                    Pair<Integer, Integer> freeSpace = freeSpaces.get(j);
                    int freeSpaceSize = freeSpace.getRight() - freeSpace.getLeft();
                    if (fileSize <= freeSpaceSize && freeSpace.getLeft() < file.getLeft()) {
                        moveFileTo(file, freeSpace);
                        calcFreeSpaces();
                        calcFiles();
                        i++;
                        break;
                    }
                }
            }
        }

        private void calcFreeSpaces() {
            List<Pair<Integer, Integer>> freeSpaces = new ArrayList<>();
            for (int i = 0; i < map.length; i++) {
                int block = map[i];
                if (block == -1) {
                    int left = i;
                    while (block == -1) {
                        if (++i == map.length) {
                            break;
                        }
                        block = map[i];
                    }
                    int right = i;
                    freeSpaces.add(Pair.of(left, right));
                }
            }
            this.freeSpaces = freeSpaces;
        }

        private void calcFiles() {
            List<Pair<Integer, Integer>> files = new ArrayList<>();
            for (int i = 0; i < map.length; i++) {
                int block = map[i];
                if (block != -1) {
                    int left = i;
                    int nextBlock = block;
                    while (nextBlock == block) {
                        if (i + 1 == map.length) {
                            break;
                        }
                        nextBlock = map[i + 1];
                        if (block != nextBlock) {
                            break;
                        }
                        i++;
                    }
                    int right = i + 1;
                    files.add(Pair.of(left, right));
                }
            }
            this.files = files;
        }

        private Pair<Integer, Integer> moveFileTo(Pair<Integer, Integer> file, Pair<Integer, Integer> freeSpace) {
            for (int i = file.getLeft(), j = freeSpace.getLeft(); i < file.getRight(); i++, j++) {
                map[j] = map[i];
                map[i] = -1;
            }
            return null;
        }

        long checksum() {
            long checksum = 0;
            for (int i = 0; i < map.length; i++) {
                int block = map[i];
                if (block < 0) {
                    continue;
                }
                checksum += i * block;
            }

            return checksum;
        }
    }
}
