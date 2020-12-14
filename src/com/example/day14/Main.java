package com.example.day14;

import static java.lang.Long.parseUnsignedLong;

import com.example.Input;
import com.example.Regex;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

// Sum = 8332632930672
// Sum = 4753238784664
public abstract class Main {

  public static void main(String[] args) {
    Main foo = new FooProc();
    Input.getLines(Main.class).forEach(foo::handleLine);
    System.out.format("Sum = %d\n", foo.sum());

    Main bar = new BarProc();
    Input.getLines(Main.class).forEach(bar::handleLine);
    System.out.format("Sum = %d\n", bar.sum());
  }

  private static final Pattern MASK = Pattern.compile("^mask = ([01X]{36})$");
  private static final Pattern MEM = Pattern.compile("^mem\\[(\\d+)\\] = (\\d+)$");

  final Map<Long, Long> memory = new HashMap<>();

  void handleLine(String line) {
    if (line.startsWith("mask")) {
      Regex.acceptMatch(MASK, line, g -> updateMasks(g[0]));
    } else {
      Regex.acceptMatch(MEM, line, g -> writeMem(parseUnsignedLong(g[0]), parseUnsignedLong(g[1])));
    }
  }

  abstract void updateMasks(String mask);

  abstract void writeMem(long addr, long value);

  private long sum() {
    return memory.values().stream().mapToLong(Long::longValue).sum();
  }

  static class FooProc extends Main {
    private long onesMask = 0L;
    private long keepMask = 0L;

    void updateMasks(String mask) {
      onesMask = parseUnsignedLong(mask.replace('X', '0'), 2);
      keepMask = parseUnsignedLong(mask.replace('X', '1'), 2);
    }

    void writeMem(long addr, long value) {
      memory.put(addr, (value | onesMask) & keepMask);
    }
  }

  static class BarProc extends Main {
    private long onesMask = 0L;
    private long floatMask = 0L;
    private byte[] floatBits = null;

    void updateMasks(String mask) {
      onesMask = parseUnsignedLong(mask.replace('X', '0'), 2);
      floatMask = parseUnsignedLong(mask.replace('X', '1'), 2) & ~onesMask;
      floatBits = maskToIndices(floatMask);
    }

    void writeMem(long addr, long value) {
      addr = (addr | onesMask) & ~floatMask;
      for (int n = 0; n < (1 << floatBits.length); n++) {
        memory.put(addr | getFloatMask(n), value);
      }
    }

    private byte[] maskToIndices(long mask) {
      byte[] arr = new byte[Long.bitCount(mask)];
      for (int n = 0, bit = 0; mask != 0; n++, bit++) {
        int skipBits = Long.numberOfTrailingZeros(mask);
        mask >>>= skipBits + 1;
        bit += skipBits;
        arr[n] = (byte) bit;
      }
      return arr;
    }

    private long getFloatMask(int n) {
      return IntStream.range(0, floatBits.length)
          .filter(i -> (n & (1 << i)) != 0)
          .mapToLong(i -> 1L << floatBits[i])  // Bastards!
          .reduce(0L, (a, b) -> a | b);
    }
  }
}
