package io.github.jsonSnapshot;

import io.github.jsonSnapshot.matchingstrategy.StringEqualsMatchingStrategy;

public interface SnapshotConfig {

  default String getFilePath() {
    return "src/test/java/";
  }

  default SnapshotMatchingStragety getSnapshotMatchRule() {
    return StringEqualsMatchingStrategy.INSTANCE;
  }
}
