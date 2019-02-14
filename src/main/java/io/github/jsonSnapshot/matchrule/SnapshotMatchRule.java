package io.github.jsonSnapshot.matchrule;

import io.github.jsonSnapshot.SnapshotMatchException;

public interface SnapshotMatchRule {
  void match(String rawSnapshot, String currentObject) throws SnapshotMatchException;
}
