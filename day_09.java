/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package aoc2025;

import java.io.IOException;
import static java.lang.Long.max;
import static java.lang.Long.min;
import static java.lang.Long.parseLong;
import static java.lang.Math.abs;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;
import static java.util.stream.Collectors.toCollection;
//import static java.util.stream.Collectors.toCollection;
import java.util.stream.Stream;

/**
 *
 * @author Piet
 */
public class day_09 {
    
    private static int day = 9;
    private static boolean test = false;
    
    private static List<Tile> tiles;
    private static TreeMap<Long, ArrayList<Segment>> rowMap = new TreeMap<>();
    private static ArrayList<Long> sortedRows = new ArrayList<>();
    private static TreeMap<Long, ArrayList<Segment>> cumRowsFromTop = new TreeMap<>();
    private static TreeMap<Long, ArrayList<Segment>> cumRowsFromBottom = new TreeMap<>();
    private static TreeMap<Long, ArrayList<Segment>> colMap = new TreeMap<>();
    private static ArrayList<Long> sortedCols = new ArrayList<>();
    private static TreeMap<Long, ArrayList<Segment>> cumColsFromLeft = new TreeMap<>();
    private static TreeMap<Long, ArrayList<Segment>> cumColsFromRight = new TreeMap<>();
    
    
    private static List<Tile> selectedTiles = new ArrayList<>();
    
    public static void main(String... args) throws IOException {
        getInput();
        var start = System.currentTimeMillis();
        solveA();
        solveB();
        var end = System.currentTimeMillis();
        System.out.println("-------------------");
        System.out.println("took: " + (end - start) / 1000. + " secs");
    }
    
    private static void getInput() throws IOException {
        var path = AOC2025.getPath(day, test);
        try (var stream = Files.lines(path)) {
             tiles = new ArrayList<>(stream.map(Tile::of).toList());
        }
        
        // creating rowMap and colMap
        tiles.add(tiles.getFirst());
        for (var i = 0; i < tiles.size() - 1; i++) {
            var t1 = tiles.get(i);
            var t2 = tiles.get(i + 1);
            var dir = Direction.getDirection(t1, t2);
            if (dir == Direction.COL) {
                colMap.computeIfAbsent(t1.col, k -> new ArrayList<>()).add(Segment.of(t1, t2));
            }
            else {
                rowMap.computeIfAbsent(t1.row, k -> new ArrayList<>()).add(Segment.of(t1, t2));
            }
        }
        tiles.removeLast();
        
        // creating sortedRows and sortedCols
        sortedRows = new ArrayList<>(rowMap.keySet().stream().sorted().toList());
        sortedCols = new ArrayList<>(colMap.keySet().stream().sorted().toList());
        //----------------------------------------------------------------------
        // cumColsFromLeft
        //----------------------------------------------------------------------
        var oldList1 = new ArrayList<Segment>();
        cumColsFromLeft.put(Long.MIN_VALUE, new ArrayList<Segment>());
        for (var col: sortedCols) {
            var newList = Segment.union(oldList1, colMap.get(col));
            cumColsFromLeft.put(col, newList);
            oldList1 = newList;
        }
        cumColsFromLeft.put(Long.MAX_VALUE, oldList1);
        //----------------------------------------------------------------------
        // cumColsFromRight
        //----------------------------------------------------------------------
        var oldList2 = new ArrayList<Segment>();
        cumColsFromRight.put(Long.MAX_VALUE, new ArrayList<Segment>());
        var temp = sortedCols.stream().sorted(Comparator.reverseOrder()).toList();
        for (var col: temp) {
            var newList = Segment.union(oldList2, colMap.get(col));
            cumColsFromRight.put(col, newList);
            oldList2 = newList;
        }
        cumColsFromRight.put(Long.MIN_VALUE, oldList2);
        //----------------------------------------------------------------------
        // cumRowsFromTop
        //----------------------------------------------------------------------
        var oldList3 = new ArrayList<Segment>();
        cumRowsFromTop.put(Long.MIN_VALUE, new ArrayList<Segment>());
        for (var row: sortedRows) {
            var newList = Segment.union(oldList3, rowMap.get(row));
            cumRowsFromTop.put(row, Segment.union(oldList3, newList));
            oldList3 = newList;
        }
        cumRowsFromTop.put(Long.MAX_VALUE, oldList3);
        //----------------------------------------------------------------------
        // cumRowsFromBottom
        //----------------------------------------------------------------------
        var oldList4 = new ArrayList<Segment>();
        cumRowsFromBottom.put(Long.MAX_VALUE, new ArrayList<Segment>());
        temp = sortedRows.stream().sorted(Comparator.reverseOrder()).toList();
        for (var row: temp) {
            var newList = Segment.union(oldList4, rowMap.get(row));
            cumRowsFromBottom.put(row, Segment.union(oldList4, newList));
            oldList4 = newList;
        }
        cumRowsFromBottom.put(Long.MIN_VALUE, oldList4);
    }
    
    private static void solveA() {
        selectedTiles.clear();
        var max = Long.MIN_VALUE;
        for (int i = 0; i < tiles.size() - 1; i++) {
            var iTile = tiles.get(i);
            for (int j = i; j < tiles.size(); j++) {
                var jTile = tiles.get(j);
                var opp = iTile.getArea(jTile);
                if (opp > max) {
                    max = opp;
                    selectedTiles.clear();
                    selectedTiles.add(iTile);
                    selectedTiles.add(jTile);
                }
            }
        }
        System.out.println("A: " + max);
        System.out.println("tiles: ");
        System.out.println(selectedTiles.getFirst());
        System.out.println(selectedTiles.get(1));
        System.out.println("--------------------------------");
    }
    
