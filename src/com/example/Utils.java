package com.example;

import static com.google.common.base.Preconditions.checkArgument;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Utils {

  public static void match(Pattern p, String s, Consumer<List<String>> fn) {
    Matcher m = p.matcher(s);
    checkArgument(m.matches(), "no match for '%s' using: %s", p, s);
    List<String> groups = new ArrayList<>();
    for (int n = 1; n <= m.groupCount(); n++) {
      groups.add(m.group(1));
    }
    fn.accept(groups);
  }

  private Utils() {}
}
