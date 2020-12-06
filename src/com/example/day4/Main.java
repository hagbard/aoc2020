package com.example.day4;

import static com.google.common.collect.ImmutableMap.toImmutableMap;
import static com.google.common.collect.ImmutableSet.toImmutableSet;
import static java.util.stream.Collectors.toList;

import com.example.Input;
import com.google.common.base.Splitter;
import com.google.common.base.Splitter.MapSplitter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.primitives.Ints;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;

// Valid = 254
// Valid = 184
public class Main {

  private enum Key {
    byr(true, inRange(1920, 2002)),
    iyr(true, inRange(2010, 2020)),
    eyr(true, inRange(2020, 2030)),
    hgt(true, inHeightRange(150, 193, "cm").or(inHeightRange(59, 76, "in"))),
    hcl(true, matches("#[0-9a-f]{6}")),
    ecl(true, matches("amb|blu|brn|gry|grn|hzl|oth")),
    pid(true, matches("[0-9]{9}")),
    cid(false, k -> true);

    private static Predicate<String> inRange(int min, int max) {
      return s -> {
        Integer n = Ints.tryParse(s);
        return n != null && min <= n && n <= max;
      };
    }

    private static Predicate<String> inHeightRange(int min, int max, String suffix) {
      Predicate<String> range = inRange(min, max);
      return s -> s.endsWith(suffix) && range.test(s.substring(0, s.length() - suffix.length()));
    }

    private static Predicate<String> matches(String s) {
      return Pattern.compile(s).asMatchPredicate();
    }

    private static final ImmutableSet<String> REQUIRED =
        Arrays.stream(values()).filter(k -> k.required).map(Enum::name).collect(toImmutableSet());

    private static final ImmutableMap<String, Predicate<String>> CHECKS =
        Arrays.stream(values()).collect(toImmutableMap(Enum::name, k -> k.check));

    public static boolean hasAllRequired(Set<String> keys) {
      return keys.containsAll(REQUIRED);
    }

    public static boolean isValid(String key, String value) {
      return CHECKS.get(key).test(value);
    }

    private final boolean required;
    private final Predicate<String> check;

    Key(boolean required, Predicate<String> check) {
      this.required = required;
      this.check = check;
    }
  }

  private static final MapSplitter MAP_SPLITTER = Splitter.on(' ').omitEmptyStrings().withKeyValueSeparator(':');
  private static final Predicate<Map<String, String>> HAS_REQUIRED = m -> Key.hasAllRequired(m.keySet());
  private static final Predicate<Map<String, String>> IS_VALID =
      HAS_REQUIRED.and(m -> m.entrySet().stream().allMatch(e -> Key.isValid(e.getKey(), e.getValue())));

  public static void main(String[] args) {
    String raw = Input.getAsString(Main.class).replaceAll("(\\S)\n", "$1 ");
    List<Map<String, String>> maps =
        Splitter.on('\n').trimResults().splitToList(raw).stream().map(MAP_SPLITTER::split).collect(toList());
    System.out.format("Valid = %d\n", maps.stream().filter(HAS_REQUIRED).count());
    System.out.format("Valid = %d\n", maps.stream().filter(IS_VALID).count());
  }
}
