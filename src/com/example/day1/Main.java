package com.example.day1;

import static java.util.stream.Collectors.toSet;

import com.example.Input;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class Main {

  public static void main(String[] args) {
    List<String> input = Input.getInput(Main.class);
    Set<Long> values = input.stream().mapToLong(Long::parseUnsignedLong).boxed().collect(toSet());

    findResultFor(2020, values)
        .ifPresent(n -> System.out.format("%d x %d = %d\n", n, 2020 - n, n * (2020 - n)));

    values.forEach(n -> findResultFor(2020 - n, values)
        .ifPresent(m -> System.out.format("%d x %d x %d = %d\n", n, m, ((2020 - n) - m), n * m * ((2020 - n) - m))));
  }

  private static Optional<Long> findResultFor(long target, Set<Long> values) {
    return values.stream().filter(n -> values.contains(target - n)).findFirst();
  }
}
