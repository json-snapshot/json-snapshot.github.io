package io.github.jsonSnapshot;

import io.github.jsonSnapshot.matchingstrategy.StringEqualsMatchingStrategy;

public interface SnapshotConfig {

  default String getFilePath() {
    return "src/test/java/";
  }

  default SnapshotMatchingStrategy getSnapshotMatchingStrategy() {
    return StringEqualsMatchingStrategy.INSTANCE;
  }
}
