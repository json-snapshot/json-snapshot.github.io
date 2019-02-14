package io.github.jsonSnapshot;

import java.lang.reflect.Method;
import java.util.function.Function;

import io.github.jsonSnapshot.matchrule.SnapshotMatchRule;

public class Snapshot {

  private SnapshotFile snapshotFile;

  private Class clazz;

  private Method method;

  private Function<Object, String> jsonFunction;

  private SnapshotMatchRule snapshotMatchRule;

  private Object[] current;

  Snapshot(
      SnapshotFile snapshotFile,
      Class clazz,
      Method method,
      Function<Object, String> jsonFunction,
      SnapshotMatchRule snapshotMatchRule,
      Object... current) {
    this.current = current;
    this.snapshotFile = snapshotFile;
    this.clazz = clazz;
    this.method = method;
    this.jsonFunction = jsonFunction;
    this.snapshotMatchRule = snapshotMatchRule;
  }

  public void toMatchSnapshot() {

    final SnapshotData snapshots = snapshotFile.getStoredSnapshots();
    final SnapshotDataItem snapshotOrNull = snapshots.getItemByNameOrNull(getSnapshotName());

    final SnapshotDataItem currentObject = takeSnapshot();

    // Match Snapshot
    if (snapshotOrNull != null) {
      snapshotMatchRule.match(snapshotOrNull.getData(), currentObject.getData());
    }
    // Create New Snapshot
    else {
      snapshotFile.push(currentObject);
    }
  }

  private SnapshotDataItem takeSnapshot() {
    return SnapshotDataItem.ofNameAndData(getSnapshotName(), jsonFunction.apply(current));
  }

  public String getSnapshotName() {
    return clazz.getName() + "." + method.getName();
  }
}
