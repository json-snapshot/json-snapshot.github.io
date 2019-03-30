package io.github.jsonSnapshot.junit5;

import static io.github.jsonSnapshot.junit5.SnapshotMatcher.expect;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;

public class SnapshotMatcherExtensionConfigurationTest {
  @TempDir public static Path tempDir;

  @BeforeAll
  public static void beforeAll() {
    System.setProperty(Constants.SNAPSHOT_ROOT_DIR, tempDir.toString());
  }

  @Test
  @ExtendWith(SnapshotMatcherExtension.class)
  public void testSnapshotRootDirectoryIsUpdatedByJvmConfig() {
    expect("hello world").toMatchSnapshot();

    String[] files =
        Paths.get(tempDir.toString(), "io", "github", "jsonSnapshot", "junit5").toFile().list();

    assertEquals(1, files.length);
    assertEquals("SnapshotMatcherExtensionConfigurationTest.snap", files[0]);
  }
}
