package io.github.jsonSnapshot.junit5;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.github.jsonSnapshot.Snapshot;
import io.github.jsonSnapshot.SnapshotConfig;
import io.github.jsonSnapshot.SnapshotFile;
import io.github.jsonSnapshot.SnapshotUtils;

public class SnapshotMatcherTest {

  @Mock private ExtensionContext extensionContext;
  @Mock private ExtensionContext.Store store;

  @BeforeEach
  public void beforeEach() {
    MockitoAnnotations.initMocks(this);
    ExtensionContextRetriever.getInstance().set(extensionContext);
  }

  @Test
  public void testSnapshotMatcherCanBeInitialized() throws Exception {
    when(extensionContext.getStore(any(ExtensionContext.Namespace.class))).thenReturn(store);
    when(extensionContext.getTestClass()).thenReturn(Optional.of(SnapshotMatcherTest.class));

    verifyInitialization(
        () -> {
          SnapshotMatcher.start();
          return null;
        });
  }

  @Test
  public void testSnapshotMatcherCanBeInitializedWithSnapshotConfig() throws Exception {
    when(extensionContext.getStore(any(ExtensionContext.Namespace.class))).thenReturn(store);
    when(extensionContext.getTestClass()).thenReturn(Optional.of(SnapshotMatcherTest.class));

    verifyInitialization(
        () -> {
          SnapshotMatcher.start(SnapshotConfig.builder().build());
          return null;
        });
  }

  @Test
  public void testSnapshotMatcherCanBeInitializedWithSnapshotConfigAndJsonBuilder()
      throws Exception {
    when(extensionContext.getStore(any(ExtensionContext.Namespace.class))).thenReturn(store);
    when(extensionContext.getTestClass()).thenReturn(Optional.of(SnapshotMatcherTest.class));

    verifyInitialization(
        () -> {
          SnapshotMatcher.start(
              SnapshotConfig.builder().build(), SnapshotUtils.defaultJsonFunction());
          return null;
        });
  }

  @Test
  public void testSnapshotIsCreated() {
    List<Snapshot> snapshots = new ArrayList<>();
    when(extensionContext.getStore(any(ExtensionContext.Namespace.class))).thenReturn(store);
    when(extensionContext.getDisplayName()).thenReturn("testSnapshotIsCreated");
    when(extensionContext.getTestClass()).thenReturn(Optional.of(SnapshotMatcherTest.class));
    when(store.get(Constants.CALLED_SNAPSHOTS, List.class)).thenReturn(snapshots);

    Snapshot snapshot = SnapshotMatcher.expect("hello world");
    assertEquals(1, snapshots.size());
    assertTrue(snapshot.getSnapshotName().endsWith("testSnapshotIsCreated="));
  }

  private void verifyInitialization(Callable<Void> initializer) throws Exception {

    initializer.call();
    verify(extensionContext).getTestClass();
    verify(extensionContext, times(2)).getStore(Constants.NAMESPACE);
    verify(store).put(eq(Constants.SNAPSHOT_FILE), any(SnapshotFile.class));
    verify(store).put(eq(Constants.CALLED_SNAPSHOTS), any(List.class));
  }
}
