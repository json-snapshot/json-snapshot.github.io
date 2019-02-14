package io.github.jsonSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import lombok.AccessLevel;
import lombok.Getter;
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
public class SnapshotData {
  @Getter(AccessLevel.NONE)
  TreeMap<String, SnapshotDataItem> snapshotDataItems;

  public SnapshotData() {
    this.snapshotDataItems = new TreeMap<>();
  }

  public void add(@NonNull final SnapshotDataItem snapshotDataItem) {
    snapshotDataItems.put(snapshotDataItem.getName(), snapshotDataItem);
  }

  public SnapshotDataItem getItemByNameOrNull(@NonNull final String snapshotName) {
    return snapshotDataItems.get(snapshotName);
  }

  public List<SnapshotDataItem> getItems() {
    return new ArrayList<>(snapshotDataItems.values());
  }
}
