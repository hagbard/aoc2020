package com.example.day5;

import static com.google.common.collect.ImmutableSortedSet.toImmutableSortedSet;

import com.example.Input;
import com.google.common.collect.ContiguousSet;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;

// max ID = 906
// my ID = 519
public class Main {

  public static void main(String[] args) {
    ImmutableSortedSet<Integer> ids = Input.getLines(Main.class)
        .map(s -> s.replaceAll("[BR]", "1").replaceAll("[FL]", "0"))
        .map(s -> Integer.parseUnsignedInt(s, 2))
        .collect(toImmutableSortedSet(Ordering.natural()));
    System.out.format("max ID = %d\n", ids.last());
    System.out.format("my ID = %s\n",
        Iterables.getOnlyElement(Sets.difference(ContiguousSet.closed(ids.first(), ids.last()), ids)));
  }
}
