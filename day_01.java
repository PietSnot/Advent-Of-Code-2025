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
    
    private static List<Integer> numbers = new ArrayList<>();
    
    public static void main(String... args) throws IOException {
        getInput();
        solve(true);
        solve(false);
    }
    
    private static void getInput() throws IOException {
        var path = AOC2025.getPath(day, test);
        var rotations = Files.readAllLines(path);
        var currentValue = startPos;
        numbers.add(currentValue);
        for (var rot: rotations) {
            currentValue = processRotation(currentValue, rot);
            numbers.add(currentValue);
        }
    }
    
    private static int processRotation(int start, String rot) {
        var sign = rot.charAt(0) == 'L' ? -1 : 1;
        var val = parseInt(rot.substring(1));
        var result = start + val * sign;
        return result;
    }
    
    private static void solve(boolean partA) {
        var result = IntStream.range(1, numbers.size())
            .map(i -> getNrOfZeroes(numbers.get(i - 1), numbers.get(i), partA))
            .sum()
        ;
        System.out.println((partA ? "A: " : "B: ") + result);
    }
    
    private static int getNrOfZeroes(int a, int b, boolean onlyEnd) {
        
        if (a == b) return 0;
        if (onlyEnd) return b % length == 0 ? 1 : 0;
        
        int result = 0;
        var delta = a < b ? 1 : -1;
        for (int i = a + delta; ; i += delta) {
            if (i % length == 0) result++;
            if (i == b) break;
        }
        
        return result;
    }
}    
