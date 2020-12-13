package com.example.day13;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static java.math.BigInteger.ONE;
import static java.math.BigInteger.ZERO;
import static java.math.BigInteger.valueOf;
import static java.util.Comparator.comparingLong;
import static java.util.function.Predicate.isEqual;
import static java.util.stream.Collectors.toList;

import com.example.Input;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// Depart = 3966
// Result = 800177252346225
public class Main {

  public static void main(String[] args) {
    ImmutableList<String> input = Input.getLines(Main.class).collect(toImmutableList());
    long ts = Long.parseUnsignedLong(input.get(0));
    List<String> list = Splitter.on(',').trimResults().splitToList(input.get(1));
    System.out.format("Depart = %d\n",
        list.stream()
            .filter(isEqual("x").negate())
            .mapToLong(Long::parseUnsignedLong)
            .mapToObj(n -> Maps.immutableEntry(n, n - (ts % n)))
            .sorted(comparingLong(Map.Entry::getValue))
            .mapToLong(e -> e.getKey() * e.getValue())
            .findFirst()
            .getAsLong());

    List<BigInteger> numbers = list.stream().map(Main::parseBigIntegerOrNull).collect(toList());
    System.out.format("Result = %d\n", getStartTime(numbers));
  }

  private static BigInteger getStartTime(List<BigInteger> list) {
    // Repeated invocation of: https://en.wikipedia.org/wiki/Chinese_remainder_theorem
    //
    // For input (where N exists):
    //   startTime + offset ≡ 0 (mod list[offset])
    // so:
    //   startTime ≡ -offset (mod list[offset])
    // which is why we subtract later on.
    BigInteger startTime = ZERO;
    BigInteger cummulativePeriod = list.get(0);
    for (int n = 1; n < list.size(); n++) {
      if (list.get(n) == null) {
        continue;
      }
      BigInteger newOffset = valueOf(n);
      BigInteger newPeriod = list.get(n);
      // Get GCD via extended Euclidean algorithm, obtaining Bézout coefficients.
      // https://en.wikipedia.org/wiki/B%C3%A9zout%27s_identity
      BigInteger[] gcd = extendedGcd(cummulativePeriod, newPeriod);
      // GCD itself is always 1 since all values are co-prime.
      checkState(gcd[2].equals(ONE));

      // x = a1*m2*n2 + a2*m1*n1 : But offset (a2) is always negated so subtract directly.
      BigInteger x = startTime.multiply(gcd[1]).multiply(newPeriod)
          .subtract(newOffset.multiply(gcd[0]).multiply(cummulativePeriod));

      cummulativePeriod = cummulativePeriod.multiply(newPeriod);
      startTime = x.mod(cummulativePeriod);
    }
    return startTime;
  }

  // https://en.wikipedia.org/wiki/Extended_Euclidean_algorithm
  //
  // Binary friendly version: http://cacr.uwaterloo.ca/hac/about/chap14.pdf#page=17
  // ---------------- gcd(693, 609) ----------------
  // u   v    A    B    C    D
  // 693 609  1    0    0    1
  // 84  609  1   −1    0    1
  // 42  609  305 −347  0    1
  // 21  609  457 −520  0    1
  // 21  588  457 −520 −457  521
  // 21  294  457 −520  76  −86
  // 21  147  457 −520  38  −43
  // 21  126  457 −520 −419  477
  // 21  63   457 −520  95  −108
  // 21  42   457 −520 −362  412
  // 21  21   457 −520 −181  206
  // 0   21   638 −726 −181  206
  // --------------------------------
  // a = −181, b = 206, v = 21
  //
  // INPUT: two positive integers x and y.
  // OUTPUT: integers [a, b, v] such that ax + by = v, where v = gcd(x, y).
  static BigInteger[] extendedGcd(BigInteger x, BigInteger y) {
    // --- Prelude: Work out scaling factor ---
    // 1. g←1.
    // 2. While x and y are both even, do the following: x←x/2, y←y/2, g←2g.
    // NOTE: Just shifting x & y by min# trailing zeros and recording in gShift.
    int gShift = Math.min(x.getLowestSetBit(), y.getLowestSetBit());
    x = x.shiftRight(gShift);
    y = y.shiftRight(gShift);

    // --- Main algorithm starts here ---
    // 3. u←x, v←y, A←1, B←0, C←0, D←1.
    BigInteger u = x, v = y, A = ONE, B = ZERO, C = ZERO, D = ONE;
    do {
      // System.out.format("u=%4d, v=%4d, A=%4d, B=%4d, C=%4d, D=%4d\n", u, v, A, B, C, D);
      // 4. While u is even do the following:
      while (isEven(u)) {
        // 4.1 u←u/2.
        u = u.shiftRight(1);
        // 4.2 If A ≡ B ≡ 0 (mod 2) then A←A/2, B←B/2; otherwise, A←(A + y)/2, B←(B − x)/2.
        if (!isEven(A) || !isEven(B)) {
          A = A.add(y);
          B = B.subtract(x);
        }
        A = A.shiftRight(1);
        B = B.shiftRight(1);
        // System.out.format("u=%4d, v=%4d, A=%4d, B=%4d, C=%4d, D=%4d\n", u, v, A, B, C, D);
      }
      // 5. While v is even do the following:
      while (isEven(v)) {
        // 5.1 v←v/2.
        v = v.shiftRight(1);
        // 5.2 If C ≡ D ≡ 0 (mod 2) then C←C/2, D←D/2; otherwise, C←(C + y)/2, D←(D − x)/2.
        if (!isEven(C) || !isEven(D)) {
          C = C.add(y);
          D = D.subtract(x);
        }
        C = C.shiftRight(1);
        D = D.shiftRight(1);
        // System.out.format("u=%4d, v=%4d, A=%4d, B=%4d, C=%4d, D=%4d\n", u, v, A, B, C, D);
      }
      // 6. If u ≥ v
      if (u.compareTo(v) >= 0) {
        // then u←u − v, A←A − C, B←B − D;
        u = u.subtract(v);
        A = A.subtract(C);
        B = B.subtract(D);
      } else {
        // otherwise, v←v − u, C←C − A, D←D − B.
        v = v.subtract(u);
        C = C.subtract(A);
        D = D.subtract(B);
      }
      // 7. If u = 0, then a←C, b←D, and return(a, b, g · v); otherwise, go to step 4.
    } while (!u.equals(ZERO));
    return new BigInteger[]{C, D, v.shiftRight(gShift)};
  }

  private static boolean isEven(BigInteger n) {
    return n.getLowestSetBit() > 0;
  }

  private static BigInteger parseBigIntegerOrNull(String n) {
    return "x".equals(n) ? null : valueOf(Integer.parseUnsignedInt(n));
  }
}
