/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.tools.idea.gradle.project.sync;

import com.android.tools.idea.flags.StudioFlags;
import com.android.tools.idea.testing.AndroidGradleTestCase;

public abstract class GradleSyncIntegrationTestCase extends AndroidGradleTestCase {
  private boolean myUseNewGradleSync;

  @Override
  public void setUp() throws Exception {
    super.setUp();
    myUseNewGradleSync = StudioFlags.NEW_SYNC_INFRA_ENABLED.get();
    StudioFlags.NEW_SYNC_INFRA_ENABLED.override(useNewSyncInfrastructure());
  }

  @Override
  protected void tearDown() throws Exception {
    try {
      StudioFlags.NEW_SYNC_INFRA_ENABLED.override(myUseNewGradleSync); // back to default value.
    }
    finally {
      super.tearDown();
    }
  }

  protected abstract boolean useNewSyncInfrastructure();
}
