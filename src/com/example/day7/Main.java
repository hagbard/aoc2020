package com.example.day7;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.ImmutableMultiset.toImmutableMultiset;

import com.example.Input;
import com.google.common.base.Splitter;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.graph.MutableValueGraph;
import com.google.common.graph.ValueGraph;
import com.google.common.graph.ValueGraphBuilder;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// count = 151
// count = 41559
public class Main {
  private static final String MY_BAG = "shiny gold";

  public static void main(String[] args) {
    MutableValueGraph<String, Integer> graph = ValueGraphBuilder.directed().allowsSelfLoops(false).build();
    Input.getLines(Main.class).filter(s -> !s.endsWith("contain no other bags.")).forEach(r -> addRuleTo(graph, r));
    System.out.format("count = %d\n", collectOuter(graph, MY_BAG, new HashSet<>()).size());
    System.out.format("count = %d\n", countInner(graph, MY_BAG, HashMultiset.create()));
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
    MatchResult m = match(RULE, rule);
    String label = m.group(1);
    SPLITTER.splitToList(m.group(2)).forEach(s -> addEdgeToGraph(graph, label, s));
  }

  private static void addEdgeToGraph(MutableValueGraph<String, Integer> graph, String label, String spec) {
    MatchResult m = match(CONTAINS, spec);
    graph.putEdgeValue(label, m.group(2), Integer.parseUnsignedInt(m.group(1)));
  }

  private static MatchResult match(Pattern pattern, String s) {
    Matcher m = pattern.matcher(s);
    checkArgument(m.matches(), s);
    return m.toMatchResult();
  }
}
