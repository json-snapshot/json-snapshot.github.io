package io.github.jsonSnapshot;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

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

class SnapshotFileTest {

  @Test
  void split_n() {
    verifySplitWithLineBreak("\n");
  }

  @Test
  void split_r() {
    verifySplitWithLineBreak("\r");
  }

  @Test
  void split_rn() {
    verifySplitWithLineBreak("\r\n");
  }

  private void verifySplitWithLineBreak(final String linebreak) {
    String str = "blahblahblah_1" + linebreak + linebreak + linebreak + "blahblahblah_2";

    String[] result = SnapshotFile.split(str);
    assertThat(result[0]).isEqualTo("blahblahblah_1");
    assertThat(result[1]).isEqualTo("blahblahblah_2");
  }
}
