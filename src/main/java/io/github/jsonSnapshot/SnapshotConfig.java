package io.github.jsonSnapshot;

import io.github.jsonSnapshot.matchrule.SnapshotMatchRule;
import io.github.jsonSnapshot.matchrule.StringEqualsMatchRule;

public interface SnapshotConfig {

  default String getFilePath() {
    return "src/test/java/";
  }

  default SnapshotMatchRule getSnapshotMatchRule() {
    return StringEqualsMatchRule.INSTANCE;
  }
}