    private static void solveB() {
        selectedTiles.clear();
        var max = Long.MIN_VALUE;
        for (int i = 0; i < tiles.size() - 1; i++) {
            var iTile = tiles.get(i);
            for (int j = i; j < tiles.size(); j++) {
                var jTile = tiles.get(j);
                if (isValidRectangle(iTile, jTile)) {
                    var area = iTile.getArea(jTile);
                    if (area > max) {
                        max = area;
                        selectedTiles.clear();
                        selectedTiles.add(iTile);
                        selectedTiles.add(jTile);
                    }
                }
            }
        }
        System.out.println("B: " + max);
        System.out.println("sel. tiles: ");
        selectedTiles.forEach(System.out::println);
    }
    
    private static boolean isValidRectangle(Tile t1, Tile t2) {
        var mincol = min(t1.col, t2.col);
        var maxcol = max(t1.col, t2.col);
        var minrow = min(t1.row, t2.row);
        var maxrow = max(t1.row, t2.row);
        var segmentLeft = Segment.of((int) mincol, minrow, maxrow, Direction.COL);
        var segmentRight = Segment.of((int) maxcol, minrow, maxrow, Direction.COL);
        var segmentTop = Segment.of((int)minrow, mincol, maxcol, Direction.ROW);
        var segmentBottom = Segment.of((int)maxrow, mincol, maxcol, Direction.ROW);
        var left = contains(cumColsFromLeft.floorEntry(mincol).getValue(), segmentLeft);
        var right = contains(cumColsFromLeft.ceilingEntry(maxcol).getValue(), segmentRight);
        var top = contains(cumRowsFromTop.floorEntry(minrow).getValue(), segmentTop);
        var bottom = contains(cumRowsFromBottom.ceilingEntry(maxrow).getValue(), segmentBottom);
        return left && right && top && bottom;    
    }
    
    private static boolean contains(List<Segment> list, Segment s) {
        var result = list.stream().anyMatch(segment -> segment.contains(s));
        return result;
    }
    
    //**************************************************************************
    
    enum Direction {
        ROW, COL;
        
        public static Direction getDirection(Tile t1, Tile t2) {
            if (t1.col == t2.col) {
                return Direction.COL;
            }
            else if (t1.row == t2.row) {
                return Direction.ROW;
            }
            else throw new RuntimeException("unknown direction");
        }
    }

    //**************************************************************************
    record Tile(long col, long row) {
        
        public static Tile of(String s) {
            var c = s.indexOf(",");
            var a = parseLong(s.substring(0, c));
            var b = parseLong(s.substring(c + 1));
            return new Tile(a, b);
        }
        
        public long getArea(Tile t) {
            var result = (abs(row - t.row) + 1) * (abs(col - t.col) + 1);
            return result;
        } 
    }
    
    
    //**************************************************************************
    record Segment(int rowOrCol, long min, long max, Direction d) {
        
        public static Segment of(int rowOrCol, long a, long b, Direction d) {
            return new Segment(rowOrCol, Long.min(a, b), Long.max(a, b), d);
        }
        
        public static Segment of(Tile t1, Tile t2) {
            var dir = Direction.getDirection(t1, t2);
            if (dir == Direction.COL) {
                var col = t1.col;
                return Segment.of((int)col, t1.row, t2.row, dir);
            }
            else {
                var row = t1.row;
                return Segment.of((int) row, t1.col, t2.col, dir);
            }
        }
        
        public boolean contains(long x) {
            return min <= x && x <= max;
        }
        
        public boolean contains(Segment s) {
            var temp = min <= s.min && s.max <= max;
            return temp;
        }
        
        public static ArrayList<Segment> union(List<Segment> list, List<Segment> seg) {
            if (seg.isEmpty()) throw new RuntimeException("empty seg");
            var rowOrCol = seg.getFirst().rowOrCol;
            var dir = seg.getFirst().d;
            var list1 = list.stream().flatMap(Segment::getSegmentEdges).toList();
            var list2 = seg.stream().flatMap(Segment::getSegmentEdges).toList();
            var temp = Stream.concat(list1.stream(), list2.stream()).collect(toCollection(ArrayList::new));
            temp.sort(SegmentEdge.getComparator());
            var result = new ArrayList<Segment>();
            var queue = new LinkedList<SegmentEdge>();
            
            for (var se: temp) {
                if (se.isBegin) {
                    queue.addLast(se);
                }
                else {
                    var seged = queue.removeLast();
                    if (queue.isEmpty()) {
                        result.add(Segment.of(rowOrCol, seged.value, se.value, dir));
                    }
                }
            }
            return result;
        }
        
        public Stream<SegmentEdge> getSegmentEdges() {
            return Stream.of(new SegmentEdge(min, true), new SegmentEdge(max, false));
        }
    }
    
    //**************************************************************************
    record SegmentEdge(long value, boolean isBegin) {
        
        public static Comparator<SegmentEdge> getComparator() {
            Comparator<SegmentEdge> c1 = (a, b) -> 
                (b.value == a.value + 1) && (!a.isBegin) && (b.isBegin) ?  1 :
                (a.value == b.value + 1) && (a.isBegin) && (!b.isBegin) ? -1 :
                                                                           0
            ;
            Comparator<SegmentEdge> c2 = Comparator.comparingLong(SegmentEdge::value);
            Comparator<SegmentEdge> c3 = Comparator.comparing(SegmentEdge::isBegin);
//            return c1.thenComparing(c2).thenComparing(c3.reversed());
            return c1.thenComparing(c2).thenComparing(c3.reversed());
        }
    }
}
