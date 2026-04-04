/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package aoc2025;

import java.io.IOException;
import static java.lang.Integer.parseInt;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Piet
 */
public class day_____12 {
    
    private static int day = 12;
    private static boolean test = false;
    
    private static List<Present> presents = new ArrayList<>();
    private static List<Grid> grids = new ArrayList<>();
    
    public static void main(String... args) throws IOException {
        getInput();
        solveA();
    }
    
    private static void getInput() throws IOException {
        var path = AOC2025.getPath(day, test);
        var input = Files.readAllLines(path);
        // presents
        for (int i = 0; i < input.size();) {
            var s = input.get(i);
            if (s.isEmpty()) {
                i++;
                continue;
            }
            if (s.charAt(1) == ':') {
                presents.add(Present.of(input.subList(i, i + 4)));
                i += 4;
            }
            
            if (s.contains("x")) {
                grids.add(Grid.of(s));
                i++;
            }
        }    
    }
    
    private static void solveA() {
        var result = 0;
        for (var g: grids) {
            if (g.fits()) {
                result++;
            }
        }
        System.out.println("A: " + result);
    }

    record Present(int id, char[][] chars, int hekjes) {
    
        public static Present of(List<String> s) {
            var id = s.get(0).charAt(0) - '0';
            var hekjes = 0;
            var chars = new char[3][];
            for (int i = 0; i < 3; i++) {
                chars[i] = s.get(i + 1).toCharArray();
                for (int j = 0; j < 3; j++) {
                    if (chars[i][j] == '#') hekjes++;
                }
            }
            return new Present(id, chars, hekjes);
        }
    }
    
    record Grid(int width, int height, List<Integer> numbers) {
    
        public static Grid of(String s) {
            var arr = s.split(":");
            var arr2 = arr[0].split("x");
            int width = parseInt(arr2[0]);
            int height = parseInt(arr2[1]);
            var numbers = Arrays.stream(arr[1].trim().split(" "))
                .mapToInt(Integer::parseInt)
                .boxed()
                .toList()
            ;
            return new Grid(width, height, numbers);
        }
    
        public boolean fits() {
            // test 1: hoeveel presents passen er in totaal?
            var horizontal = width % 3;
            var vertical = height % 3;
            var total = horizontal * vertical;
            var nrOfPresents = numbers.stream().mapToInt(i -> i).sum();
            if (total >= nrOfPresents) {
                return true;
            }
            // test 2
            var totalAvailable = width * height;
            var toFit = 0;
            for (int i = 0; i < numbers.size(); i++) {
                toFit += numbers.get(i) * presents.get(i).hekjes();
            }
            if (toFit <= totalAvailable) {
                return true;
            } 
            return false;
        }
    }

}
