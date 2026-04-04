/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package aoc2025;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.toCollection;

/**
 *
 * @author Piet
 */
public class day_________11 {
    
    private static int day = 11;
    private static boolean test = false;

    private static Map<String, List<String>> map = new HashMap<>();

    public static void main(String... args) throws IOException {
        getInput();
//        solveA();
        solveB();
    }

    private static void getInput() throws IOException {
        var path = AOC2025.getPath(day, test);
        var lines = Files.readAllLines(path);
        for (var s : lines) {
            var dp = s.indexOf(":");
            var key = s.substring(0, dp);
            var value = Arrays.stream((s.substring(dp + 1).trim().split("\\s+")))
                .collect(toCollection(ArrayList::new));
            map.put(key, value);
        }
    }
    
    private static void solveA() {
        var from = "you";
        var to = "out";
        var result = getPaths(from, to);
        System.out.println("A: " + result);
    }

    private static long getPaths(String from, String to) {
        var result = 0;
        var queue = new LinkedList<String>();
        var visited = new HashSet<String>();
        queue.add(from);
        visited.add(from);
        while (!queue.isEmpty()) {
            var s = queue.removeFirst();
            var lijst = map.get(s);
            if (lijst == null || lijst.isEmpty()) {
                continue;
            }
            for (var str : lijst) {
//                if (visited.contains(str)) {
//                    continue;
//                }
                if (str.equals(to)) {
                    result++;
                    continue;
                }
                visited.add(str);
                queue.addLast(str);
            }
        }
        return result;
    }

    private static void solveB() {
        Map<Node, Long> visited = new HashMap<>();
        var result = solveBHelper("svr", false, false, visited);
        System.out.println("B:" + result);
    }

    private static long solveBHelper(String current, boolean fftSeen, boolean dacSeen, Map<Node, Long> visited) {
        if (current.equals("out")) {
            return fftSeen && dacSeen ? 1L : 0L;
        }
        var node = new Node(current, fftSeen, dacSeen);
        if (visited.containsKey(node)) {
            return visited.get(node);
        }
        var neighbours = map.get(current);
        if (neighbours == null || neighbours.isEmpty()) {
            System.out.println("null or empty for current: " + current);
            return 0L;
        }
        long result = 0;
        for (var s : neighbours) {
            var newfft = current.equals("fft");
            var newdac = current.equals("dac");;
            result += solveBHelper(s, fftSeen || newfft, dacSeen || newdac, visited);
        }
        visited.put(node, result);
        return result;
    }
}

record Node(String s, boolean fft, boolean dac) {}
