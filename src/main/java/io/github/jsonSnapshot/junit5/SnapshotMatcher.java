package io.github.jsonSnapshot.junit5;

import static io.github.jsonSnapshot.SnapshotUtils.defaultJsonFunction;
import static io.github.jsonSnapshot.junit5.Constants.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.jsonSnapshot.Snapshot;
import io.github.jsonSnapshot.SnapshotConfig;
import io.github.jsonSnapshot.SnapshotFile;
import io.github.jsonSnapshot.SnapshotMatchException;
import io.github.jsonSnapshot.SnapshotUtils;

public class SnapshotMatcher {

  private static Logger log = LoggerFactory.getLogger(SnapshotMatcher.class);
  private static final SnapshotConfig DEFAULT_CONFIG = SnapshotConfig.builder().build();
  private static final String MISSING_CONTEXT_ERR =
      "SnapshotTester not yet started! Ensure that you have placed @ExtendWith(SnapshotMatcherExtension.class) on your test class";
  private static Function<Object, String> jsonFunction;

  public static void start() {
    start(DEFAULT_CONFIG, defaultJsonFunction());
  }

  public static void start(SnapshotConfig config) {
    start(config, defaultJsonFunction());
  }

  public static void start(SnapshotConfig config, Function<Object, String> jsonFunction) {
    SnapshotMatcher.jsonFunction = jsonFunction;

    try {
      ExtensionContext context =
          ExtensionContextRetriever.getInstance()
              .get()
              .orElseThrow(() -> new SnapshotMatchException(MISSING_CONTEXT_ERR));
      Class clazz =
          context
              .getTestClass()
              .orElseThrow(
                  () -> new SnapshotMatchException("JUnit is unable to locate a class to test"));
      context
          .getStore(NAMESPACE)
          .put(
              SNAPSHOT_FILE,
              new SnapshotFile(
                  config.getFilePath(), clazz.getName().replaceAll("\\.", "/") + ".snap"));
      context.getStore(NAMESPACE).put(CALLED_SNAPSHOTS, new ArrayList<Snapshot>());
    } catch (IOException e) {
      throw new SnapshotMatchException(e.getMessage());
    }
  }

  public static void validateSnapshots() {
    ExtensionContext context =
        ExtensionContextRetriever.getInstance()
            .get()
            .orElseThrow(() -> new SnapshotMatchException(MISSING_CONTEXT_ERR));
    List<Snapshot> calledSnapshots = context.getStore(NAMESPACE).get(CALLED_SNAPSHOTS, List.class);
    SnapshotFile snapshotFile = context.getStore(NAMESPACE).get(SNAPSHOT_FILE, SnapshotFile.class);

    Set<String> rawSnapshots = snapshotFile.getRawSnapshots();
    List<String> snapshotNames =
        calledSnapshots.stream().map(Snapshot::getSnapshotName).collect(Collectors.toList());
    List<String> unusedRawSnapshots = new ArrayList<>();

    for (String rawSnapshot : rawSnapshots) {
      boolean foundSnapshot = false;
      for (String snapshotName : snapshotNames) {
        if (rawSnapshot.contains(snapshotName)) {
          foundSnapshot = true;
        }
      }
      if (!foundSnapshot) {
        unusedRawSnapshots.add(rawSnapshot);
      }
    }
    if (unusedRawSnapshots.size() > 0) {
      log.warn(
          "All unused Snapshots: "
              + StringUtils.join(unusedRawSnapshots, "\n")
              + ". Consider deleting the snapshot file to recreate it!");
    }
  }

  public static Snapshot expect(Object firstObject, Object... others) {
    ExtensionContext context =
        ExtensionContextRetriever.getInstance()
            .get()
            .orElseThrow(() -> new SnapshotMatchException(MISSING_CONTEXT_ERR));

    Object[] objects = SnapshotUtils.mergeObjects(firstObject, others);
    List<Snapshot> calledSnapshots = context.getStore(NAMESPACE).get(CALLED_SNAPSHOTS, List.class);
    SnapshotFile snapshotFile = context.getStore(NAMESPACE).get(SNAPSHOT_FILE, SnapshotFile.class);
    Class clazz =
        context
            .getTestClass()
            .orElseThrow(
                () ->
                    new SnapshotMatchException("Junit is unable to locate a class/method to test"));

    /**
     * The DISPLAY NAME for a parameterized test is built dynamically by Junit. We further namespace
     * these dynamic names with the name of the method being tested to prevent the small chance of
     * naming collisions with other parameterized tests.
     */
    String testName =
        context
            .getParent()
            .map(
                (parentContext) -> {
                  if (parentContext.getTestMethod().isPresent()
                      && context.getTestMethod().isPresent()) {
                    if (parentContext.getTestMethod().get().equals(context.getTestMethod().get())) {
                      return String.format(
                          "%s.%s",
                          context.getTestMethod().get().getName(), context.getDisplayName());
                    }
                  }
                  return context.getDisplayName();
                })
            .orElseGet(() -> context.getDisplayName());

    // TODO kebab case the testName

    Snapshot snapshot = new Snapshot(snapshotFile, clazz, testName, jsonFunction, objects);
    SnapshotUtils.validateExpectCall(snapshot, calledSnapshots);
    calledSnapshots.add(snapshot);
    return snapshot;
  }
}
