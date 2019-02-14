package io.github.jsonSnapshot.matchrule;

import lombok.NonNull;

import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import io.github.jsonSnapshot.SnapshotMatchException;

/*
 * #%L
 * json-snapshot.github.io
 * %%
 * Copyright (C) 2019 metas GmbH
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program. If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

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
