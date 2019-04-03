package io.github.jsonSnapshot.junit5;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import io.github.jsonSnapshot.SnapshotConfig;

public class SnapshotMatcherExtension implements BeforeEachCallback, AfterEachCallback {

  @Override
  public void beforeEach(ExtensionContext extensionContext) throws Exception {
    ExtensionContextRetriever.getInstance().set(extensionContext);
    SnapshotConfig.SnapshotConfigBuilder config = SnapshotConfig.builder();

    extensionContext
        .getConfigurationParameter(Constants.SNAPSHOT_ROOT_DIR)
        .ifPresent((path) -> config.filePath(path));
    SnapshotMatcher.start(config.build());
  }

  @Override
  public void afterEach(ExtensionContext extensionContext) throws Exception {
    SnapshotMatcher.validateSnapshots();
    ExtensionContextRetriever.getInstance().remove();
  }
}
