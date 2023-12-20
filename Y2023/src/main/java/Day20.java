import org.apache.commons.math3.util.ArithmeticUtils;

import java.util.*;

public class Day20 {
    static long buttonPresses = 0;
    static long highSent = 0;
    static long lowSent = 0;
    static boolean STOP_THE_MACHINES = false;

    public static void main(String[] args) {
        List<String> lines = InputUtils.getFromResource("Day20.txt");

//        part1(lines);
        part2(lines);
    }

    private static void part2(List<String> lines) {
        Queue<Module> processingOrder = new ArrayDeque<>();

        Map<String, List<String>> moduleAndReceivers = new HashMap<>();
        Map<String, Module> modules = new HashMap<>();
        for (String line : lines) {
            String[] senderAndReceivers = line.split(" -> ");

            String moduleName;
            Module module;
            if (senderAndReceivers[0].startsWith("%")) {
                moduleName = senderAndReceivers[0].substring(1);
                module = new FlipFlop(processingOrder, moduleName);
            } else if (senderAndReceivers[0].startsWith("&")) {
                moduleName = senderAndReceivers[0].substring(1);
                module = new Conjunction(processingOrder, moduleName);
            } else {
                moduleName = senderAndReceivers[0];
                module = new Broadcaster(processingOrder, moduleName);
            }
            modules.put(module.name, module);

            String[] receiverNames = senderAndReceivers[1].split(", ");
            moduleAndReceivers.put(moduleName, Arrays.asList(receiverNames));
        }

        for (Map.Entry<String, List<String>> entry : moduleAndReceivers.entrySet()) {
            Module module = modules.get(entry.getKey());
            module.receivers = entry.getValue().stream()
                    .map(key -> {
                        Module receiver = modules.get(key);
                        if (receiver == null) {
                            receiver = new Dummy(processingOrder, key);
                        }
                        return receiver;
                    })
                    .peek(receiver -> {
                        if (receiver instanceof Conjunction conjunction) {
                            conjunction.addSender(module);
                        }
                    })
                    .toList();
        }

        Broadcaster first = (Broadcaster) modules.get("broadcaster");
        List<List<Module>> cycleBlocks = new ArrayList<>();
        for (Module receiver : first.receivers) {
            List<Module> cycleBlock = new ArrayList<>();
            cycleBlock.add(receiver);
            Module nextModule = getNext(receiver);
            while (!(nextModule instanceof Conjunction)) {
                cycleBlock.add(nextModule);
                nextModule = getNext(nextModule);
            }
            cycleBlock.add(nextModule);
            cycleBlocks.add(cycleBlock);
        }


        boolean cycle1 = true;
        boolean cycle2 = true;
        boolean cycle3 = true;
        boolean cycle4 = true;
        long cycleLength1 = 0;
        long cycleLength2 = 0;
        long cycleLength3 = 0;
        long cycleLength4 = 0;
        while (cycle1 || cycle2 || cycle3 || cycle4) {
            if (cycle1) {
                cycleLength1++;
            }
            if (cycle2) {
                cycleLength2++;
            }
            if (cycle3) {
                cycleLength3++;
            }
            if (cycle4) {
                cycleLength4++;
            }
            first.queuePulse(false, null);
            processingOrder.add(first);
            while (!processingOrder.isEmpty()) {
                Module module = processingOrder.poll();
                module.receivePulse();
            }

            for (int i = 0; i < cycleBlocks.size(); i++) {
                List<Module> cycleBlock = cycleBlocks.get(i);
                boolean allModulesAreDefault = true;
                for (Module module : cycleBlock) {
                    boolean moduleIsDefault = false;
                    if (module instanceof Conjunction that) {
                        moduleIsDefault = that.isDefaultState();
                    } else if (module instanceof FlipFlop that) {
                        moduleIsDefault = !that.state;
                    }
                    if (!moduleIsDefault) {
                        allModulesAreDefault = false;
                        break;
                    }
                }

                if (allModulesAreDefault) {
                    if (i == 0) {
                        cycle1 = false;
                    } else if (i == 1) {
                        cycle2 = false;
                    } else if (i == 2) {
                        cycle3 = false;
                    } else if (i == 3) {
                        cycle4 = false;
                    }
                }
            }
        }

        long lcm = ArithmeticUtils.lcm(ArithmeticUtils.lcm(cycleLength1, cycleLength2),
                ArithmeticUtils.lcm(cycleLength3, cycleLength4));
        System.out.println("Button presses: " + lcm);
    }

    private static Module getNext(Module receiver) {
        for (Module module : receiver.receivers) {
            if (module instanceof Conjunction) {
                continue;
            }
            return module;
        }
        return receiver.receivers.get(0);
    }

    private static List<Module> countReceiversCycle(Module receiver) {
        List<Module> allModules = new ArrayList<>();

        if (receiver instanceof Conjunction) {
            allModules.add(receiver);
            return allModules;
        }


        return allModules;
    }

