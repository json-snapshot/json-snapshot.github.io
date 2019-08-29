package io.github.jsonSnapshot;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.diff.DiffUtils;
import org.assertj.core.util.diff.Patch;

public class Snapshot {

  private SnapshotFile snapshotFile;

  private Class clazz;

  private Method method;

  private String scenario;

  private Function<Object, String> jsonFunction;

  private Object[] current;

  private final SnapshotConfig snapshotConfig;

  Snapshot(
      SnapshotConfig snapshotConfig,
      SnapshotFile snapshotFile,
      Class clazz,
      Method method,
      String scenario,
      Function<Object, String> jsonFunction,
      Object... current) {
    this.snapshotConfig = snapshotConfig;
    this.current = current;
    this.snapshotFile = snapshotFile;
    this.clazz = clazz;
    this.method = method;
    this.scenario = scenario;
    this.jsonFunction = jsonFunction;
  }

  public void toMatchSnapshot() {

    Set<String> rawSnapshots = snapshotFile.getRawSnapshots();

    String rawSnapshot = getRawSnapshot(rawSnapshots);

    String currentObject = takeSnapshot();

    // Match Snapshot
    if (rawSnapshot != null && !snapshotConfig.shouldUpdateSnapshot()) {
      if (!rawSnapshot.trim().equals(currentObject.trim())) {
        throw generateDiffError(rawSnapshot, currentObject);
      }
    }
    // Create New Snapshot
    else {
      snapshotFile.push(currentObject);
    }
  }

  private SnapshotMatchException generateDiffError(String rawSnapshot, String currentObject) {
    // compute the patch: this is the diffutils part
    Patch<String> patch =
        DiffUtils.diff(
            Arrays.asList(rawSnapshot.trim().split("\n")),
            Arrays.asList(currentObject.trim().split("\n")));
    String error =
        "Error on: \n"
            + currentObject.trim()
            + "\n\n"
            + patch
                .getDeltas()
                .stream()
                .map(delta -> delta.toString() + "\n")
                .reduce(String::concat)
                .get();
    return new SnapshotMatchException(error);
  }

  private String getRawSnapshot(Collection<String> rawSnapshots) {
    for (String rawSnapshot : rawSnapshots) {

      if (rawSnapshot.contains(getSnapshotName())) {
        return rawSnapshot;
      }
    }
    return null;
  }

  private String takeSnapshot() {
    return getSnapshotName() + jsonFunction.apply(current);
  }

  public String getSnapshotName() {
    String scenarioFormat = StringUtils.isBlank(scenario) ? "" : "[" + scenario + "]";
    return clazz.getName() + "." + method.getName() + scenarioFormat + "=";
  }
}
