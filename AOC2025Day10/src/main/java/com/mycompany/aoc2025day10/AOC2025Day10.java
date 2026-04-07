/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.aoc2025day10;

import com.google.ortools.Loader;
import com.google.ortools.sat.CpModel;
import com.google.ortools.sat.CpSolver;
import com.google.ortools.sat.CpSolverStatus;
import com.google.ortools.sat.IntVar;
import com.google.ortools.sat.LinearExpr;
import java.io.IOException;
import static java.lang.Integer.min;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static java.util.stream.Collectors.toCollection;
import java.util.stream.LongStream;

/**
 *
 * @author Piet
 */
public class AOC2025Day10 {

    public static void main(String[] args) throws URISyntaxException, IOException {
        
//        Loader.loadNativeLibraries();
//        CpModel model = new CpModel();
//        
//        
//        IntVar[] v = new IntVar[3];
//        v[0] = model.newIntVar(0, 50, "A");
//        v[1] = model.newIntVar(0, 50, "B");
//        v[2] = model.newIntVar(0, 50, "C");
//        
//        model.addLessOrEqual(LinearExpr.weightedSum(v, new long[]{2, 7, 3}), 50);
//        model.addLessOrEqual(LinearExpr.weightedSum(v, new long[]{3, -5, 7}), 45);
//        model.addLessOrEqual(LinearExpr.weightedSum(v, new long[]{5, 2, -6}), 37);
//        
//        model.maximize(LinearExpr.weightedSum(v, new long[]{1, 1, 1}));
//        
//        CpSolver solver = new CpSolver();
//        var status = solver.solve(model);
//        
//        if (status == CpSolverStatus.OPTIMAL || status == CpSolverStatus.FEASIBLE) {
//            System.out.printf("Minimum of objective function: %f%n", solver.objectiveValue());
//            System.out.println("A = " + solver.value(v[0]));
//            System.out.println("y = " + solver.value(v[1]));
//            System.out.println("z = " + solver.value(v[2]));
//        } else {
//            System.out.println("No solution found.");
//        }
//        
//        var s = "[...#.] (0,2,3,4) (2,3) (0,4) (0,1,2) (1,2,3,4) {7,5,12,7,2}";
//        var m = new Machine(s);
//        var start = System.currentTimeMillis();
//        var t = m.getNumberOfPressesToReachJolts();
//        var end = System.currentTimeMillis();
//        System.out.println("duurde: " + (end - start) / 1000. + " seconden");
        var mac = "[#####....] (0,3,6,8) (0,2,3,4,5,6) (1,4,5,8) (6,8) (0,1,3,5,7,8) (0,1,4,5,6,7,8) (1,3,5,6,7) (0,1,2,3,4,6,8) (2,5) {56,253,31,241,39,250,240,234,56}";
        var macmac = new Machine(mac);
        var st = System.currentTimeMillis();
        var t = macmac.getNumberOfPressesToReachJolts();
        var et = System.currentTimeMillis();
        System.out.println("took: " + (et - st) / 1000. + " secs");
        System.out.println("***************************************");
        var c = new AOC2025Day10();
        var url = c.getClass().getResource("/day_10.txt");
        var path = Paths.get(url.toURI());
        List<Machine> machines;
        try (var stream = Files.lines(path)) {
            machines = stream.map(Machine::new).toList();
        }
        var start = System.currentTimeMillis();
        var total = machines.stream()
            .mapToLong(m -> m.getNumberOfPressesToReachJolts())
            .sum()
        ;
        var end = System.currentTimeMillis();
        System.out.println("total: " + total);
        System.out.println("took: " + (end - start) / 1000. + " secs");
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
        
        public long getNumberOfPressesToReachJolts() {
            // fr part B
            Loader.loadNativeLibraries();
            CpModel model = new CpModel();
            
            IntVar[] v = new IntVar[buttons.size()];
            for (int i = 0; i < buttons.size(); i++) {
                v[i] = model.newIntVar(0, maxButtonPresses.get(i), "" + (char) (i + 65));
            }
            
            // adding constraints
            for (int i = 0; i < joltsToReach.size(); i++) {
                var arr = getCoefficients(i);
                var t = joltsToReach.get(i);
                model.addEquality(LinearExpr.weightedSum(v, arr), t);
            }
            var arrl = LongStream.range(0, buttons.size()).map(i -> 1).toArray();
            
            model.minimize(LinearExpr.weightedSum(v, arrl));
            
            var solver = new CpSolver();
            var status = solver.solve(model);
            
            if (status == CpSolverStatus.OPTIMAL || status == CpSolverStatus.FEASIBLE) {
//                System.out.printf("Minimum of objective function: %f%n", solver.objectiveValue());
//                for (int i = 0; i < buttons.size(); i++) {
//                    System.out.println(v[i].getName() + " = " + solver.value(v[i]));
//                } 
            } 
            else {
                System.out.println("No solution found.");
            }
            
            return (long) (solver.objectiveValue() + .1);
        }
        
        private long[] getCoefficients(int jolt) {
            var arr = new long[buttons.size()];
            for (int i = 0; i < buttons.size(); i++) {
                if (buttons.get(i).contains(jolt)) {
                    arr[i] = 1;
                }
            }
            return arr;
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
