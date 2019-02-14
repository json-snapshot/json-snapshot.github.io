package io.github.jsonSnapshot;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.NonNull;
import lombok.Value;

@Value
public class SnapshotDataItem implements Comparable<SnapshotDataItem> {
  private static final int REGEX_FLAGS = Pattern.MULTILINE + Pattern.DOTALL;
  private static final Pattern REGEX =
      Pattern.compile("(?<name>[^ =]*) *=+ *(?<data>\\[.*\\])[^\\]]*", REGEX_FLAGS);

  public static SnapshotDataItem ofRawData(@NonNull final String rawDataString)
      throws IllegalRawDataException {
    final Matcher matcher = REGEX.matcher(rawDataString);
    if (!matcher.matches()) {
      throw new IllegalRawDataException(rawDataString);
    }

    final String name = matcher.group("name");
    final String data = matcher.group("data");
    return SnapshotDataItem.ofNameAndData(trimOrNull(name), trimOrNull(data));
  }

  private static String trimOrNull(final String str) {
    if (str == null) {
      return null;
    }
    return str.trim();
  }

  public static SnapshotDataItem ofNameAndData(
      @NonNull final String name, @NonNull final String data) {
    return new SnapshotDataItem(name, data);
  }

  String name;

  String data;

  private SnapshotDataItem(@NonNull final String name, @NonNull final String data) {
    this.name = name;
    this.data = data;
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

  public static final class IllegalRawDataException extends IllegalArgumentException {
    private static final long serialVersionUID = -5698941959821538053L;

    private IllegalRawDataException(String invalidRawData) {
      super(invalidRawData);
    }
  }
}
