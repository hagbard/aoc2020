package com.example.day11;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static java.util.function.Predicate.isEqual;

import com.example.Input;
import com.google.common.collect.ImmutableList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

// Count = 2468
// Count = 2214
public class Main {

  private final ImmutableList<Integer> DIRS = ImmutableList.of(
      vec(-1, -1), vec(0, -1), vec(+1, -1),
      vec(-1, 0), /*--------*/ vec(+1, 0),
      vec(-1, +1), vec(0, +1), vec(+1, +1));

  private final Map<Integer, Boolean> state = new HashMap<>();
  private final int size;

  private Main(ImmutableList<String> lines) {
    this.size = lines.size();
    for (int y = 0; y < size; y++) {
      String row = lines.get(y);
      checkArgument(row.length() == size);
      for (int x = 0; x < size; x++) {
        if (row.charAt(x) == 'L') {
          state.put(vec(x, y), false);
        }
      }
    }
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
    long count = DIRS.stream().map(d -> adj(pos, d)).filter(this::isOccupied).count();
    return state.get(pos) ? count >= 4 : count == 0;
  }

  private boolean shouldFlipBar(int pos) {
    long count = DIRS.stream().mapToInt(d -> see(pos, d)).sum();
    return state.get(pos) ? count >= 5 : count == 0;
  }

  private static int vec(int x, int y) {
    return (x & 0xFFFF) + (y << 16);
  }

  private int adj(int pos, int adj) {
    int x = ((short) pos) + ((short) adj);
    int y = (pos >> 16) + (adj >> 16);
    return (0 <= x && x < size && 0 <= y && y < size) ? vec(x, y) : -1;
  }

  // No BoolStream so fudge it by returning a 0/1 counter for matches and summing.
  private int see(int pos, int adj) {
    do {
      pos = adj(pos, adj);
    } while (pos != -1 && !isChair(pos));
    return pos != -1 && isOccupied(pos) ? 1 : 0;
  }

  private Boolean isOccupied(int p) {
    return state.getOrDefault(p, false);
  }

  private Boolean isChair(int p) {
    return state.containsKey(p);
  }

  private void reset() {
    state.entrySet().forEach(e -> e.setValue(false));
  }
}
