package com.example.day1;

import static java.util.stream.Collectors.toSet;

import com.example.Input;
import java.util.Optional;
import java.util.Set;

// 51 x 1969 = 100419
// 565 x 972 x 483 = 265253940
// 972 x 565 x 483 = 265253940
// 483 x 565 x 972 = 265253940
public class Main {

  public static void main(String[] args) {
    Set<Long> values = Input.getLines(Main.class).mapToLong(Long::parseUnsignedLong).boxed().collect(toSet());

    findResultFor(2020, values)
        .ifPresent(n -> System.out.format("%d x %d = %d\n", n, 2020 - n, n * (2020 - n)));

    values.forEach(n -> findResultFor(2020 - n, values)
        .ifPresent(m -> System.out.format("%d x %d x %d = %d\n", n, m, ((2020 - n) - m), n * m * ((2020 - n) - m))));
  }

  private static Optional<Long> findResultFor(long target, Set<Long> values) {
    return values.stream().filter(n -> values.contains(target - n)).findFirst();
  }
}
