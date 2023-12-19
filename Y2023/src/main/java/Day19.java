import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public class Day19 {
    static final String ACCEPTED = "A";
    static final String REJECTED = "R";

    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException {
        List<String> lines = InputUtils.getFromResource("Day19-test1.txt");

        Map<String, Workflow> workflows = new HashMap<>();
        List<Part> parts = new ArrayList<>();

        boolean parseParts = false;
        for (String line : lines) {
            if (line.isBlank()) {
                parseParts = true;
                continue;
            }

            if (parseParts) {
                Part part = new Part(line);
                parts.add(part);
            } else {
                Workflow workflow = new Workflow(line);
                workflows.put(workflow.name, workflow);
            }
        }

        part1(workflows, parts);
        part2(lines);
    }

    private static void part2(List<String> lines) throws NoSuchFieldException,
            IllegalAccessException {
        List<String> filteredLines = new ArrayList<>();
        for (String line : lines) {
            if (line.isBlank()) {
                break;
            }
            filteredLines.add(line);
        }
        Zalupa sorter = new Zalupa(filteredLines);
        System.out.println(sorter.countPossible());
    }


    static class Zalupa {
        static final Rule acceptRule = new Rule("", 0, false, ACCEPTED);
        static final Rule rejectRule = new Rule("", 0, false, REJECTED);

        Map<String, List<Rule>> workflows = new HashMap<>();
        Rule start;

        public Zalupa(List<String> lines) {
            for (String line : lines) {
                ArrayList<Rule> rules = new ArrayList<>();

                String[] nameAndRules = line.split("\\{");
                String[] rulesSplit = StringUtils.strip(nameAndRules[1], "}").split(",");

                for (String ruleStr : rulesSplit) {
                    String ratingType;
                    int threshold;
                    boolean moreThan;
                    String destination;

                    if (ruleStr.contains(":")) {
                        String[] split = ruleStr.split(":");
                        destination = split[1];

                        BiFunction<Integer, Integer, Boolean> compare;
                        String[] typeAndValue;
                        if (split[0].contains("<")) {
                            moreThan = false;
                            typeAndValue = split[0].split("<");
                        } else {
                            moreThan = true;
                            typeAndValue = split[0].split(">");
                        }

                        ratingType = typeAndValue[0];
                        threshold = Integer.parseInt(typeAndValue[1]);

                    } else {
                        ratingType = "";
                        threshold = 0;
                        moreThan = false;
                        destination = ruleStr;
                    }

                    Rule rule = new Rule(ratingType, threshold, moreThan, destination);
                    rules.add(rule);
                }
                workflows.put(nameAndRules[0], rules);
            }

            for (Map.Entry<String, List<Rule>> workflowEntry : workflows.entrySet()) {
                List<Rule> rules = workflowEntry.getValue();
                for (int i = 0; i < rules.size(); i++) {
                    Rule rule = rules.get(i);
                    if (!rule.ratingType.isBlank()) {
                        Rule left = rules.get(i + 1);
                        if (left.ratingType.isBlank()) {
                            if (left.destination.equals(REJECTED)) {
                                left = rejectRule;
                            } else if (left.destination.equals(ACCEPTED)) {
                                left = acceptRule;
                            } else {
                                left = workflows.get(left.destination).get(0);
                            }
                        }
                        rule.toFalse = left;


                        if (rule.destination.equals(ACCEPTED)) {
                            rule.toTrue = acceptRule;
                        } else if (rule.destination.equals(REJECTED)) {
                            rule.toTrue = rejectRule;
                        } else {
                            rule.toTrue = workflows.get(rule.destination).get(0);
                        }
                    }
                }
            }

            start = workflows.get("in").get(0);
        }

        long countPossible() throws NoSuchFieldException, IllegalAccessException {
            Range defaultPart = new Range();
            return count(defaultPart, start);
        }

        long count(Range range, Rule rule) throws NoSuchFieldException, IllegalAccessException {
            if (rule == rejectRule) {
                return 0;
            }
            if (rule == acceptRule) {
                return range.getProduct();
            }

            long result = 0;
            Field lowerLimit = null;
            Field upperLimit = null;
            switch (rule.ratingType) {
                case "x" -> {
                    lowerLimit = Range.class.getDeclaredField("xcoolFrom");
                    upperLimit = Range.class.getDeclaredField("xcoolTo");
                }
                case "m" -> {
                    lowerLimit = Range.class.getDeclaredField("musicalFrom");
                    upperLimit = Range.class.getDeclaredField("musicalTo");
                }
                case "a" -> {
                    lowerLimit = Range.class.getDeclaredField("aerodynamicFrom");
                    upperLimit = Range.class.getDeclaredField("aerodynamicTo");
                }
                case "s" -> {
                    lowerLimit = Range.class.getDeclaredField("shinyFrom");
                    upperLimit = Range.class.getDeclaredField("shinyTo");
                }
            }

            if (rule.moreThan) {
                if (lowerLimit.getInt(range) < rule.threshold && upperLimit.getInt(range) > rule.threshold) {
                    Range toTrue = new Range(range);
                    lowerLimit.setInt(toTrue, rule.threshold + 1);

                    Range toFalse = new Range(range);
                    upperLimit.setInt(toFalse, rule.threshold + 1);

                    result += count(toTrue, rule.toTrue);
                    result += count(toFalse, rule.toFalse);
                } else if (lowerLimit.getInt(range) >= rule.threshold && upperLimit.getInt(range) > rule.threshold) {
                    Range toTrue = new Range(range);
                    result += count(toTrue, rule.toTrue);
                }
            } else {
                if (lowerLimit.getInt(range) < rule.threshold && upperLimit.getInt(range) > rule.threshold) {
                    Range toTrue = new Range(range);
                    upperLimit.setInt(toTrue, rule.threshold);

                    Range toFalse = new Range(range);
                    lowerLimit.setInt(toFalse, rule.threshold);

                    result += count(toTrue, rule.toTrue);
                    result += count(toFalse, rule.toFalse);
                } else if (upperLimit.getInt(range) <= rule.threshold && lowerLimit.getInt(range) < rule.threshold) {
                    Range toTrue = new Range(range);
                    result += count(toTrue, rule.toTrue);
                }
            }


            return result;
        }
    }

    static class Rule {
        String ratingType;
        int threshold;
        boolean moreThan;
        String destination;

        Rule toFalse;
        Rule toTrue;

        public Rule(String ratingType, int threshold, boolean moreThan, String destination) {
            this.ratingType = ratingType;
            this.threshold = threshold;
            this.moreThan = moreThan;
            this.destination = destination;
        }

        @Override
        public String toString() {
            return "Rule{" +
                    "ratingType='" + ratingType + '\'' +
                    ", threshold=" + threshold +
                    ", moreThan=" + moreThan +
                    ", destination='" + destination + '\'' +
                    '}';
        }
    }

    static class Range {
        int xcoolFrom = 1;
        int xcoolTo = 4001;

        int musicalFrom = 1;
        int musicalTo = 4001;

        int aerodynamicFrom = 1;
        int aerodynamicTo = 4001;

        int shinyFrom = 1;
        int shinyTo = 4001;

        public Range() {
        }

        public Range(Range range) {
            xcoolFrom = range.xcoolFrom;
            xcoolTo = range.xcoolTo;

            musicalFrom = range.musicalFrom;
            musicalTo = range.musicalTo;

            aerodynamicFrom = range.aerodynamicFrom;
            aerodynamicTo = range.aerodynamicTo;

            shinyFrom = range.shinyFrom;
            shinyTo = range.shinyTo;
        }

        public long getProduct() {
            return (long) (xcoolTo - xcoolFrom) * (musicalTo - musicalFrom) * (aerodynamicTo - aerodynamicFrom) * (shinyTo - shinyFrom);
        }
    }

    private static void part1(Map<String, Workflow> workflows, List<Part> parts) {
        int allAcceptedRatings = 0;
        Workflow start = workflows.get("in");
        for (Part part : parts) {
            String result = start.applyRules(part);
            while (true) {
                if (result.equals(ACCEPTED)) {
                    allAcceptedRatings += part.ratingsSum();
                    break;
                }
                if (result.equals(REJECTED)) {
                    break;
                }
                result = workflows.get(result).applyRules(part);
            }
        }

        System.out.println(allAcceptedRatings);
    }

    static class Workflow {

        private static final String TO_NEXT_RULE = "N";
        private static final Function<Part, Integer> XCOOL = part -> Math.toIntExact(part.xcool);
        private static final Function<Part, Integer> MUSICAL = part -> part.musical;
        private static final Function<Part, Integer> AERODYNAMIC = part -> part.aerodynamic;
        private static final Function<Part, Integer> SHINY = part -> part.shiny;
        private static final BiFunction<Integer, Integer, Boolean> LESS_THAN =
                (integer, integer2) -> integer < integer2;
        private static final BiFunction<Integer, Integer, Boolean> MORE_THAN =
                (integer, integer2) -> integer > integer2;

        private static final BiFunction<String, Boolean, String> SEND_TO =
                (s, aBoolean) -> aBoolean ? s : TO_NEXT_RULE;

        String name;
        List<Function<Part, String>> ruleFunctions = new ArrayList<>();

        public Workflow(String workflowStr) {
            String[] nameAndRules = workflowStr.split("\\{");
            name = nameAndRules[0];
            String[] rulesSplit = StringUtils.strip(nameAndRules[1], "}").split(",");

            for (String ruleStr : rulesSplit) {
                String sendTo;
                Function<Part, String> rule;

                if (ruleStr.contains(":")) {
                    String[] split = ruleStr.split(":");
                    sendTo = split[1];

                    BiFunction<Integer, Integer, Boolean> compare;
                    String[] typeAndValue;
                    if (split[0].contains("<")) {
                        compare = LESS_THAN;
                        typeAndValue = split[0].split("<");
                    } else {
                        compare = MORE_THAN;
                        typeAndValue = split[0].split(">");
                    }

                    int compareValue = Integer.parseInt(typeAndValue[1]);
                    Function<Part, Integer> rating = null;
                    switch (typeAndValue[0]) {
                        case "x" -> rating = XCOOL;
                        case "m" -> rating = MUSICAL;
                        case "a" -> rating = AERODYNAMIC;
                        case "s" -> rating = SHINY;
                    }


                    Function<Part, Integer> finalCategory = rating;
                    rule = part -> SEND_TO.apply(sendTo, compare.apply(finalCategory.apply(part),
                            compareValue));
                } else {
                    sendTo = ruleStr;
                    rule = part -> SEND_TO.apply(sendTo, true);
                }

                ruleFunctions.add(rule);
            }
        }

        public String applyRules(Part part) {
            for (Function<Part, String> rule : ruleFunctions) {
                String sendTo = rule.apply(part);
                if (!sendTo.equals(TO_NEXT_RULE)) {
                    return sendTo;
                }
            }
            throw new IllegalStateException();
        }

    }

    static class Part {
        long xcool;
        int musical;
        int aerodynamic;
        int shiny;

        public Part(String partStr) {
            String stripped = StringUtils.strip(partStr, "{}");
            String[] split = stripped.split(",");
            for (String rating : split) {
                String[] typeAndValue = rating.split("=");
                switch (typeAndValue[0]) {
                    case "x" -> xcool = Integer.parseInt(typeAndValue[1]);
                    case "m" -> musical = Integer.parseInt(typeAndValue[1]);
                    case "a" -> aerodynamic = Integer.parseInt(typeAndValue[1]);
                    case "s" -> shiny = Integer.parseInt(typeAndValue[1]);
                }
            }
        }

        public long ratingsSum() {
            return xcool + musical + aerodynamic + shiny;
        }
    }
}
