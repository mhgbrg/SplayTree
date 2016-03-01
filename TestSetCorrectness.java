import java.util.Random;
import java.util.Set;
import java.util.HashSet;

public class TestSetCorrectness {
    public static void main(String[] args) {
        int n1 = 0, n2 = 0, n3 = 0;
        try {
            n1 = Integer.parseInt(args[0]);
            n2 = Integer.parseInt(args[1]);
            n3 = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            System.out.println("Illegal arguments given to program");
            return;
        }

        Random random = new Random();

        // how many resets?
        for (int i = 0; i < n1; i++) {
            SimpleSet<Integer> testSet = new SplayTreeSet<>();
            Set<Integer> javaSet = new HashSet<>();

            // how many operations?
            for (int j = 0; j < n2; j++) {
                int operation = random.nextInt(4);
                int value = random.nextInt(n3);
                switch (operation) {
                    case 0: // size
                        System.out.println("Operation: size");
                        if (testSet.size() != javaSet.size()) {
                            System.out.println("Error! size(): reset " + i + ", operation " + j);
                            return;
                        }
                        break;
                    case 1: // add
                        System.out.println("Operation: add(" + value + ")");
                        if (testSet.add(value) != javaSet.add(value)) {
                            System.out.println("Error! add(" + value + "): reset " + i + ", operation " + j);
                            return;
                        }
                        break;
                    case 2: // remove
                        System.out.println("Operation: remove(" + value + ")");
                        if (testSet.remove(value) != javaSet.remove(value)) {
                            System.out.println("Error! remove(" + value + "): reset " + i + ", operation " + j);
                            return;
                        }
                        break;
                    case 3: // contains
                        System.out.println("Operation: contains(" + value + ")");
                        if (testSet.contains(value) != javaSet.contains(value)) {
                            System.out.println("Error! contains(" + value + "): reset " + i + ", operation " + j);
                            return;
                        }
                        break;
                }
                System.out.println(testSet);
            }
        }

        System.out.println("All tests passed!");
    }
}
