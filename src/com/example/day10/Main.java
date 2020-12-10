package com.example.day10;

import static com.google.common.collect.ImmutableMultiset.toImmutableMultiset;

import com.example.Input;
import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.Iterables;
import com.google.common.primitives.Ints;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;

// Diffs = 1914
// Combos = 9256148959232
public class Main {

  public static void main(String[] args) {
    int[] input = IntStream.concat(IntStream.of(0),
        Input.getLines(Main.class).mapToInt(Integer::parseUnsignedInt).sorted()).toArray();

    ImmutableMultiset<Integer> diffs = IntStream.range(1, input.length)
        .map(n -> input[n] - input[n - 1]).boxed().collect(toImmutableMultiset());
    // Remember trailing 3 diff.
    System.out.format("Diffs = %d\n", diffs.count(1) * (diffs.count(3) + 1));

    long combos = 1L;
    for (int i = 0, j = 1; j <= input.length; j++) {
      if (j == input.length || input[j] - input[i] > j - i) {
        // Discovered a run of j - i consecutive values, but want min/max diff.
        combos *= fib3((j - i) - 1);
        i = j;
      }
    }
    System.out.format("Combos = %d\n", combos);
  }

  private static int fib3(int n) {
    return (n == 0) ? 1 : IntStream.range(Math.max(n - 3, 0), n).map(Main::fib3).sum();
  }
}
