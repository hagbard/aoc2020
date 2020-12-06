package com.example.day3;

import static com.google.common.base.Preconditions.checkState;
import static java.util.stream.Collectors.toList;

import com.example.Input;
import java.util.List;

// Trees = 167
// Result = 736527114
public class Main {

  public static void main(String[] args) {
    List<Integer> masks = Input.getLines(Main.class).map(Main::asInteger).collect(toList());

    int count = countTrees(masks, 3, 1);
    System.out.format("Trees = %d\n", count);

    int result = countTrees(masks, 1, 1)
        * countTrees(masks, 3, 1)
        * countTrees(masks, 5, 1)
        * countTrees(masks, 7, 1)
        * countTrees(masks, 1, 2);
    System.out.format("Result = %d\n", result);
  }

  private static int countTrees(List<Integer> masks, int skip, int jump) {
    int offset = 0;
    int count = 0;
    for (int i = 0; i < masks.size(); i += jump) {
      count += (masks.get(i) & (1 << offset)) != 0 ? 1 : 0;
      offset = (offset + skip) % 31;
    }
    return count;
  }

  private static Integer asInteger(String s) {
    checkState(s.length() == 31);
    int mask = 0;
    for (int n = 0; n < 31; n++) {
      if (s.charAt(n) == '#') {
        mask |= 1 << n;
      }
    }
    return mask;
  }
}
