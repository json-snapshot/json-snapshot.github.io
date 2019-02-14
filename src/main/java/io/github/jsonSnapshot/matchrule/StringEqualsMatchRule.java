package io.github.jsonSnapshot.matchrule;

import java.util.Arrays;

import org.assertj.core.util.diff.DiffUtils;
import org.assertj.core.util.diff.Patch;

import io.github.jsonSnapshot.SnapshotMatchException;
import lombok.NonNull;

public class StringEqualsMatchRule implements SnapshotMatchRule {
  public static final StringEqualsMatchRule INSTANCE = new StringEqualsMatchRule();

  private StringEqualsMatchRule() {}

  @Override
  public void match(@NonNull final String rawSnapshot, @NonNull final String currentObject) {
    if (!rawSnapshot.trim().equals(currentObject.trim())) {
      throw generateDiffError(rawSnapshot, currentObject);
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
