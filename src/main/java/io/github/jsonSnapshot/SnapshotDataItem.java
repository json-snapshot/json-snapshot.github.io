package io.github.jsonSnapshot;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.NonNull;
import lombok.Value;

/*
 * #%L
 * json-snapshot.github.io
 * %%
 * Copyright (C) 2019 metas GmbH
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program. If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

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
