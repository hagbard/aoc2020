package com.example.day16;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.collect.ImmutableMap.toImmutableMap;
import static com.google.common.collect.Maps.immutableEntry;
import static java.lang.Integer.numberOfTrailingZeros;
import static java.lang.Integer.parseUnsignedInt;
import static java.util.Arrays.asList;

import com.example.Input;
import com.example.Regex;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableRangeSet;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

// Sum = 28884
// Result = 1001849322119
public class Main {
  private static final Pattern RANGE = Pattern.compile("([^:]+): (\\d+)-(\\d+) or (\\d+)-(\\d+)");
  private static final Splitter COMMA = Splitter.on(',');

  public static void main(String[] args) {
    List<List<String>> input = Input.getGroups(Main.class).collect(toImmutableList());

    ImmutableMap<String, RangeSet<Integer>> ranges = input.get(0).stream()
        .map(r -> Regex.applyMatch(
            RANGE, r, g -> immutableEntry(g[0], rangeSet(range(g[1], g[2]), range(g[3], g[4])))))
        .collect(toImmutableMap(Map.Entry::getKey, Map.Entry::getValue));

    ImmutableList<Range<Integer>> allRanges =
        ranges.values().stream().flatMap(r -> r.asRanges().stream()).collect(toImmutableList());
    RangeSet<Integer> neverValid = ImmutableRangeSet.unionOf(allRanges).complement();

    List<int[]> data = input.get(2).stream().skip(1).map(Main::decode).collect(toImmutableList());
    checkArgument(data.stream().mapToInt(r -> r.length).distinct().count() == 1);
    int colCount = data.get(0).length;

    int sum = data.stream().flatMapToInt(Arrays::stream).filter(neverValid::contains).sum();
    System.out.format("Sum = %d\n", sum);

    ImmutableList<int[]> validData =
        data.stream()
            .filter(r -> Arrays.stream(r).noneMatch(neverValid::contains))
            .collect(toImmutableList());

    ImmutableMap<String, Integer> valid =
        Maps.transformValues(
            ranges,
            range -> IntStream.range(0, colCount)
                .filter(i -> validData.stream().mapToInt(row -> row[i]).allMatch(range::contains))
                .map(i -> 1 << i)
                .reduce(0, (a, b) -> a | b))
        .entrySet()
        .stream()
        .sorted(Map.Entry.comparingByValue())
        .collect(toImmutableMap(Map.Entry::getKey, Map.Entry::getValue));

    int[] ticket = decode(input.get(1).get(1));
    long result = 1L;
    int mask = 0;
    for (Map.Entry<String, Integer> e : valid.entrySet()) {
      int m = e.getValue() & ~mask;
      checkArgument(Integer.bitCount(m) == 1);
      if (e.getKey().startsWith("departure")) {
        result *= ticket[numberOfTrailingZeros(m)];
      }
      mask = e.getValue();
    }
    System.out.format("Result = %d\n", result);
  }

  private static Range<Integer> range(String lo, String hi) {
    return Range.closed(parseUnsignedInt(lo), parseUnsignedInt(hi));
  }

  private static ImmutableRangeSet<Integer> rangeSet(Range<Integer> a, Range<Integer> b) {
    return ImmutableRangeSet.unionOf(asList(a, b));
  }

  private static int[] decode(String s) {
    return COMMA.splitToList(s).stream().mapToInt(Integer::parseUnsignedInt).toArray();
  }
}
