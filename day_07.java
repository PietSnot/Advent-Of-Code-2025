/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package aoc2025;

import java.awt.Point;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Piet
 */
public class day_07 {
    
    private static int day = 7;
    private static boolean test = false;
    
    private static char[][] grid;
    private static Point start = null;
    
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
        for (int i = 0; i < grid[0].length; i++) {
            if (grid[0][i] == 'S') {
                start = new Point(0, i);
                break;
            }
        }
        if (start == null) throw new RuntimeException("geen start gevonden");
    }
    
    private static void solveA() {
        var result = 0;
        var beams = new HashSet<Point>();
        beams.add(start);
        for (int row = 1; row < grid.length; row++) {
            var templist = new HashSet<Point>();
            var splits = 0;
            for (var beam: beams) {
                if (grid[row][beam.y] == '^') {
                    templist.add(new Point(row, beam.y - 1));
                    templist.add(new Point(row, beam.y + 1));
                    splits++;
                    checkTemplist(templist);
                }
                else {
                    templist.add(new Point(row, beam.y));
                }
            }
            beams = templist;
            result += splits;
        }
        System.out.println("A: " + result);
    }
    
    private static void solveB() {
        long[][] totals = new long[grid.length][grid[0].length];
        totals[start.x][start.y] = 1L;
        for (int row = 1; row < grid.length; row++) {
            for (int col = 0; col < grid[row].length; col++) {
                if (grid[row][col] == '^') continue;
                var temp = totals[row - 1][col];
                if (col + 1 < grid[row].length) {
                    if (grid[row][col + 1] == '^') {
                        temp += totals[row - 1][col + 1];
                    }
                }
                if (col - 1 >= 0) {
                    if (grid[row][col - 1] == '^') {
                        temp += totals[row - 1][col - 1];
                    }
                }
                totals[row][col] = temp;
            }
        }
        var result = Arrays.stream(totals[grid.length - 1]).sum();
        System.out.println("B: " + result);
    }
    
    private static void checkTemplist(Set<Point> set) {
        set.removeIf(p -> p.y < 0 || p.y >= grid[0].length);
    }
    
}
