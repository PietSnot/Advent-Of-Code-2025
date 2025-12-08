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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 *
 * @author Piet
 */
public class day_08 {
    
    private static int day = 8;
    private static boolean test = false;
    
    private static List<Punt> points;
    private static TreeMap<Long, List<Punt>> puntParen = new TreeMap<>();
    
    public static void main(String... args) throws IOException {
        getInput();
        solveA();
        solveB();
    }
    
    private static void getInput() throws IOException {
        var path = AOC2025.getPath(day, test);
        try (var stream = Files.lines(path)) {
            points = stream
                .map(Punt::of)
                .toList()
            ;
        }
        
        for (int i = 0; i < points.size() - 1; i++) {
            var pf = points.get(i);
            for (int j = i + 1; j < points.size(); j++) {
                var ps = points.get(j);
                puntParen.put(pf.distance(ps), List.of(pf, ps));
            }
        }
    }
    
    private static void solveA() {
        var circuits = new ArrayList<HashSet<Punt>>();
        ArrayList<Map.Entry<Long, List<Punt>>> entries = new ArrayList<>(puntParen.entrySet());
        Comparator<Map.Entry<Long, List<Punt>>> comp = Map.Entry.comparingByKey();
        entries.sort(comp);
        var end = test ? 10 : 1000;
        for (var i = 0; i < end; i++) {
            var p1 = entries.get(i).getValue().getFirst();
            var p2 = entries.get(i).getValue().getLast();
            HashSet<Punt> p1c = null;
            HashSet<Punt> p2c = null;
            for (var c: circuits) {
                if (c.contains(p1)) p1c = c;
                if (c.contains(p2)) p2c = c;            
            }
            if (p1c == null && p2c != null) {
                p2c.add(p1);
            }
            else if (p1c != null && p2c == null) {
                p1c.add(p2);
            }
            else if (p1c != null && p2c != null) {
                if (!p1c.equals(p2c)) {
                    p1c.addAll(p2c);
                    circuits.remove(p2c);
                }
            }
            else {
                // allebei null
                var h = new HashSet<Punt>();
                h.add(p1);
                h.add(p2);
                circuits.add(h);
            }               
        }
        // sorteren op lengte
        Comparator<Set> comp2 = Comparator.comparingInt(Set::size);
        circuits.sort(comp2.reversed());
        var result = circuits.subList(0, 3).stream()
            .mapToInt(Set::size)
            .reduce(1, (a, b) -> a * b)
        ;
        System.out.println("A: " + result); 
    }
    
    private static void solveB() {
        var circuits = new ArrayList<HashSet<Punt>>();
        ArrayList<Map.Entry<Long, List<Punt>>> entries = new ArrayList<>(puntParen.entrySet());
        Comparator<Map.Entry<Long, List<Punt>>> comp = Map.Entry.comparingByKey();
        entries.sort(comp);
        for (var i = 0; i < entries.size(); i++) {
            var p1 = entries.get(i).getValue().getFirst();
            var p2 = entries.get(i).getValue().getLast();
            HashSet<Punt> p1c = null;
            HashSet<Punt> p2c = null;
            for (var c: circuits) {
                if (c.contains(p1)) p1c = c;
                if (c.contains(p2)) p2c = c;            
            }
            if (p1c == null && p2c != null) {
                p2c.add(p1);
            }
            else if (p1c != null && p2c == null) {
                p1c.add(p2);
            }
            else if (p1c != null && p2c != null) {
                if (!p1c.equals(p2c)) {
                    p1c.addAll(p2c);
                    circuits.remove(p2c);
                }
            }
            else {
                // allebei null
                var h = new HashSet<Punt>();
                h.add(p1);
                h.add(p2);
                circuits.add(h);
            }
            
            for (var c: circuits) {
                if (c.size() == points.size()) {
                    var result = p1.x * p2.x;
                    System.out.println("B: " + result);
                    return;
                }
            }
        }
        
    }
    
    
    //**************************************************************************
    static record Punt(long x, long y, long z) {
        
        public static Punt of(String s) {
            var arr = s.split(",");
            return new Punt(parseLong(arr[0]), parseLong(arr[1]), parseLong(arr[2]));
        }
        public long distance(Punt p) {
            var dx = x - p.x;
            var dy = y - p.y;
            var dz = z - p.z;
            return dx * dx + dy * dy + dz * dz;
        }
    }
}
