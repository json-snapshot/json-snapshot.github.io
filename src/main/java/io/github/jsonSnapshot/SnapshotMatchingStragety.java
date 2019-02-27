package io.github.jsonSnapshot;

public interface SnapshotMatchingStragety {

  void match(SnapshotDataItem expectedSnapshotItem, String currentObject)
      throws SnapshotMatchException;
}
