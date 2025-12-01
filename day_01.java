/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package aoc2025;

import java.io.IOException;
import static java.lang.Integer.parseInt;
import static java.lang.Math.abs;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import static java.util.stream.Gatherers.windowSliding;
import java.util.stream.IntStream;

/**
 *
 * @author Piet
 */
public class day_01 {
    
    private static final int day = 1;
    private static final boolean test = false;
    
    private static final int startPos = 50;
    private static final int length = 100;
    
    private static List<DivMod> divmods = new ArrayList<>();
    
    public static void main(String... args) throws IOException {
        getInput();
        solveA();
        solveB();
    }
    
    private static void getInput() throws IOException {
        var path = AOC2025.getPath(day, test);
        var rotations = Files.readAllLines(path);
        var currentValue = startPos;
        divmods.add(DivMod.of(currentValue));
        for (var rot: rotations) {
            currentValue = processRotation(currentValue, rot);
            divmods.add(DivMod.of(currentValue));
        }
    }
    
    private static int processRotation(int start, String rot) {
        var sign = rot.charAt(0) == 'L' ? -1 : 1;
        var val = parseInt(rot.substring(1));
        var result = start + val * sign;
        return result;
    }
    
    private static void solveA() {
        var result = divmods.stream()
            .skip(1)
            .filter(d -> d.mod == 0)
            .count()
        ;
        System.out.println("A: " + result);
    }
    
    private static void solveB() {
        var result = divmods.stream()
            .gather(windowSliding(2))
            .mapToInt(list -> list.get(1).zeroesTo(list.get(0)) )
            .sum()
        ;
        System.out.println("B: " + result);
    }
    
    //**************************************************************************
    static record DivMod(int n, int div, int mod) {
        
        public static DivMod of(int n) {
            var d = n / length;
            var m = n % length;
            if (m < 0) {
                d--;
                m += length;
            }
            return new DivMod(n, d, m);
        }
        
        public int zeroesTo(DivMod d) {
            var result = abs(div - d.div);
            if (n < d.n) {
                if (mod == 0) result++;
                else if (d.mod == 0) result--;
            }
            return result;
        }
    }
}    
