package io.github.jsonSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Value;

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
