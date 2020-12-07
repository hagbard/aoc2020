package com.example;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Regex {

  public static void acceptMatch(Pattern p, String s, Consumer<String[]> fn) {
    applyMatch(p, s, g -> { fn.accept(g); return null; });
  }

  public static <V> V applyMatch(Pattern p, String s, Function<String[], V> fn) {
    Matcher m = p.matcher(s);
    checkArgument(m.matches(), "no match for '%s' using: %s", p, s);
    String[] groups = new String[m.groupCount()];
    for (int n = 1; n <= m.groupCount(); n++) {
      groups[n - 1] = m.group(n);
    }
    return fn.apply(groups);
  }

  private Regex() {}
}
