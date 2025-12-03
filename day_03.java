/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package aoc2025;

import java.io.IOException;
import static java.lang.Long.parseLong;
import java.nio.file.Files;
import java.util.List;

/**
 *
 * @author Piet
 */
public class day_03 {
    
    private final static int day = 3;
    private final static boolean test = false;
    
    public static List<List<Long>> input;
    
    public static void main(String... args) throws IOException {
        getInput();
        solveA();
        solveB();
    }
    
    private static void getInput() throws IOException {
        var path = AOC2025.getPath(day, test);
        try (var stream = Files.lines(path)) {
            input = stream
                .map(line -> processInput(line))
                .toList()
            ;
        }
    }
    
    private static void solveA() {
        var result = 0;
        for (var list: input) {
            var mai1 = getMaxAndIndex(list.subList(0, list.size() - 1));
            var mai2 = getMaxAndIndex(list.subList(mai1.index + 1, list.size()));
            var temp = mai1.max * 10 + mai2.max;
            result += temp;
        }
        System.out.println("A: " + result);
    }
    
    private static void solveB() {
        int aantalBatterijen = 12;
        var result = 0L;
        for (var list: input) {
            var startIndex = 0;
            var temp = "";
            for (var i = aantalBatterijen; i >= 1; i--) {
                var mai = getMaxAndIndex(list.subList(startIndex, list.size() - i + 1));
                temp += mai.max;
                startIndex += mai.index + 1;
            }
            result += parseLong(temp);
        }
        System.out.println("B: " + result);
    }
    
    private static List<Long> processInput(String s) {
        var result = s.chars()
            .mapToLong(c -> (long) c - '0')
            .boxed()
            .toList()
        ;
        return result;
    }
    
    private static MaxAndIndex getMaxAndIndex(List<Long> list) {
        int index = 0;
        long max = list.get(0);
        for (int i = 1; i < list.size(); i++) {
            if (list.get(i) > max) {
                max = list.get(i);
                index = i;
            }
        }
        return new MaxAndIndex(max, index);
    }
    
    //**************************************************************************
    record MaxAndIndex(long max, int index) {}
}
