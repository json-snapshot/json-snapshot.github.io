package io.github.jsonSnapshot;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

public class JunitConfigTest {

  @AfterEach
  public void beforeEach() {
    System.setProperty(SnapshotConfig.UPDATE_SNAPSHOTS_PARAMETER, "");
  }

  @Test
  public void shouldNotUpdateSnapshotNotPassed() {
    SnapshotConfig snapshotConfig = new JUnitConfig();
    assertThat(snapshotConfig.shouldUpdateSnapshot()).isFalse();
  }

  @Test
  public void shouldUpdateSnapshotTrue() {
    System.setProperty(SnapshotConfig.UPDATE_SNAPSHOTS_PARAMETER, "true");
    SnapshotConfig snapshotConfig = new JUnitConfig();
    assertThat(snapshotConfig.shouldUpdateSnapshot()).isTrue();
  }

  @Test
  public void shouldUpdateSnapshotFalse() {
    System.setProperty(SnapshotConfig.UPDATE_SNAPSHOTS_PARAMETER, "false");
    SnapshotConfig snapshotConfig = new JUnitConfig();
    assertThat(snapshotConfig.shouldUpdateSnapshot()).isFalse();
  }
}
