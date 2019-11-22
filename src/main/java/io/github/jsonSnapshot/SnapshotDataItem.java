package io.github.jsonSnapshot;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.NonNull;
import lombok.Value;

import org.apache.commons.lang3.StringUtils;

@Value
public class SnapshotDataItem implements Comparable<SnapshotDataItem> {

  private static final int REGEX_FLAGS = Pattern.MULTILINE + Pattern.DOTALL;
  private static final Pattern REGEX =
      Pattern.compile("(?<name>[^ =]*) *=+ *(?<data>\\[.*\\])[^\\]]*", REGEX_FLAGS);

  private final String name;
  private final String data;

  public SnapshotDataItem(@NonNull final String rawDataString) {
    final Matcher matcher = REGEX.matcher(rawDataString);
    if (!matcher.matches()) {
      throw new IllegalArgumentException(
          "Raw data string does not match expected pattern. String: " + rawDataString);
    }

    final String name = matcher.group("name");
    final String data = matcher.group("data");

    this.name = StringUtils.trim(name);
    this.data = StringUtils.trim(data);
  }

  public SnapshotDataItem(String name, String data) {
    this.name = StringUtils.trim(name);
    this.data = StringUtils.trim(data);
  }

  public String asRawData() {
    return name + "=" + data;
  }

  @Override
  public int compareTo(final SnapshotDataItem o) {
    if (o == null) {
      return 1;
    }
    return this.name.compareTo(o.name);
  }
}
