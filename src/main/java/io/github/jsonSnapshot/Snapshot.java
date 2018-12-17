package io.github.jsonSnapshot;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import org.assertj.core.util.diff.DiffUtils;
import org.assertj.core.util.diff.Patch;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static io.github.jsonSnapshot.SnapshotMatcher.defaultJsonFunction;

public class Snapshot {

  private SnapshotFile snapshotFile;

  private Class clazz;

  private Method method;

  private Function<Object, String> jsonFunction;

  private final List<String> pathsToIgnore = new ArrayList<>();

  private Object[] current;

  Snapshot(
      SnapshotFile snapshotFile,
      Class clazz,
      Method method,
      Function<Object, String> jsonFunction,
      Object... current) {
    this.current = current;
    this.snapshotFile = snapshotFile;
    this.clazz = clazz;
    this.method = method;
    this.jsonFunction = jsonFunction;
  }

  public void toMatchSnapshot() {

    Set<String> rawSnapshots = snapshotFile.getRawSnapshots();

    String rawSnapshot = getRawSnapshot(rawSnapshots);

    String currentObject = takeSnapshot();

    // Match Snapshot
    if (rawSnapshot != null) {
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

  private Object ignorePaths(Object input) {
    DocumentContext context = JsonPath.using(new JacksonJsonProvider()).parse(defaultJsonFunction().apply(input));
    this.pathsToIgnore.forEach(path -> context.delete(path));
    String root = "$";
    return context.read(root);
  }

  private Object[] getCurrentWithoutIgnoredPaths() {
    if (pathsToIgnore.isEmpty() || current == null) {
      return current;
    }

    return Arrays.asList(current).stream()
        .map(this::ignorePaths)
        .collect(Collectors.toList())
        .toArray();
  }

  private String takeSnapshot() {
    return getSnapshotName() + jsonFunction.apply(getCurrentWithoutIgnoredPaths());
  }

  public String getSnapshotName() {
    return clazz.getName() + "." + method.getName() + "=";
  }

  /**
   * Ignore fields when comparing object and snapshot.
   *
   * @param jsonPathsToIgnore A list of paths to ignore in <a href="https://github.com/json-path/JsonPath">JsonPath</a> syntax.
   */
  public Snapshot ignoring(String... jsonPathsToIgnore) {
    this.pathsToIgnore.addAll(Arrays.asList(jsonPathsToIgnore));
    return this;
  }
}
