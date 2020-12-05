package com.example;

public class Checks {
  public static void checkState(boolean condition) {
    if (!condition) {
      throw new IllegalStateException();
    }
  }
}
