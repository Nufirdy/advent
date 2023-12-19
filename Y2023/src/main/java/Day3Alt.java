import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class Day3Alt {

    public static void main(String[] args) throws IOException, URISyntaxException {
        URL url = Thread.currentThread().getContextClassLoader().getResource("Day3.txt");
        Path path = Paths.get(url.toURI());
        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);

        int partNumbersSum = 0;
        long gearRatiosSum = 0;
        Schematic schematic = new Schematic(lines);
        for (SchematicObject schematicObject : schematic.allSchematicObjects) {
            if (schematicObject.isPartNumber()) {
                partNumbersSum += schematicObject.getAsNumber();
            }
            if (schematicObject.isGear()) {
                gearRatiosSum += schematicObject.getGearRatio();
            }
        }
        System.out.println(partNumbersSum);
        System.out.println(gearRatiosSum);
    }

    static class Schematic {
        SchematicObject[][] objectsArray;
        Set<SchematicObject> allSchematicObjects = new HashSet<>();

        public Schematic(List<String> lines) {
            objectsArray = new SchematicObject[lines.size()][lines.get(0).length()];
            for (int row = 0; row < lines.size(); row++) {
                char[] chars = lines.get(row).toCharArray();
                StringBuilder currentNumber = new StringBuilder();
                char lastChar = '.';
                for (int column = 0; column < chars.length; column++) {
                    char c = chars[column];
                    boolean encounteredSymbol = false;
                    if (!Character.isDigit(c) && c != '.') {
                        SchematicObject schematicObject = new SchematicObject();
                        schematicObject.row = row;
                        schematicObject.column = column;
                        schematicObject.value = String.valueOf(c);
                        schematicObject.length = 1;
                        putSchematicObject(schematicObject);
                        encounteredSymbol = true;
                    }

                    if (Character.isDigit(c)) {
                        currentNumber.append(c);
                    }

                    if ((Character.isDigit(lastChar) && encounteredSymbol)
                            || (Character.isDigit(lastChar) && c == '.')
                            || (Character.isDigit(c) && chars.length - 1 == column)) {
                        String number = currentNumber.toString();
                        currentNumber = new StringBuilder();

                        SchematicObject schematicObject = new SchematicObject();
                        schematicObject.row = row;
                        schematicObject.column = column - number.length();
                        schematicObject.value = number;
                        schematicObject.length = number.length();
                        putSchematicObject(schematicObject);
                    }

                    lastChar = c;
                }
            }

            for (SchematicObject schematicObject : allSchematicObjects) {
                for (int row = schematicObject.row - 1; row <= schematicObject.row + 1; row++) {
                    for (int col = schematicObject.column - 1; col <= schematicObject.column + schematicObject.length; col++) {
                        if (row == schematicObject.row && col >= schematicObject.column && col < schematicObject.column + schematicObject.length) {
                            continue;
                        }
                        SchematicObject fromArray;
                        try {
                            fromArray = objectsArray[row][col];
                        } catch (Exception e) {
                            continue;
                        }
                        if (fromArray != null) {
                            schematicObject.adjacentObjects.add(fromArray);
                        }
                    }
                }
            }
        }

        SchematicObject getSchematicObject(int row, int column) {
            return objectsArray[row][column];
        }

        void putSchematicObject(SchematicObject schematicObject) {
            for (int i = schematicObject.column; i < schematicObject.column + schematicObject.length; i++) {
                objectsArray[schematicObject.row][i] = schematicObject;
            }
            allSchematicObjects.add(schematicObject);
        }
    }

    static class SchematicObject {
        int row;
        int column;
        int length;
        String value;
        Set<SchematicObject> adjacentObjects = new HashSet<>();

        boolean isPartNumber() {
            if (!isNumber()) {
                return false;
            }

            for (SchematicObject adjacentObject : adjacentObjects) {
                if (!adjacentObject.isNumber()) {
                    return true;
                }
            }

            return false;
        }

        long getGearRatio() {
            if (!isGear()) {
                throw new RuntimeException("Not a gear");
            }

            long gearRatio = 1;
            for (SchematicObject adjacentObject : adjacentObjects) {
                if (adjacentObject.isNumber()) {
                    gearRatio = gearRatio * adjacentObject.getAsNumber();
                }
            }
            return gearRatio;
        }

        boolean isGear() {
            if (!value.equals("*")) {
                return false;
            }

            int adjacentNumbers = 0;
            for (SchematicObject adjacentObject : adjacentObjects) {
                if (adjacentObject.isNumber()) {
                    adjacentNumbers++;
                }
            }

            return adjacentNumbers == 2;
        }

        long getAsNumber() {
            return Long.parseLong(value);
        }

        boolean isNumber() {
            try {
                Integer.parseInt(value);
            } catch (NumberFormatException e) {
                return false;
            }
            return true;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof SchematicObject that)) return false;
            return row == that.row && column == that.column;
        }

        @Override
        public int hashCode() {
            return Objects.hash(row, column);
        }

        @Override
        public String toString() {
            return "SchematicObject{" +
                    "value='" + value + '\'' +
                    '}';
        }
    }
}
