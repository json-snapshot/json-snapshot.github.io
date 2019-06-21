package io.github.jsonSnapshot;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import io.github.jsonSnapshot.matchingstrategy.StringEqualsMatchingStrategy;

@ExtendWith(MockitoExtension.class)
class SnapshotTest {

  private static final SnapshotConfig DEFAULT_CONFIG = new DefaultConfig();
  private static final String FILE_PATH = "src/test/java/anyFilePath";
  private static final String SNAPSHOT_NAME = "java.lang.String.toString";
  private static final String SNAPSHOT = "java.lang.String.toString=[\n  \"anyObject\"\n]";

  private SnapshotFile snapshotFile;

  private Snapshot snapshot;

  @BeforeEach
  void setUp() throws NoSuchMethodException, IOException {
    snapshotFile = new SnapshotFile(DEFAULT_CONFIG.getFilePath(), "anyFilePath");
    snapshot =
        new Snapshot(
            snapshotFile,
            String.class,
            String.class.getDeclaredMethod("toString"),
            SnapshotMatcher.defaultJsonFunction(),
            StringEqualsMatchingStrategy.INSTANCE,
            "anyObject");
  }

  @AfterEach
  void tearDown() throws IOException {
    Files.delete(Paths.get(FILE_PATH));
  }

  @Test
  void shouldGetSnapshotNameSuccessfully() {
    String snapshotName = snapshot.getSnapshotName();
    assertThat(snapshotName).isEqualTo(SNAPSHOT_NAME);
  }

  @Test
  void shouldMatchSnapshotSuccessfully() {
    snapshot.toMatchSnapshot();

    final SnapshotData storedSnapshots = snapshotFile.getStoredSnapshots();
    assertThat(storedSnapshots.getItems()).hasSize(1);

    final SnapshotDataItem singleItem = storedSnapshots.getItems().get(0);
    assertThat(singleItem)
        .extracting("name", "data")
        .containsExactly(SNAPSHOT_NAME, "[\n  \"anyObject\"\n]");
  }

  @Test
  void shouldMatchSnapshotWithException() {

    snapshotFile.push(new SnapshotDataItem(SNAPSHOT_NAME, "anyWrongSnapshot"));

    assertThrows(SnapshotMatchException.class, snapshot::toMatchSnapshot);
  }
}
