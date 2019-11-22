package io.github.jsonSnapshot;

public interface SnapshotMatchingStrategy {

  void match(SnapshotDataItem expectedSnapshotItem, String currentObject)
      throws SnapshotMatchException;
}
