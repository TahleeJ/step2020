package com.google.sps.data;

public final class Comment {

  private final long id;
  private final String text;
  private final long time;
  private static int numComments = 0;

  public Comment(long id, String text, long time) {
    this.id = id;
    this.text = text;
    this.time = time;
    numComments++;
  }

  public static int getNumComments() {
      return numComments;
  }
}