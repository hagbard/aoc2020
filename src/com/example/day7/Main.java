package com.example.day7;

import static com.example.Regex.acceptMatch;
import static java.lang.Integer.parseUnsignedInt;

import com.example.Input;
import com.google.common.base.Splitter;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.graph.MutableValueGraph;
import com.google.common.graph.ValueGraph;
import com.google.common.graph.ValueGraphBuilder;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

// Outer count = 151
// Inner count = 41559
public class Main {
  private static final String MY_BAG = "shiny gold";

  public static void main(String[] args) {
    MutableValueGraph<String, Integer> graph = ValueGraphBuilder.directed().allowsSelfLoops(false).build();
    Input.getLines(Main.class).filter(s -> !s.endsWith("contain no other bags.")).forEach(r -> addRuleTo(graph, r));
    System.out.format("Outer count = %d\n", collectOuter(graph, MY_BAG, new HashSet<>()).size());
    System.out.format("Inner count = %d\n", countInner(graph, MY_BAG, HashMultiset.create()));
  }

  private static Set<String> collectOuter(ValueGraph<String, Integer> graph, String label, Set<String> visited) {
    graph.predecessors(label).forEach(p -> {
      if (visited.add(p)) {
        collectOuter(graph, p, visited);
      }
    });
    return visited;
  }

  private static int countInner(ValueGraph<String, Integer> graph, String label, Multiset<String> counts) {
    int n = graph.successors(label).stream().mapToInt(
        s -> graph.edgeValue(label, s).get()
            * (1 + (counts.elementSet().contains(s) ? counts.count(s) : countInner(graph, s, counts)))).sum();
    counts.setCount(label, n);
    return n;
  }

  private static final Pattern RULE = Pattern.compile("(\\w.*) bags contain (.*)\\.");
  private static final Pattern CONTAINS = Pattern.compile("(\\d+) (.*) bags?");
  private static final Splitter SPLITTER = Splitter.on(',').trimResults();

  private static void addRuleTo(MutableValueGraph<String, Integer> graph, String rule) {
    acceptMatch(RULE, rule, g -> SPLITTER.splitToList(g[1]).forEach(s -> addEdgeTo(graph, g[0], s)));
  }

  private static void addEdgeTo(MutableValueGraph<String, Integer> graph, String label, String spec) {
    acceptMatch(CONTAINS, spec, g -> graph.putEdgeValue(label, g[1], parseUnsignedInt(g[0])));
  }
}
