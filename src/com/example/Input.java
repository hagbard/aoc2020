package com.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Input {

  private static final Path ROOT = Paths.get(System.getenv("HOME"))
      .resolve(Paths.get("advent-of-code-2020/src"));

  private static Path getInputForClass(Class<?> cls) {
    Path localDir = Paths.get(cls.getPackage().getName().replace('.', '/'));
    return ROOT.resolve(localDir).resolve("input.txt");
  }

  public static List<String> getInput(Class<?> cls) {
    try {
      return Files.readAllLines(getInputForClass(cls));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static String getInputAsString(Class<?> cls) {
    try {
      return Files.readString(getInputForClass(cls));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
