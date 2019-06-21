package io.github.jsonSnapshot.matchingstrategy;

import java.util.Arrays;

import lombok.NonNull;

import org.assertj.core.util.diff.DiffUtils;
import org.assertj.core.util.diff.Patch;

import io.github.jsonSnapshot.SnapshotDataItem;
import io.github.jsonSnapshot.SnapshotMatchException;
import io.github.jsonSnapshot.SnapshotMatchingStrategy;

public class StringEqualsMatchingStrategy implements SnapshotMatchingStrategy {
  public static final StringEqualsMatchingStrategy INSTANCE = new StringEqualsMatchingStrategy();

  private StringEqualsMatchingStrategy() {}

  @Override
  public void match(
      @NonNull final SnapshotDataItem expectedSnapshotItem, @NonNull final String currentObject) {

    final String rawSnapshotStr = expectedSnapshotItem.getData();

    if (!rawSnapshotStr.trim().equals(currentObject.trim())) {
      throw generateDiffError(rawSnapshotStr, currentObject);
    }
  }

  private SnapshotMatchException generateDiffError(
      @NonNull final String rawSnapshot, @NonNull final String currentObject) {
    // compute the patch: this is the diffutils part
    Patch<String> patch =
        DiffUtils.diff(
            Arrays.asList(rawSnapshot.trim().split("\n")),
            Arrays.asList(currentObject.trim().split("\n")));
    String error =
        "StringEqualsMatchRule - error on: \n"
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
}
