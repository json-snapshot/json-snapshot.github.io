package io.github.jsonSnapshot;

public interface SnapshotConfig {
  String getFilePath();

  StackTraceElement findStacktraceElement();
}
