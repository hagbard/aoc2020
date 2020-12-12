package com.example.day11;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static java.util.function.Predicate.isEqual;

import com.example.Input;
import com.example.Vec2;
import com.google.common.collect.ImmutableList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

// Count = 2468
// Count = 2214
public class Main {

  private final ImmutableList<Vec2> DIRS = ImmutableList.of(
      vec(-1, -1), vec(0, -1), vec(+1, -1),
      vec(-1, 0), /*--------*/ vec(+1, 0),
      vec(-1, +1), vec(0, +1), vec(+1, +1));

  private final Map<Vec2, Boolean> state = new HashMap<>();
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

  private long processSeating(Predicate<Vec2> shouldFlip) {
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

  private boolean shouldFlipFoo(Vec2 pos) {
    long count = DIRS.stream().map(d -> adj(pos, d)).filter(this::isOccupied).count();
    return state.get(pos) ? count >= 4 : count == 0;
  }

  private boolean shouldFlipBar(Vec2 pos) {
    long count = DIRS.stream().mapToInt(d -> see(pos, d)).sum();
    return state.get(pos) ? count >= 5 : count == 0;
  }

  private static Vec2 vec(int x, int y) {
    return Vec2.vec(x, y);
  }

  private Vec2 adj(Vec2 pos, Vec2 adj) {
    return pos.add(adj);
  }

  // No BoolStream so fudge it by returning a 0/1 counter for matches and summing.
  private int see(Vec2 pos, Vec2 adj) {
    Optional<Vec2> v = Optional.of(pos);
    do {
      pos = pos.add(adj);
    } while (inBounds(pos) && !isChair(pos));
    return inBounds(pos) && isOccupied(pos) ? 1 : 0;
  }

  private Boolean isOccupied(Vec2 pos) {
    return state.getOrDefault(pos, false);
  }

  private Boolean isChair(Vec2 pos) {
    return state.containsKey(pos);
  }

  public boolean inBounds(Vec2 pos) {
    return 0 <= pos.x && pos.x < size && 0 <= pos.y && pos.y < size;
  }

  private void reset() {
    state.entrySet().forEach(e -> e.setValue(false));
  }
}
