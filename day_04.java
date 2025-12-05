/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package aoc2025;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;

/**
 *
 * @author Piet
 */
public class day_04 {
    
    private static int day = 4;
    private static boolean test = false;
    
    private static char[][] grid;
    private static int max = 3;
    
    public static void main(String... args) throws IOException {
        getInput();
        solveA();
        solveB();
    }
    
    private static void getInput() throws IOException {
        var path = AOC2025.getPath(day, test);
        try (var stream = Files.lines(path)) {
            grid = stream
                .map(s -> s.toCharArray())
                .toArray(char[][]::new)
            ;
        }
    }
    
    private static void solveA() {
        var result = 0;
        for (int r = 0; r < grid.length; r++) {
            for (int c = 0; c < grid[r].length; c++) {
                if (grid[r][c] != '@') continue;
                if (nrOfOccupiedNeighbors(r, c) <= max) {
                    result++;
                }
            }
        }
        System.out.println("A: " + result);
    }
    
    private static void solveB() {
        var startTotal = getNrOfRolls();
        while (true) {
            var removed = false;
            for (int r = 0; r < grid.length; r++) {
                for (int c = 0; c < grid[r].length; c++) {
                    if (grid[r][c] == '@' && nrOfOccupiedNeighbors(r, c) <= max) {
                        grid[r][c] = '.';
                        removed = true;
                    }
                }
            }
            if (!removed) break;
        }
        var newTotal = getNrOfRolls();
        System.out.println("B: " + (startTotal - newTotal));
    }
    
    private static int nrOfOccupiedNeighbors(int row, int col) {
        var result = 0;
        for (int r = row - 1; r <= row + 1; r++) {
            if (r < 0 || r >= grid.length) continue;
            for (int c = col - 1; c <= col + 1; c++) {
                if (c < 0 || c >= grid[r].length) continue;
                if (r == row && c == col) continue;
                if (grid[r][c] == '@') {
                    result++;
                }
            }
        }
        return result;
    }
    
    private static int getNrOfRolls() {
        var total = 0;
        for (int r = 0; r < grid.length; r++) {
            for (int c = 0; c < grid[r].length; c++) {
                if (grid[r][c] == '@') {
                    total++;
                }
            }
        }
        return total;
    }
}
