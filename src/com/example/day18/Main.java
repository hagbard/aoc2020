package com.example.day18;

import static com.google.common.base.CharMatcher.whitespace;
import static java.lang.Long.parseLong;

import com.example.Input;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

// Filthy = 6640667297513
// Dirty = 451589894841552
public class Main {
  private static final Pattern SEQ = Pattern.compile("\\(([^()]+)\\)");
  private static final Pattern ADD_OR_MUL = Pattern.compile("([0-9]+)([+*])([0-9]+)");
  private static final Pattern ADD = Pattern.compile("([0-9]+)([+])([0-9]+)");
  private static final Pattern MUL = Pattern.compile("([0-9]+)([*])([0-9]+)");

  public static void main(String[] args) {
    System.out.format("Filthy = %d\n", Input.getLines(Main.class).mapToLong(Main::part1).sum());
    System.out.format("Dirty = %d\n", Input.getLines(Main.class).mapToLong(Main::part2).sum());
  }

  private static long part1(String s) {
    return parseLong(fold("(" + whitespace().removeFrom(s) + ")", t -> eval(t, ADD_OR_MUL)));
  }

  private static long part2(String s) {
    return parseLong(fold("(" + whitespace().removeFrom(s) + ")", t -> eval(eval(t, ADD), MUL)));
  }

  private static String fold(String s, Function<String, String> fn) {
    String t;
    do {
      s = SEQ.matcher(t = s).replaceAll(r -> fn.apply(r.group(1)));
    } while (!s.equals(t));
    return s;
  }

  private static String eval(String s, Pattern op) {
    String t;
    do {
      s = op.matcher(t = s).replaceFirst(Main::fn);
    } while (!s.equals(t));
    return s;
  }

  private static String fn(MatchResult r) {
    return "+".equals(r.group(2))
        ? Long.toString(parseLong(r.group(1)) + parseLong(r.group(3)))
        : Long.toString(parseLong(r.group(1)) * parseLong(r.group(3)));
  }
}
