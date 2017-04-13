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
package com.android.tools.idea.gradle.project.model.ide.android;

import com.android.builder.model.TestedTargetVariant;
import org.jetbrains.annotations.NotNull;

/**
 * Creates a deep copy of {@link TestedTargetVariant}.
 *
 * @see IdeAndroidProject
 */
public class IdeTestedTargetVariants extends IdeModel implements TestedTargetVariant {
  @NotNull private final String myTargetProjectPath;
  @NotNull private final String myTargetVariant;

  public IdeTestedTargetVariants(@NotNull TestedTargetVariant tested) {
    myTargetProjectPath = tested.getTargetProjectPath();
    myTargetVariant = tested.getTargetVariant();
  }

  @Override
  @NotNull
  public String getTargetProjectPath() {
    return myTargetProjectPath;
  }

  @Override
  @NotNull
  public String getTargetVariant() {
    return myTargetVariant;
  }
}
