package io.github.jsonSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.TreeMap;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Value;

@Value
public class SnapshotData {

  @Getter(AccessLevel.NONE)
  private final TreeMap<String, SnapshotDataItem> snapshotDataItems = new TreeMap<>();

  public void add(@NonNull final SnapshotDataItem snapshotDataItem) {
    snapshotDataItems.put(snapshotDataItem.getName(), snapshotDataItem);
  }

  public Optional<SnapshotDataItem> getItemByName(@NonNull final String snapshotName) {
    return Optional.ofNullable(snapshotDataItems.get(snapshotName));
  }

  public List<SnapshotDataItem> getItems() {
    return Collections.unmodifiableList(new ArrayList<>(snapshotDataItems.values()));
  }
}
