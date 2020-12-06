package com.example.day6;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.ImmutableList.toImmutableList;

import com.example.Input;
import com.google.common.collect.ImmutableList;
import java.util.Arrays;
import java.util.function.IntBinaryOperator;

// Sum (OR) = 6742
// Sum (AND) = 3447
public class Main {
  public static void main(String[] args) {
    ImmutableList<int[]> masks =
        Input.getGroups(Main.class).map(g -> g.stream().mapToInt(Main::toMask).toArray()).collect(toImmutableList());
    System.out.format("Sum (OR) = %d\n", sumAnswers(masks, (a, b) -> a | b));
    System.out.format("Sum (AND) = %d\n", sumAnswers(masks, (a, b) -> a & b));
  }

  private static int toMask(String s) {
    return s.codePoints().map(Main::toMaskBit).reduce(0, (a, b) -> a | b);
  }

  private static int toMaskBit(int c) {
    checkArgument('a' <= c && c <= 'z', "%s", c);
    return 1 << (c - 'a');
  }

  private static int sumAnswers(ImmutableList<int[]> masks, IntBinaryOperator reduce) {
    return masks.stream().mapToInt(m -> Arrays.stream(m).reduce(reduce).orElse(0)).map(Integer::bitCount).sum();
  }
}