    static void part1(List<String> lines) {
        Queue<Module> processingOrder = new ArrayDeque<>();

        Map<String, List<String>> moduleAndReceivers = new HashMap<>();
        Map<String, Module> modules = new HashMap<>();
        for (String line : lines) {
            String[] senderAndReceivers = line.split(" -> ");

            String moduleName;
            Module module;
            if (senderAndReceivers[0].startsWith("%")) {
                moduleName = senderAndReceivers[0].substring(1);
                module = new FlipFlop(processingOrder, moduleName);
            } else if (senderAndReceivers[0].startsWith("&")) {
                moduleName = senderAndReceivers[0].substring(1);
                module = new Conjunction(processingOrder, moduleName);
            } else {
                moduleName = senderAndReceivers[0];
                module = new Broadcaster(processingOrder, moduleName);
            }
            modules.put(module.name, module);

            String[] receiverNames = senderAndReceivers[1].split(", ");
            moduleAndReceivers.put(moduleName, Arrays.asList(receiverNames));
        }

        for (Map.Entry<String, List<String>> entry : moduleAndReceivers.entrySet()) {
            Module module = modules.get(entry.getKey());
            module.receivers = entry.getValue().stream()
                    .map(key -> {
                        Module receiver = modules.get(key);
                        if (receiver == null) {
                            receiver = new Dummy(processingOrder, key);
                        }
                        return receiver;
                    })
                    .peek(receiver -> {
                        if (receiver instanceof Conjunction conjunction) {
                            conjunction.addSender(module);
                        }
                    })
                    .toList();
        }

        Broadcaster first = (Broadcaster) modules.get("broadcaster");
        for (int i = 0; i < 1000; i++) {
            first.queuePulse(false, null);
            lowSent++;
            processingOrder.add(first);
            while (!processingOrder.isEmpty()) {
                Module module = processingOrder.poll();
                module.receivePulse();
            }
        }

        System.out.println(highSent + " * " + lowSent + " = " + highSent * lowSent);
    }

    static abstract class Module {
        public String name;
        protected Queue<Boolean> pulseQueue = new ArrayDeque<>();
        protected Queue<Module> processingOrder;
        protected List<Module> receivers = new ArrayList<>();

        public Module(Queue<Module> processingOrder, String name) {
            this.name = name;
            this.processingOrder = processingOrder;
        }

        public void queuePulse(boolean pulse, Module sender) {
            pulseQueue.add(pulse);
        }

        public abstract void receivePulse();

        protected void sendPulse(boolean pulse) {
            for (Module receiver : receivers) {
                receiver.queuePulse(pulse, this);
                processingOrder.add(receiver);
//                if (pulse) { //uncomment for part 1
//                    highSent++;
//                } else {
//                    lowSent++;
//                }
            }
        }

        @Override
        public String toString() {
            return "Module{" +
                    "name='" + name + '\'' +
                    '}';
        }
    }

    static class Broadcaster extends Module {
        public Broadcaster(Queue<Module> processingOrder, String name) {
            super(processingOrder, name);
        }

        @Override
        public void receivePulse() {
            sendPulse(pulseQueue.poll());
        }
    }

    static class FlipFlop extends Module {
        private boolean state = false;

        public FlipFlop(Queue<Module> processingOrder, String name) {
            super(processingOrder, name);
        }

        @Override
        public void receivePulse() {
            boolean pulse = pulseQueue.poll();
            if (!pulse) {
                state = !state;
                sendPulse(state);
            }
        }
    }

    static class Conjunction extends Module {
        private Map<String, Boolean> lastReceivedFrom = new HashMap<>();

        public Conjunction(Queue<Module> processingOrder, String name) {
            super(processingOrder, name);
        }

        public void addSender(Module sender) {
            lastReceivedFrom.put(sender.name, false);
        }

        @Override
        public void queuePulse(boolean pulse, Module sender) {
            pulseQueue.add(pulse);
            lastReceivedFrom.put(sender.name, pulse);
        }

        @Override
        public void receivePulse() {
            pulseQueue.poll();
            boolean pulseToSend = false;
            for (Boolean value : lastReceivedFrom.values()) {
                if (!value) {
                    pulseToSend = true;
                    break;
                }
            }
            sendPulse(pulseToSend);
        }

        public boolean isDefaultState() {
            for (Boolean value : lastReceivedFrom.values()) {
                if (value) {
                    return false;
                }
            }
            return true;
        }
    }

    static class Dummy extends Module {

        public Dummy(Queue<Module> processingOrder, String name) {
            super(processingOrder, name);
        }

        @Override
        public void receivePulse() {
            boolean pulse = pulseQueue.poll();
            if (!pulse) {
                STOP_THE_MACHINES = true;
            }
        }
    }


}
