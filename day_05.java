/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package aoc2025;

import java.io.IOException;
import static java.lang.Long.parseLong;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

/**
 *
 * @author Piet
 */
public class day_05 {
    
    private static int day = 5;
    private static boolean test = false;
    
    private static List<Long> ingredients = new ArrayList<>();
    private static List<Segment> segments = new ArrayList<>();
    
    public static void main(String... args) throws IOException {
//        var seg1 = new Segment(3, 8);
//        var seg2 = new Segment(1, 2);
//        var result = seg1.getDiff(seg2);
//        System.out.println(seg1);
//        System.out.println(seg2);
//        System.out.println(result);
        getInput();
        solveA();
        solveB();
    }
    
    private static void getInput() throws IOException {
        var path = AOC2025.getPath(day, test);
        var lines = Files.readAllLines(path);
        var firstPart = true;
        for (var s: lines) {
            if (s.isEmpty()) {
                firstPart = false;
                continue;
            }
            if (firstPart) segments.add(Segment.of(s));
            else ingredients.add(parseLong(s));
        }
    }
    
    private static void solveA() {
        var fresh = ingredients.stream()
            .filter(i -> segments.stream().anyMatch(seg -> seg.contains(i)))
            .count()
        ;
        System.out.println("A: " + fresh);
    }
    
    private static void solveB() {
        var extremes = segments.stream()
            .flatMap(seg -> Stream.of(new Extreme(seg.start, true), new Extreme(seg.end, false)))
            .sorted(Extreme.comp)
            .toList()
        ;
        var result = new ArrayList<Segment>();
        var lili = new LinkedList<Extreme>();
        for (var e: extremes) {
            if (e.start) lili.add(e);
            else {
                var begin = lili.removeLast();
                if (lili.isEmpty()) {
                    result.add(new Segment(begin.value, e.value));
                }
            }
        }
        var total = result.stream()
            .mapToLong(seg -> seg.howMany())
            .sum()
        ;
        System.out.println("B: " + total);
    }
    
    //**************************************************************************
    record Segment(long start, long end) {
        
        public static Segment of(String s) {
            var dash = s.indexOf("-");
            var start = parseLong(s.substring(0, dash));
            var end = parseLong(s.substring(dash + 1));
            return new Segment(start, end);
        }
        
        public boolean contains(long n) {
            return start <= n && n <= end;
        }
        
        public long howMany() {
            return end - start + 1;
        }
    }
    
    //**************************************************************************
    static class Extreme {
        long value;
        boolean start;
        static Comparator<Extreme> comp;
        
        static {
            comp = (a, b) -> a.value < b.value ?  -1 :
                             a.value > b.value ? 1 :
                             a.start && !b.start ? -1 :
                             !a.start && b.start ? 1 :
                             0
            ;                      
        }
        
        Extreme(long value, boolean isStartpoint) {
            this.value = value;
            start = isStartpoint;
        }
        
        @Override
        public String toString() {
            return "%d, %s".formatted(value, start);
        }
    }
}
