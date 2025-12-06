/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package aoc2025;

import java.io.IOException;
import static java.lang.Long.parseLong;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.stream.IntStream;

/**
 *
 * @author Piet
 */
public class day_06 {
    
    private static final int day = 6;
    private static boolean test = false;
    
    private static List<String> rawInput;
    private static String rawOperations;
    private static int longestLine;
    private static long[][] input;
    private static String[] operations;
    
    private static Map<String, BinaryOperator<Long>> map = new HashMap<>();
    static {
        map.put("+", Long::sum);
        map.put("*", (a, b) -> a * b);
    }
    
    public static void main(String... args) throws IOException {
        getInput();
        solveA();
        solveB();
    }
    
    private static void getInput() throws IOException {
        var path = AOC2025.getPath(day, test);
        var lines = Files.readAllLines(path);
        rawInput = lines.subList(0, lines.size() - 1);
        input = lines.subList(0, lines.size() - 1).stream()
            .map(s -> Arrays.stream(s.trim().split("\\s+")).mapToLong(Long::parseLong).toArray())
            .toArray(long[][]::new)
        ;
        rawOperations = lines.get(lines.size() - 1);
        operations = Arrays.stream(lines.get(lines.size() - 1).split("\\s+")).toArray(String[]::new);
        longestLine = rawInput.stream()
            .mapToInt(String::length)
            .max()
            .getAsInt()
        ;
    }
    
    private static void solveA() {
        var result = 0L;
        for (int i = 0; i < input[0].length; i++) {
            var arr = getColumn(i);
            var op = operations[i];
            var temp = arr.stream()
                .reduce(map.get(operations[i]))
                .get()
            ;
            result += temp;
        }
        System.out.println("A: " + result);
    }
    
    private static void solveB() {
        var colStarts = getColStarts();
        var result = 0L;
        for (int i = 0; i < colStarts.length; i++) {
            var len = i == colStarts.length - 1 ? longestLine - colStarts[i] :
                           colStarts[i + 1] - colStarts[i] - 1
            ;
            var list = getNumbers(colStarts[i], len);
            var temp = list.stream().reduce(map.get(operations[i])).get();
            result += temp;
        }
        System.out.println("B: " + result);
    }
    
    private static int[] getColStarts() {
        var result = IntStream.range(0, rawOperations.length())
            .filter(i -> rawOperations.charAt(i) != ' ')
            .toArray()
        ;
        return result;
    }
    
    private static List<Long> getColumn(int col) {
        var result = Arrays.stream(input)
            .mapToLong(arr -> arr[col])
            .boxed()
            .toList()
        ;
        return result;
    }
    
    private static List<Long> getNumbers(int colStart, int colWidth) {
        var result = new ArrayList<Long>();
        for (int i = colStart + colWidth - 1; i >= colStart; i--) {
            var sb = new StringBuilder();
            for (var row = 0; row < rawInput.size(); row++) {
                sb.append(rawInput.get(row).charAt(i));
            }
            var str = sb.toString().trim();
            if (str.isEmpty()) {
                result.add(0L);
            }
            else result.add(parseLong(str));
        }
        return result;
    }
}
