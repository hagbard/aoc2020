package com.example.day12;

import static com.example.Vec2.vec;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static java.lang.Integer.parseUnsignedInt;

import com.example.Input;
import com.example.Vec2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

// Dist = 2280
// Dist = 38693
public class Main {
  private static final ImmutableList<Vec2> DIRS =
      ImmutableList.of(vec(1, 0), vec(0, -1), vec(-1, 0), vec(0, 1));

  private interface ShipOp {
    void apply(Ship s, int n);
  }

  private static int angleToDir(int angle) {
    return (angle / 90) & 0x3;
  }

  private static final ImmutableMap<Character, ShipOp> FOO_OPS =
      ImmutableMap.<Character, ShipOp>builder()
          .put('E', (s, n) -> s.moveBy(DIRS.get(0).mul(n)))
          .put('S', (s, n) -> s.moveBy(DIRS.get(1).mul(n)))
          .put('W', (s, n) -> s.moveBy(DIRS.get(2).mul(n)))
          .put('N', (s, n) -> s.moveBy(DIRS.get(3).mul(n)))
          .put('L', (s, n) -> s.rotateShip(angleToDir(-n)))
          .put('R', (s, n) -> s.rotateShip(angleToDir(n)))
          .put('F', Ship::moveForward)
          .build();

  private static final ImmutableMap<Character, ShipOp> BAR_OPS =
      ImmutableMap.<Character, ShipOp>builder()
          .put('E', (s, n) -> s.moveWaypoint(DIRS.get(0).mul(n)))
          .put('S', (s, n) -> s.moveWaypoint(DIRS.get(1).mul(n)))
          .put('W', (s, n) -> s.moveWaypoint(DIRS.get(2).mul(n)))
          .put('N', (s, n) -> s.moveWaypoint(DIRS.get(3).mul(n)))
          .put('L', (s, n) -> s.rotateWaypoint(angleToDir(-n)))
          .put('R', (s, n) -> s.rotateWaypoint(angleToDir(n)))
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
    private int headingDir = 0;  // [0..3]
    private Vec2 waypoint = vec(10, 1);

    Ship(ImmutableMap<Character, ShipOp> ops) {
      this.ops = ops;
    }

    void moveBy(Vec2 adj) {
      pos = pos.add(adj);
    }

    void moveForward(int dist) {
      moveBy(DIRS.get(headingDir).mul(dist));
    }

    void moveToWaypoint(int times) {
      moveBy(waypoint.mul(times));
    }

    void rotateShip(int dir) {
      this.headingDir = (headingDir + dir) & 0x3;
    }

    void moveWaypoint(Vec2 adj) {
      waypoint = waypoint.add(adj);
    }

    void rotateWaypoint(int dir) {
      switch (dir) {
        case 1: waypoint = vec(waypoint.y, -waypoint.x); break;
        case 2: waypoint = vec(-waypoint.x, -waypoint.y); break;
        case 3: waypoint = vec(-waypoint.y, waypoint.x); break;
      }
    }

    int execute(ImmutableList<String> inst) {
      inst.forEach(s -> ops.get(s.charAt(0)).apply(this, parseUnsignedInt(s.substring(1))));
      return Math.abs(pos.x) + Math.abs(pos.y);
    }
  }
}
