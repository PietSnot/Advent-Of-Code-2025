/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package aoc2025;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import static java.lang.Integer.max;
import static java.lang.Integer.parseInt;
import static java.lang.Long.min;
import java.nio.file.Files;
import static java.nio.file.Files.list;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

/**
 *
 * @author Piet
 */
public class day_09_swing {
    static int day = 9;
    static boolean test = false;
    List<Point> tiles = new ArrayList<>();
    
    Map<Integer, List<SegmentC>> rowMap = new HashMap<>();
    Map<Integer, List<SegmentC>> colMap = new HashMap<>();
    List<Integer> sortedRows, sortedCols;
    int maxsize = 100_000;
    int bufsize = 600;
    int spsize = 650;
    double scale =  bufsize * 1.0 / maxsize; 
    BufferedImage buf = new BufferedImage(bufsize, bufsize, BufferedImage.TYPE_INT_RGB);
    List<Point> points = new ArrayList<>(List.of(new Point(5025, 66513), new Point(94997, 50126)));
    
    public static void main(String... args) {
        SwingUtilities.invokeLater(day_09_swing::new);
    }
    
    day_09_swing() {
        try {
            getInput();
        }
        catch (Exception e) {
            throw new RuntimeException("er gaat iets mis");
        }
        var g = buf.createGraphics();
        g.scale(scale, scale);
        rowMap.entrySet().stream()
            .forEach(e -> e.getValue().stream()
                          .forEach(s -> s.draw(e.getKey(), g))
                    )
        ;
        colMap.entrySet().stream()
            .forEach(e -> e.getValue().stream()
                          .forEach(s -> s.draw(e.getKey(), g))
                    )
        ;
        
        var p1 = points.get(0);
        var p2 = points.get(1);
        var mincol = Integer.min(p1.x, p2.x);
        var maxcol = Integer.max(p1.x, p2.x);
        var minrow = Integer.min(p1.y, p2.y);
        var maxrow = Integer.max(p1.y, p2.y);
        g.setColor(Color.blue);
        g.drawLine(mincol, minrow, maxcol, minrow);
        g.drawLine(maxcol, minrow, maxcol, maxrow);
        g.drawLine(maxcol, maxrow, mincol, maxrow);
        g.drawLine(mincol, maxrow, mincol, minrow);
        
        g.drawRect(mincol, minrow, maxcol - mincol + 1, maxrow - minrow + 1);
        g.dispose();
        
        var panel = new JPanel() {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(bufsize, bufsize);
            }
            @Override
            protected void paintComponent(Graphics g) {
                g.drawImage(buf, 0, 0, null);
            }
        };
        
        var sp = new JScrollPane(panel);
        sp.setPreferredSize(new Dimension(spsize, spsize));
        
        var f = new JFrame();
        var c = f.getContentPane();
        c.add(sp, BorderLayout.CENTER);
        f.pack();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setLocationRelativeTo(null);
        f.setVisible(true);
        
    }
    
    void getInput() throws IOException {
        var path = AOC2025.getPath(day, test);
        var lijst = Files.readAllLines(path);
        for (var s: lijst) {
            var arr = s.split(",");
            var p = new Point(parseInt(arr[0]), parseInt(arr[1]));
            tiles.add(p);
        }
        
        var p1 = tiles.get(0);
        var start = p1;
        for (var i = 1; i < tiles.size(); i++) {
            var p2 = tiles.get(i);
            var d = DirectionC.getDirection(p1, p2);
            if (d == DirectionC.ROW) {
                rowMap.computeIfAbsent(p1.y, k -> new ArrayList<>()).add(SegmentC.of(p1, p2));
                if (i == tiles.size() - 1) {
                    rowMap.get(p1.y).add(SegmentC.of(p2, start));
                }
            }
            else {
                colMap.computeIfAbsent(p1.x, k -> new ArrayList<>()).add(SegmentC.of(p1, p2));
                if (i == tiles.size() - 1) {
                    colMap.get(p1.x).add(SegmentC.of(p2, start));
                }
            }
            p1 = p2;
        }
        sortedRows = rowMap.keySet().stream().sorted().distinct().toList();
        sortedCols = colMap.keySet().stream().sorted().distinct().toList();
    }
    
    public static void drawPoint(Point p, Graphics2D g, int size) {
        var e = new Rectangle(p.x - size / 2, p.y - size / 2, size, size);
        g.fill(e);
    }
    
    
}

enum DirectionC {
    ROW, COL;
    
    public static DirectionC getDirection(Point p1, Point p2) {
        var d = p1.x == p2.x ? DirectionC.COL : DirectionC.ROW;
        return d;
    }
}

record SegmentC(int rowOrCol, int f, int s, DirectionC d) {

    public static SegmentC of(Point p1, Point p2) {
        var startx = Integer.min(p1.x, p2.x);
        var endx = Integer.max(p1.x, p2.x);
        var starty = Integer.min(p1.y, p2.y);
        var endy = Integer.max(p1.y, p2.y);
        var dir = p1.x == p2.x ? DirectionC.COL : DirectionC.ROW;
        var result = dir == DirectionC.ROW ? new SegmentC(p1.y, startx, endx, dir) :
                                           new SegmentC(p1.x, starty, endy, dir) 
        ;
        return result;
    }
    
    public static Comparator<Point> getComparator(DirectionC d) {
        Comparator<Point> byCol = Comparator.comparingInt(p -> p.x);
        Comparator<Point> byRow = Comparator.comparingInt(p -> p.y);
        return d.equals(DirectionC.ROW) ? byCol : byRow;
    }
    
    public void draw(int r, Graphics2D g) {
        var oldcol = g.getColor();
        g.setColor(Color.RED);
        if (d == DirectionC.ROW) {
            g.drawLine(f, rowOrCol, s, rowOrCol);
        }
        else {
            g.drawLine(rowOrCol, f, rowOrCol, s);
        }
    }
    
    public long getLength() {
        return s - f + 1;
    }
}
