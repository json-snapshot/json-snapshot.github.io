package io.github.jsonSnapshot.junit5;

import org.junit.jupiter.api.extension.ExtensionContext;

public class Constants {

  public static final ExtensionContext.Namespace NAMESPACE =
      ExtensionContext.Namespace.create(SnapshotMatcher.class);
  public static final String SNAPSHOT_FILE = "snapshotFile";
  public static final String CALLED_SNAPSHOTS = "calledSnapshots";

  // ***************** JVM Config Values *************************
  public static final String SNAPSHOT_ROOT_DIR =
      "junit.jupiter.extensions.snapshotmatcher.snaps.rootDir";

  private Constants() {}
}
