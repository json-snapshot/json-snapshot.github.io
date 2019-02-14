package io.github.jsonSnapshot.matchrule;

import java.util.Arrays;

import lombok.NonNull;

import org.assertj.core.util.diff.DiffUtils;
import org.assertj.core.util.diff.Patch;

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
