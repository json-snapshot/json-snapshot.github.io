package io.github.jsonSnapshot;

public interface SnapshotConfig {
  String UPDATE_SNAPSHOTS_PARAMETER = "update-snapshots";

  String getFilePath();

  StackTraceElement findStacktraceElement();

  boolean shouldUpdateSnapshot();
}
