package aoc2025;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 *
 * @author Piet
 */
public class AOC2025 {

    /**
     * @param args the command line arguments
     */
    public final static String clss = "AOC2025";
    public final static String path = String.format("G:/%s/src/%s/Resources/input_", clss, clss.toLowerCase());
    
    public static Path getPath(int day, boolean test) {
        var d = (day < 10 ? "0" : "") + day;
        var t = (test ? "_test" : "") + ".txt";
        var s = path + d + t;
        return Paths.get(s);
        
    }
    public static void main(String[] args) {
        var s = getPath(8, true);
    }
    
}
