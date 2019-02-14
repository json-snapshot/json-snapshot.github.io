package io.github.jsonSnapshot.matchrule;

import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import io.github.jsonSnapshot.SnapshotMatchException;
import lombok.NonNull;

public class JSONAssertMatchRule implements SnapshotMatchRule {
  public static final JSONAssertMatchRule INSTANCE_LENIENT =
      new JSONAssertMatchRule(JSONCompareMode.LENIENT);

  public static final JSONAssertMatchRule INSTANCE_STRICT =
      new JSONAssertMatchRule(JSONCompareMode.STRICT);

  private final JSONCompareMode compareMode;

  private JSONAssertMatchRule(final JSONCompareMode compareMode) {
    this.compareMode = compareMode;
  }

  @Override
  public void match(@NonNull final String expectedData, @NonNull final String actualData) {
    try {
      JSONAssert.assertEquals(expectedData, actualData, compareMode);

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
