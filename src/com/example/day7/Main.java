package com.example.day7;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.ImmutableMultiset.toImmutableMultiset;

import com.example.Input;
import com.google.common.base.Splitter;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import com.google.common.graph.MutableValueGraph;
import com.google.common.graph.ValueGraph;
import com.google.common.graph.ValueGraphBuilder;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
  private static final String MY_BAG = "shiny gold";

  public static void main(String[] args) {
    MutableValueGraph<String, Integer> g = ValueGraphBuilder.directed().allowsSelfLoops(false).build();
    Input.getLines(Main.class).map(Rule::parse).forEach(r -> r.addTo(g));
    System.out.format("count = %d\n", collectOuter(g, MY_BAG, new HashSet<>()).size());
    System.out.format("count = %d\n", countInner(g, MY_BAG, HashMultiset.create()));
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
    int c = graph.successors(label).stream().mapToInt(
        t -> graph.edgeValue(label, t).get()
            * (1 + (counts.elementSet().contains(t) ? counts.count(t) : countInner(graph, t, counts)))).sum();
    counts.setCount(label, c);
    return c;
  }

  private static final class Rule {
    // muted magenta bags contain 4 muted teal bags, 5 dotted turquoise bags, 3 pale plum bags, 4 faded bronze bags.
    // dim coral bags contain no other bags.
    private static final Pattern RULE = Pattern.compile("(\\w.*) bags contain (.*)\\.");
    private static final Pattern CONTAINS = Pattern.compile("(\\d+) (.*) bags?");
    private static final Splitter SPLITTER = Splitter.on(',').trimResults();

    private final String label;
    private final ImmutableMultiset<String> inner;

    public Rule(String label, ImmutableMultiset<String> inner) {
      this.label = label;
      this.inner = inner;
    }

    static Rule parse(String s) {
      Matcher m = RULE.matcher(s);
      checkArgument(m.matches());
      ImmutableMultiset<String> inner = m.group(2).equals("no other bags")
          ? ImmutableMultiset.of()
          : SPLITTER.splitToList(m.group(2)).stream()
              .map(Rule::parseContains)
              .collect(toImmutableMultiset(Multiset.Entry::getElement, Multiset.Entry::getCount));
      return new Rule(m.group(1), inner);
    }

    static Multiset.Entry<String> parseContains(String s) {
      Matcher m = CONTAINS.matcher(s);
      checkArgument(m.matches(), s);
      return Multisets.immutableEntry(m.group(2), Integer.parseUnsignedInt(m.group(1)));
    }

    public void addTo(MutableValueGraph<String, Integer> g) {
      inner.forEachEntry((t, n) -> g.putEdgeValue(label, t, n));
    }
  }
}
