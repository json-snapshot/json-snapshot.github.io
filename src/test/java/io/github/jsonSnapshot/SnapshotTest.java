package io.github.jsonSnapshot;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.PathNotFoundException;
import org.junit.Before;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SnapshotTest {

  private static final SnapshotConfig DEFAULT_CONFIG = new DefaultConfig();
  private static final String FILE_PATH = "src/test/java/anyFilePath";
  private static final String SNAPSHOT_NAME = "java.lang.String.toString=";
  private static final String SNAPSHOT = "java.lang.String.toString=[\n  \"anyObject\"\n]";

  private SnapshotFile snapshotFile;

  private Snapshot snapshot;

  @BeforeEach
  void setUp() throws NoSuchMethodException, IOException {
    snapshotFile = new SnapshotFile(DEFAULT_CONFIG.getFilePath(), "anyFilePath");
    snapshot = createSnapshot("anyObject");
  }

  @AfterEach
  void tearDown() throws IOException {
    Files.delete(Paths.get(FILE_PATH));
  }

  private Snapshot createSnapshot(Object... current) throws NoSuchMethodException {
    return new Snapshot(
        snapshotFile,
        String.class,
        String.class.getDeclaredMethod("toString"),
        SnapshotMatcher.defaultJsonFunction(),
        current);
  }

  @Test
  void shouldGetSnapshotNameSuccessfully() {
    String snapshotName = snapshot.getSnapshotName();
    assertThat(snapshotName).isEqualTo(SNAPSHOT_NAME);
  }

  @Test
  void shouldMatchSnapshotSuccessfully() {
    snapshot.toMatchSnapshot();
    assertThat(snapshotFile.getRawSnapshots())
        .isEqualTo(Stream.of(SNAPSHOT).collect(Collectors.toCollection(TreeSet::new)));
  }

  @Test
  void shouldMatchSnapshotWithException() {
    snapshotFile.push(SNAPSHOT_NAME + "anyWrongSnapshot");

    assertThrows(SnapshotMatchException.class, snapshot::toMatchSnapshot);
  }

  @Test
  public void shouldNotFailOnEmptyIgnoredPathList() {
    snapshot.ignoring().toMatchSnapshot();
    assertThat(snapshotFile.getRawSnapshots()).isEqualTo(Stream.of(SNAPSHOT).collect(Collectors.toCollection(TreeSet::new)));
  }

  @Test
  public void shouldFailWhenTryingToIgnoreChildOfPrimitiveJsonObject() {
    assertThrows(PathNotFoundException.class, () -> snapshot.ignoring("anyObject_is_a_json_string_and_can_not_have_children").toMatchSnapshot());
  }

  @Test
  public void shouldIgnoreTopLevelAndNestedFields() throws NoSuchMethodException, IOException {
    Object object = new ObjectMapper().readTree("{\n" +
        "  \"notIgnored\": \"notIgnored\",\n" +
        "  \"ignoredInParent\": \"ignored\",\n" +
        "  \"child\": {\n" +
        "    \"ignoredInParent\": \"notIgnored\",\n" +
        "    \"ignoredInChild\": \"ignored\"\n" +
        "  }\n" +
        "}");
    Snapshot snapshot = createSnapshot(object);

    snapshot.ignoring("ignoredInParent", "child.ignoredInChild").toMatchSnapshot();
    String expectedSnapshot = "java.lang.String.toString=[\n" +
        "  {\n" +
        "    \"child\": {\n" +
        "      \"ignoredInParent\": \"notIgnored\"\n" +
        "    },\n" +
        "    \"notIgnored\": \"notIgnored\"\n" +
        "  }\n" +
        "]";
    assertThat(snapshotFile.getRawSnapshots()).isEqualTo(Collections.singleton(expectedSnapshot));
  }
}
