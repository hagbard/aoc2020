package com.example;

import java.util.Objects;

public final class Vec2 {
  public static Vec2 vec(int x, int y) {
    return new Vec2(x, y);
  }

  public final int x;
  public final int y;

  private Vec2(int x, int y) {
    this.x = x;
    this.y = y;
  }

  public Vec2 add(Vec2 v) {
    return new Vec2(x + v.x, y + v.y);
  }

  public Vec2 mul(int scale) {
    return new Vec2(x * scale, y * scale);
  }

  @Override
  public boolean equals(Object o) {
    Vec2 v;
    return (o instanceof Vec2) && x == (v = (Vec2) o).x && y == v.y;
  }

  @Override
  public int hashCode() {
    return Objects.hash(x, y);
  }

  @Override
  public String toString() {
    return String.format("(%d, %d)", x, y);
  }
}
