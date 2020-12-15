package com.example.day15;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Main {
  public static void main(String[] args) {
    System.out.format("Answer = %d\n", getAnswer(2020, 9, 19, 1, 6, 0, 5, 4));
    System.out.format("Answer = %d\n", getAnswer(30000000, 9, 19, 1, 6, 0, 5, 4));
  }

  private static int getAnswer(int endRound, int... inputs) {
    Map<Integer, Integer> map = new HashMap<>();
    for (int n = 0; n < inputs.length; n++) {
      map.put(inputs[n], n + 1);
    }
    int round = inputs.length;
    int value = inputs[round - 1];
    do {
      Optional<Integer> lastIndex = Optional.ofNullable(map.put(value, round));
      int lastRound = round++;
      value = lastIndex.map(n -> lastRound - n).orElse(0);
    } while (round < endRound);
    return value;
  }
}
