package com.example;

import com.google.common.base.Splitter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

public class Input {

  private static final Splitter NUL_SPLITTER = Splitter.on('\0').omitEmptyStrings();

  private static final Path ROOT = Paths.get(System.getenv("HOME"))
      .resolve(Paths.get("advent-of-code-2020/src"));

  private static Path getInputForClass(Class<?> cls) {
    Path localDir = Paths.get(cls.getPackage().getName().replace('.', '/'));
    return ROOT.resolve(localDir).resolve("input.txt");
  }

  public static List<String> get(Class<?> cls) {
    try {
      return Files.readAllLines(getInputForClass(cls));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static Stream<List<String>> getGrouped(Class<?> cls) {
    return Splitter.on('\n').splitToList(getAsString(cls).replaceAll("(\\S)\n", "$1\0")).stream()
        .map(NUL_SPLITTER::splitToList);
  }

  public static String getAsString(Class<?> cls) {
    try {
      return Files.readString(getInputForClass(cls));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
