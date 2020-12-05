package com.example.day5;

import static com.google.common.collect.ImmutableSortedSet.toImmutableSortedSet;

import com.example.Input;
import com.google.common.collect.ContiguousSet;
import com.google.common.collect.ImmutableRangeSet;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Ordering;
import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import com.google.common.collect.Sets;
import java.util.BitSet;
import java.util.List;

public class Main {

  public static void main(String[] args) {
    List<String> input = Input.getInput(Main.class);
    ImmutableSortedSet<Integer> ids = input.stream().map(s -> s.replaceAll("[BR]", "1").replaceAll("[FL]", "0"))
        .map(s -> Integer.parseUnsignedInt(s, 2)).collect(toImmutableSortedSet(Ordering.natural()));
    System.out.format("max ID = %d\n", ids.last());
    System.out.format("my ID = %s\n",
        Iterables.getOnlyElement(Sets.difference(ContiguousSet.closed(ids.first(), ids.last()), ids)));
  }
}
