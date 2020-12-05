package com.example.day2;

import static com.example.Checks.checkState;
import static java.lang.Integer.parseUnsignedInt;

import com.example.Checks;
import com.example.Input;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
  private static final Pattern ENTRY = Pattern.compile("(\\d+)-(\\d+) ([a-z]): (.*)");

  public static void main(String[] args) {
    List<String> input = Input.getInput(Main.class);
    System.out.format("Valid = %d\n", input.stream().filter(s -> check(s, Main::isValidFoo)).count());
    System.out.format("Valid = %d\n", input.stream().filter(s -> check(s, Main::isValidBar)).count());
  }

  interface Checker {
    boolean check(int a, int b, char c, String pw);
  }

  static boolean check(String s, Checker c) {
    Matcher m = ENTRY.matcher(s);
    checkState(m.matches());
    return c.check(parseUnsignedInt(m.group(1)), parseUnsignedInt(m.group(2)), m.group(3).charAt(0), m.group(4));
  }

  private static boolean isValidFoo(int min, int max, char chr, String pw) {
    long count = pw.codePoints().filter(cp -> cp == chr).count();
    return min <= count && count <= max;
  }

  private static boolean isValidBar(int i, int j, char chr, String pw) {
    return chr == pw.charAt(i - 1) ^ chr == pw.charAt(j - 1);
  }
}
