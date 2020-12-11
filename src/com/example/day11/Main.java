package com.example.day11;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.collect.ImmutableMultiset.toImmutableMultiset;
import static java.util.function.Predicate.isEqual;

import com.example.Input;
import com.google.common.collect.ImmutableList;
import com.google.common.primitives.Booleans;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.IntStream;

// Count = 2468
// Count = 2214
public class Main {

  private final Map<Integer, Boolean> state = new HashMap<>();
  private final int size;

  private Main(ImmutableList<String> lines) {
    this.size = lines.size();
    for (int y = 0; y < size; y++) {
      String row = lines.get(y);
      checkArgument(row.length() == size);
      for (int x = 0; x < size; x++) {
        if (row.charAt(x) == 'L') {
          state.put(coord(x, y), false);
        }
      }
    }
  }

  private void reset() {
    state.entrySet().forEach(e -> e.setValue(false));
  }

  public static void main(String[] args) {
    Main puzzle = new Main(Input.getLines(Main.class).collect(toImmutableList()));
    System.out.format("Count = %d\n", puzzle.processSeating(puzzle::shouldFlipFoo));
    puzzle.reset();
    System.out.format("Count = %d\n", puzzle.processSeating(puzzle::shouldFlipBar));
  }

  private long processSeating(Predicate<Integer> shouldFlip) {
    long count = 0;
    long lastCount;
    do {
      lastCount = count;
      state.keySet().stream()
          .filter(shouldFlip)
          .collect(toImmutableList())
          .forEach(p -> state.put(p, !state.get(p)));
      count = state.values().stream().filter(isEqual(true)).count();
    } while (count != lastCount);
    return count;
  }

  private boolean shouldFlipFoo(int pos) {
    long count = IntStream.of(
        adj(pos, -1, -1), adj(pos, 0, -1), adj(pos, +1, -1),
        adj(pos, -1, 0), /*-------------*/ adj(pos, +1, 0),
        adj(pos, -1, +1), adj(pos, 0, +1), adj(pos, +1, +1))
        .filter(this::isOccupied)
        .count();
    return state.get(pos) ? count >= 4 : count == 0;
  }

  private boolean shouldFlipBar(int pos) {
    long count = IntStream.of(
        see(pos, -1, -1), see(pos, 0, -1), see(pos, +1, -1),
        see(pos, -1, 0), /*-------------*/ see(pos, +1, 0),
        see(pos, -1, +1), see(pos, 0, +1), see(pos, +1, +1))
        .sum();
    return state.get(pos) ? count >= 5 : count == 0;
  }

  private int adj(int pos, int xAdj, int yAdj) {
    int x = (pos & 0xFF) + xAdj;
    int y = (pos >>> 16) + yAdj;
    return (0 <= x && x < size && 0 <= y && y < size) ? coord(x, y) : -1;
  }

  private int see(int pos, int xAdj, int yAdj) {
    do {
      pos = adj(pos, xAdj, yAdj);
    } while (pos != -1 && !isChair(pos));
    return pos != -1 && isOccupied(pos) ? 1 : 0;
  }

  private Boolean isOccupied(int p) {
    return state.getOrDefault(p, false);
  }

  private Boolean isChair(int p) {
    return state.containsKey(p);
  }

  private static int coord(int x, int y) {
    return x + (y << 16);
  }
}
