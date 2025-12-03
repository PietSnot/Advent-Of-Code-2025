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
import java.util.stream.LongStream;

/**
 *
 * @author Piet
 */
public class day_02 {
    
    private static final int day = 2;
    private static final boolean test = true;
    
    static List<Segment> ranges = new ArrayList<>();
    
    static final Map<Integer, List<Integer>> divisors = new HashMap<>();
    static {
        divisors.put(1, new ArrayList<Integer>());
        for (int i = 2; i <= 30; i++) {
            var list = new ArrayList<Integer>();
            for (int j = 1; j < i; j++)  {
                if (i % j == 0) {
                    list.add(j);
                }
            }
            divisors.put(i, list);
        }
    }
    
    public static void main(String... args) throws IOException {
        var seg = Segment.of("11-12");
        var list = seg.getInvalidIDsA();
        System.out.println(list);
        getInput();
        solveA();
        solveB();
    }
    
    private static void getInput() throws IOException {
        var path = AOC2025.getPath(day, test);
        var input = Files.readAllLines(path);
        var arr = input.get(0).split(",");
        ranges = Arrays.stream(arr).map(Segment::of).toList();
    }
    
    private static void solveA() {
        var result = ranges.stream()
            .map(range -> range.getInvalidIDsA())
            .flatMap(List::stream)
            .mapToLong(i -> i)
            .sum()
        ;
        System.out.println("A: " + result);
    }
    
    private static void solveB() {
        var result = ranges.stream()
            .map(r -> r.getInvalidIdsB())
            .flatMap(List::stream)
            .mapToLong(i -> i)
            .sum()
        ;
        System.out.println("B: " + result);
    }
}

//******************************************************************************
record Segment(long start, long end) {
    
    public static Segment of(String s) {
        var arr = s.split("-");
        var start = parseLong(arr[0]);
        var end = parseLong(arr[1]);
        return new Segment(start, end);
    }
    
    public List<Long> getInvalidIDsA() {
        var result = LongStream.rangeClosed(start, end)
            .filter(lang -> isInvalidA(lang))
            .boxed()
            .toList()
        ;
        return result;
    }
    
    
    public List<Long> getInvalidIdsB() {
        var result = LongStream.rangeClosed(start, end)
            .filter(lang -> isInvalidB(lang, day_02.divisors.get(("" + lang).length())))
            .boxed()
            .toList()
        ;
        return result;
    }
    
    public boolean isInvalidA(long n) {
        var str = "" + n;
        var strLen = str.length();
        if (strLen % 2 != 0) return false;
        return str.substring(0, strLen / 2).equals(str.substring(strLen / 2));
    }
    
    private boolean isInvalidB(long n, List<Integer> divisors) {
        return divisors.stream().anyMatch((div -> isInvalid(n, div)));
    }
    
    private boolean isInvalid(long nr, int len) {
        var strn = "" + nr;
        if (strn.length() < 2) return false;
        if (strn.length() % len != 0) return false;
        var begin = strn.substring(0, len);
        return strn.equals(strn.substring(0, len).repeat(strn.length() / len));
    }
}
