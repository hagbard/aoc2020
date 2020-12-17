package com.example.day17;

import static com.example.Vec4.vec;
import static com.google.common.collect.ImmutableMultiset.toImmutableMultiset;
import static com.google.common.collect.ImmutableSet.toImmutableSet;
import static java.util.stream.Collectors.toList;

import com.example.Input;
import com.example.Vec4;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multiset;
import com.google.common.collect.Range;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

// Count = 315
// Count = 1520
public class Main {
  private static final Vec4 ZERO = vec(0, 0, 0, 0);

  private static final Predicate<Vec4> NOT_ZERO = Predicate.<Vec4>isEqual(ZERO).negate();

  private static final ImmutableSet<Vec4> ALL_3 =
      Stream.of(ZERO)
          .flatMap(e -> Stream.of(e.add(vec(-1, 0, 0, 0)), e, e.add(vec(1, 0, 0, 0))))
          .flatMap(e -> Stream.of(e.add(vec(0, -1, 0, 0)), e, e.add(vec(0, 1, 0, 0))))
          .flatMap(e -> Stream.of(e.add(vec(0, 0, -1, 0)), e, e.add(vec(0, 0, 1, 0))))
          .collect(toImmutableSet());

  private static final ImmutableSet<Vec4> ALL_4 =
      ALL_3.stream()
          .flatMap(e -> Stream.of(e.add(vec(0, 0, 0, -1)), e, e.add(vec(0, 0, 0, 1))))
          .collect(toImmutableSet());

  private static final Range<Long> LIVE = Range.closed(2L, 3L);

  public static void main(String[] args) {
    ImmutableSet<Vec4> start = getStart();
    System.out.format("Count = %d\n", iterate(start, 6, ALL_3));
    System.out.format("Count = %d\n", iterate(start, 6, ALL_4));
  }

  private static ImmutableSet<Vec4> getStart() {
    ImmutableSet.Builder<Vec4> start = ImmutableSet.builder();
    int i = 0;
    for (String line : Input.getLines(Main.class).collect(toList())) {
      for (int j = 0; j < line.length(); j++) {
        if (line.charAt(j) == '#') {
          start.add(vec(j, i, 0, 0));
        }
      }
      i++;
    }
    return start.build();
  }

  private static int iterate(ImmutableSet<Vec4> start, int remaining, ImmutableSet<Vec4> adj) {
    HashSet<Vec4> live = new HashSet<>(start);
    while (remaining-- > 0) {
      Set<Vec4> toLive = live.stream()
          .flatMap(e -> nextTo(e, adj).filter(n -> !live.contains(n)))
          .collect(toImmutableMultiset())
          .entrySet().stream()
          .filter(e -> e.getCount() == 3)
          .map(Multiset.Entry::getElement)
          .collect(toImmutableSet());

      Set<Vec4> toDie = live.stream()
          .filter(e -> !LIVE.contains(nextTo(e, adj).filter(live::contains).count()))
          .collect(toImmutableSet());

      live.addAll(toLive);
      live.removeAll(toDie);
    }
    return live.size();
  }

  private static Stream<Vec4> nextTo(Vec4 e, ImmutableSet<Vec4> adj) {
    return adj.stream().filter(NOT_ZERO).map(e::add);
  }
}
