package com.example.day9;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.ImmutableList.toImmutableList;

import com.example.Input;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multiset;
import java.util.List;

// Not sum = 1398413738
// Weakness = 169521051
public class Main {

  public static void main(String[] args) {
    List<Long> input =
        Input.getLines(Main.class)
            .map(Long::parseUnsignedLong)
            .peek(n -> checkArgument(n > 0))
            .collect(toImmutableList());

    Long target = findNonSum(input);
    checkState(target != null && target > 0L);
    System.out.format("Not sum = %d\n", target);

    ImmutableList<Long> contig = getSortedSubsequence(input, target);
    System.out.format("Weakness = %d\n", contig.get(0) + Iterables.getLast(contig));
  }

  private static Long findNonSum(List<Long> input) {
    Multiset<Long> working = HashMultiset.create(input.subList(0, 25));
    Long target = null;
    for (int n = 25; n < input.size() && isSum(working, target = input.get(n)); n++) {
      working.remove(input.get(n - 25));
      working.add(target);
    }
    return target;
  }

  private static boolean isSum(Multiset<Long> working, long target) {
    for (Long a : working.elementSet()) {
      long b = target - a;
      if (b > 0 && b != a && working.contains(b)) {
        return true;
      }
    }
    return false;
  }

  private static ImmutableList<Long> getSortedSubsequence(List<Long> input, Long target) {
    int start = 0;
    int end = 0;
    for (long sum = 0L; sum != target;) {
      sum += (sum < target) ? input.get(end++) : -input.get(start++);
    }
    return ImmutableList.sortedCopyOf(input.subList(start, end));
  }
}
