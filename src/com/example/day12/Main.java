package com.example.day12;

import static com.example.Vec2.vec;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static java.lang.Integer.parseUnsignedInt;
import static java.lang.Math.floorMod;

import com.example.Input;
import com.example.Vec2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

// Dist = 2280
// Dist = 38693
public class Main {
  private static final ImmutableMap<Integer, Vec2> DIRS =
      ImmutableMap.of(0, vec(1, 0), 90, vec(0, -1), 180, vec(-1, 0), 270, vec(0, 1));

  private interface ShipOp {
    void apply(Ship s, int n);
  }

  private static final ImmutableMap<Character, ShipOp> FOO_OPS =
      ImmutableMap.<Character, ShipOp>builder()
          .put('E', (s, n) -> s.moveBy(DIRS.get(0).mul(n)))
          .put('S', (s, n) -> s.moveBy(DIRS.get(90).mul(n)))
          .put('W', (s, n) -> s.moveBy(DIRS.get(180).mul(n)))
          .put('N', (s, n) -> s.moveBy(DIRS.get(270).mul(n)))
          .put('L', (s, n) -> s.rotateShip(-n))
          .put('R', (s, n) -> s.rotateShip(n))
          .put('F', Ship::moveForward)
          .build();

  private static final ImmutableMap<Character, ShipOp> BAR_OPS =
      ImmutableMap.<Character, ShipOp>builder()
          .put('E', (s, n) -> s.moveWaypoint(DIRS.get(0).mul(n)))
          .put('S', (s, n) -> s.moveWaypoint(DIRS.get(90).mul(n)))
          .put('W', (s, n) -> s.moveWaypoint(DIRS.get(180).mul(n)))
          .put('N', (s, n) -> s.moveWaypoint(DIRS.get(270).mul(n)))
          .put('L', (s, n) -> s.rotateWaypoint(-n))
          .put('R', (s, n) -> s.rotateWaypoint(n))
          .put('F', Ship::moveToWaypoint)
          .build();

  public static void main(String[] args) {
    ImmutableList<String> inst = Input.getLines(Main.class).collect(toImmutableList());
    System.out.format("Dist = %d\n", new Ship(FOO_OPS).execute(inst));
    System.out.format("Dist = %d\n", new Ship(BAR_OPS).execute(inst));
  }

  private static class Ship {
    private final ImmutableMap<Character, ShipOp> ops;
    private Vec2 pos = vec(0, 0);
    private int heading = 0;
    private Vec2 waypoint = vec(10, 1);

    Ship(ImmutableMap<Character, ShipOp> ops) {
      this.ops = ops;
    }

    void moveBy(Vec2 adj) {
      pos = pos.add(adj);
    }

    void moveForward(int dist) {
      moveBy(DIRS.get(heading).mul(dist));
    }

    void moveToWaypoint(int times) {
      moveBy(waypoint.mul(times));
    }

    void rotateShip(int turn) {
      this.heading = floorMod(heading + turn, 360);
    }

    void moveWaypoint(Vec2 adj) {
      waypoint = waypoint.add(adj);
    }

    void rotateWaypoint(int turn) {
      switch (floorMod(turn, 360)) {
        case 90: waypoint = vec(waypoint.y, -waypoint.x); break;
        case 180: waypoint = vec(-waypoint.x, -waypoint.y); break;
        case 270: waypoint = vec(-waypoint.y, waypoint.x); break;
      }
    }

    int execute(ImmutableList<String> inst) {
      inst.forEach(s -> ops.get(s.charAt(0)).apply(this, parseUnsignedInt(s.substring(1))));
      return Math.abs(pos.x) + Math.abs(pos.y);
    }
  }
}
