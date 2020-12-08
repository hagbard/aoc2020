package com.example.day8;

import static com.example.Regex.applyMatch;
import static com.google.common.base.Preconditions.checkState;
import static java.lang.Integer.parseInt;

import com.example.Input;
import com.example.Regex;
import java.util.BitSet;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Stream;

// Acc = 1744
// ........................................
// Acc = 1174
public class Main {
  private static final Pattern INST = Pattern.compile("(nop|acc|jmp) ([+-]\\d+)");

  enum IType {
    acc, jmp, nop;
  }

  private static final class Inst {
    final IType type;
    final int value;

    public Inst(IType type, int value) {
      this.type = type;
      this.value = value;
    }
  }

  public static void main(String[] args) {
    Inst[] code = Input.getLines(Main.class)
        .map(s -> applyMatch(INST, s, g -> new Inst(IType.valueOf(g[0]), parseInt(g[1])))).toArray(Inst[]::new);
    runInstructions(code, -1, (hlt, acc) -> System.out.format("Acc = %d\n", acc));
    for (int i = 0; i < code.length; i++) {
      if (runInstructions(code, i, (hlt, acc) -> System.out.print(hlt ? String.format("\nAcc = %d\n", acc): "."))) {
        break;
      };
    }
  }

  private static boolean runInstructions(Inst[] instructions, int flipCounter, BiConsumer<Boolean, Long> onHalt) {
    long accumulator = 0;
    int pos = 0;
    BitSet visited = new BitSet();
    while (!visited.get(pos) && pos < instructions.length) {
      Inst inst = instructions[pos];
      visited.set(pos);
      if (inst.type == IType.acc) {
        accumulator += inst.value;
        pos++;
      } else {
        pos += (inst.type == IType.jmp) ^ (flipCounter-- == 0) ? inst.value : 1;
      }
    }
    checkState(pos <= instructions.length);
    boolean halted = pos == instructions.length;
    onHalt.accept(halted, accumulator);
    return halted;
  }
}
