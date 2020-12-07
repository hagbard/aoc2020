package com.example;

import com.google.common.base.Splitter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

public class Input {
  private static final Splitter GROUP_SPLITTER = Splitter.on("\n\n");
  private static final Splitter LINE_SPLITTER = Splitter.on('\n').omitEmptyStrings();

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

  public static Stream<String> getLines(Class<?> cls) {
    return get(cls).stream();
  }

  public static Stream<List<String>> getGroups(Class<?> cls) {
    return GROUP_SPLITTER.splitToList(getAsString(cls)).stream().map(LINE_SPLITTER::splitToList);
  }

  public static String getAsString(Class<?> cls) {
    try {
      return Files.readString(getInputForClass(cls));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
