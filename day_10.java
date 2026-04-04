/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package aoc2025;

import java.io.IOException;
import static java.lang.Integer.min;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static java.util.stream.Collectors.toCollection;
import java.util.stream.IntStream;
import org.apache.commons.math4.legacy.optim.linear.LinearConstraint;
import org.apache.commons.math4.legacy.optim.linear.LinearConstraintSet;
import org.apache.commons.math4.legacy.optim.linear.LinearObjectiveFunction;
import org.apache.commons.math4.legacy.optim.linear.NonNegativeConstraint;
import org.apache.commons.math4.legacy.optim.linear.Relationship;
import org.apache.commons.math4.legacy.optim.linear.SimplexSolver;
import org.apache.commons.math4.legacy.optim.nonlinear.scalar.GoalType;

/**
 *
 * @author Piet
 */
public class day_10 {
    
    private static int day = 10;
    private static boolean test = false;
    
    private static List<Machine> machines;
    private static int maxButtons;
    
    public static void main(String... args) throws IOException {
//        var s = "[.#..###] (0,1,4,5) (0,3,4,5,6) (3,5,6) (0,5) (0,1,4,5,6) (0,1,2,3,4) (0,1,5) (2,5) (0,1,2,4,5) {36,30,6,18,28,40,21}";
//        var m = new Machine(s);
//        var opt = m.getNumberOfPressesToReachJolts();
//////        var j = m.pressButton(new ArrayList<>(m.startJolts), 1, 4);
//////        var h = m.pressButton(new ArrayList<>(m.startJolts), 2, 2);
//////        var result = m.getJolts();
//        System.out.println(opt);
        getInput();
        solveA();
        solveB();
    }
    
    private static void getInput() throws IOException {
        var path = AOC2025.getPath(day, test);
        try (var stream = Files.lines(path)) {
            machines = stream.map(Machine::new).toList();
        }
        maxButtons = machines.stream()
            .mapToInt(m -> m.buttons.size())
            .max()
            .getAsInt()
        ;
    }
    
    private static void solveA() {
//        var result = 0;
//        for (var m: machines) {
//            var r = m.getState();
//            result += r;
//        }
//        System.out.println("A: " + result);
    }
    
    private static void solveB() {
        var result = 0;
        for (var m: machines) {
            var tot = m.getNumberOfPressesToReachJolts();
            result += tot;
        }
        System.out.println("B: " + result);
    }
    
    //**************************************************************************
    private static class Machine {
        char[] startState;
        char[] stateToReach;
        List<List<Integer>> buttons = new ArrayList<>();
        List<Integer> startJolts;
        List<Integer> joltsToReach;
        List<Integer> maxButtonPresses = new ArrayList<>();
        
        Machine(String s) {
            var arr = s.split("\\s+");
            for (var str: arr) {
                if (str.charAt(0) == '[') {
                    str = str.substring(1, str.length() - 1);
                    stateToReach = str.toCharArray();
                    startState = new char[stateToReach.length];
                    for (int i = 0; i < startState.length; i++) startState[i] = '.';
                }
                else if (str.charAt(0) == '(') {
                    str = str.substring(1, str.length() - 1);
                    var temp = str.split(",");
                    var list = Arrays.stream(temp).map(Integer::valueOf).toList();
                    buttons.add(list);
                }
                else if (str.charAt(0) == '{') {
                    str = str.substring(1, str.length() - 1);
                    var temp = str.split(",");
                    joltsToReach = Arrays.stream(temp).map(Integer::valueOf).toList();
                    startJolts = joltsToReach.stream().map(i -> 0 ).toList();
                }
                else throw new RuntimeException("unknown Machine part " + str);
            }
            // max button presses
            for (var button: buttons) {
                var max = Integer.MAX_VALUE;
                for (var b: button) {
                    max = min(max, joltsToReach.get(b));
                }
                maxButtonPresses.add(max);
            }
        }
        
        public int getNumberOfPressesToReachJolts() {
            // fr part B
            var farr = IntStream.range(0, buttons.size()).mapToDouble(i -> 1).toArray();
            var f = new LinearObjectiveFunction(farr, 0); 
            var constraints = new ArrayList<LinearConstraint>();
            for (int jolt = 0; jolt < joltsToReach.size(); jolt++) {
                var arr = new double[buttons.size()];
                for (int b = 0; b < buttons.size(); b++) {
                    if (buttons.get(b).contains(jolt)) {
                        arr[b] = 1.0;
                    }
                }
                int val = joltsToReach.get(jolt);
                constraints.add(new LinearConstraint(arr, Relationship.EQ, val));
            }
            var lcs = new LinearConstraintSet(constraints);
            var solution = new SimplexSolver().optimize(f, lcs, GoalType.MINIMIZE, new NonNegativeConstraint(true));
//            System.out.println("solution: " + solution);
            var answer = (int) (solution.getSecond() + .8);
            return answer;
        }
                
//        public boolean checkList(List<Integer> probeer) {
//            return IntStream.range(0, probeer.size())
//                .allMatch(i -> probeer.get(i) <= joltsToReach.get(i))
//            ;
//        }
        
        public List<Integer> pressButton(List<Integer> current, int buttonIndex, int times) {
            var result = current.stream().collect(toCollection(ArrayList::new));
            var b = buttons.get(buttonIndex);
            for (int i = 0; i < b.size(); i++) {
                result.set(b.get(i), result.get(i) + times);
            }
            return result;
        }
        
    }   
        
        
        
        
    
    record Jolt(int level, List<Integer> list) {
        
        public Jolt getNext(List<Integer> button) {
            var temp = list.stream().collect(toCollection(ArrayList::new));
            for (var i = 0; i < button.size(); i++) {
                temp.set(button.get(i), temp.get(button.get(i)) + 1);
            }
            return new Jolt(level + 1, temp);
        }
    }

    
}
