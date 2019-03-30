package io.github.jsonSnapshot;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SnapshotConfig {
  @Builder.Default private final String filePath = "src/test/java/";
}
