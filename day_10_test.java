/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package aoc2025;

import com.google.ortools.Loader;
import com.google.ortools.sat.CpModel;
import com.google.ortools.sat.CpSolver;
import com.google.ortools.sat.CpSolverStatus;
import com.google.ortools.sat.IntVar;
import com.google.ortools.sat.LinearExpr;
import static java.lang.Integer.min;
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



public class day_10_test {
    
    public static void main(String... args) {
        Loader.loadNativeLibraries();
        CpModel model = new CpModel();
        
        
        IntVar[] v = new IntVar[3];
        v[0] = model.newIntVar(0, 50, "A");
        v[1] = model.newIntVar(0, 50, "B");
        v[2] = model.newIntVar(0, 50, "C");
        
        model.addLessOrEqual(LinearExpr.weightedSum(v, new long[]{2, 7, 3}), 50);
        model.addLessOrEqual(LinearExpr.weightedSum(v, new long[]{3, -5, 7}), 45);
        model.addLessOrEqual(LinearExpr.weightedSum(v, new long[]{5, 2, -6}), 37);
        
        model.maximize(LinearExpr.weightedSum(v, new long[]{2, 2, 3}));
        
        CpSolver solver = new CpSolver();
        var status = solver.solve(model);
        
        if (status == CpSolverStatus.OPTIMAL || status == CpSolverStatus.FEASIBLE) {
            System.out.printf("Maximum of objective function: %f%n", solver.objectiveValue());
            System.out.println("A = " + solver.value(v[0]));
            System.out.println("y = " + solver.value(v[1]));
            System.out.println("z = " + solver.value(v[2]));
        } else {
            System.out.println("No solution found.");
        }
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