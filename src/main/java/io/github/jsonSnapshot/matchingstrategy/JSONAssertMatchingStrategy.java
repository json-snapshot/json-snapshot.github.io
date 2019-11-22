package io.github.jsonSnapshot.matchingstrategy;

import lombok.NonNull;

import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import io.github.jsonSnapshot.SnapshotDataItem;
import io.github.jsonSnapshot.SnapshotMatchException;
import io.github.jsonSnapshot.SnapshotMatchingStrategy;

public class JSONAssertMatchingStrategy implements SnapshotMatchingStrategy {
  public static final JSONAssertMatchingStrategy INSTANCE_LENIENT =
      new JSONAssertMatchingStrategy(JSONCompareMode.LENIENT);

  public static final JSONAssertMatchingStrategy INSTANCE_STRICT =
      new JSONAssertMatchingStrategy(JSONCompareMode.STRICT);

  private final JSONCompareMode compareMode;

  private JSONAssertMatchingStrategy(final JSONCompareMode compareMode) {
    this.compareMode = compareMode;
  }

  @Override
  public void match(
      @NonNull final SnapshotDataItem expectedSnapshotItem, @NonNull final String actualData) {
    try {
      JSONAssert.assertEquals(expectedSnapshotItem.getData(), actualData, compareMode);

    } catch (JSONException e) {
      final String error =
          "JSONAssertMatchRule - error with compareMode="
              + compareMode
              + ": Error on: \n"
              + actualData.trim()
              + "\n\n"
              + e.getMessage();
      throw new SnapshotMatchException(error, e);
    }
  }
}
