package com.example.day2;

import static com.example.Regex.applyMatch;
import static java.lang.Integer.parseUnsignedInt;

import com.example.Input;
import java.util.List;
import java.util.regex.Pattern;

// Valid = 396
// Valid = 428
public class Main {
  private static final Pattern ENTRY = Pattern.compile("(\\d+)-(\\d+) ([a-z]): (.*)");

  public static void main(String[] args) {
    List<String> input = Input.get(Main.class);
    System.out.format("Valid = %d\n", input.stream().filter(s -> check(s, Main::isValidFoo)).count());
    System.out.format("Valid = %d\n", input.stream().filter(s -> check(s, Main::isValidBar)).count());
  }

  interface Checker {
    boolean check(int a, int b, char c, String pw);
  }

  private static boolean check(String s, Checker c) {
    return applyMatch(ENTRY, s, g -> c.check(parseUnsignedInt(g[0]), parseUnsignedInt(g[1]), g[2].charAt(0), g[3]));
  }

  private static boolean isValidFoo(int min, int max, char chr, String pw) {
    long count = pw.codePoints().filter(cp -> cp == chr).count();
    return min <= count && count <= max;
  }

  private static boolean isValidBar(int i, int j, char chr, String pw) {
    return chr == pw.charAt(i - 1) ^ chr == pw.charAt(j - 1);
  }
}
