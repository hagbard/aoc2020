package com.example;

import java.util.Objects;

public final class Vec4 {
  public static Vec4 vec(int x, int y, int z, int w) {
    return new Vec4(x, y, z, w);
  }

  public final int x;
  public final int y;
  public final int z;
  public final int w;
  public final int hash;

  private Vec4(int x, int y, int z, int w) {
    this.x = x;
    this.y = y;
    this.z = z;
    this.w = w;
    this.hash = Objects.hash(x, y, z, w);
  }

  public Vec4 add(Vec4 v) {
    return new Vec4(x + v.x, y + v.y, z + v.z, w + v.w);
  }

  @Override
  public boolean equals(Object o) {
    Vec4 v;
    return (o instanceof Vec4) && x == (v = (Vec4) o).x && y == v.y && z == v.z && w == v.w;
  }

  @Override
  public int hashCode() {
    return hash;
  }

  @Override
  public String toString() {
    return String.format("(%d, %d, %d, %d)", x, y, z, w);
  }
}
