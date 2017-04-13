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

import com.android.builder.model.BuildTypeContainer;
import com.android.builder.model.SourceProviderContainer;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Objects;

/**
 * Creates a deep copy of a {@link BuildTypeContainer}.
 */
public final class IdeBuildTypeContainer extends IdeModel implements BuildTypeContainer {
  // Increase the value when adding/removing fields or when changing the serialization/deserialization mechanism.
  private static final long serialVersionUID = 1L;

  @NotNull private final IdeBuildType myBuildType;
  @NotNull private final IdeSourceProvider mySourceProvider;
  @NotNull private final Collection<SourceProviderContainer> myExtraSourceProviders;

  public IdeBuildTypeContainer(@NotNull BuildTypeContainer container, @NotNull ModelCache modelCache) {
    myBuildType = new IdeBuildType(container.getBuildType(), modelCache);
    mySourceProvider = new IdeSourceProvider(container.getSourceProvider());
    myExtraSourceProviders = copy(container.getExtraSourceProviders(), modelCache, IdeSourceProviderContainer::new);
  }

  @Override
  @NotNull
  public IdeBuildType getBuildType() {
    return myBuildType;
  }

  @Override
  @NotNull
  public IdeSourceProvider getSourceProvider() {
    return mySourceProvider;
  }

  @Override
  @NotNull
  public Collection<SourceProviderContainer> getExtraSourceProviders() {
    return myExtraSourceProviders;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof IdeBuildTypeContainer)) {
      return false;
    }
    IdeBuildTypeContainer container = (IdeBuildTypeContainer)o;
    return Objects.equals(myBuildType, container.myBuildType) &&
           Objects.equals(mySourceProvider, container.mySourceProvider) &&
           Objects.equals(myExtraSourceProviders, container.myExtraSourceProviders);
  }

  @Override
  public int hashCode() {
    return Objects.hash(myBuildType, mySourceProvider, myExtraSourceProviders);
  }

  @Override
  public String toString() {
    return "IdeBuildTypeContainer{" +
           "myBuildType=" + myBuildType +
           ", mySourceProvider=" + mySourceProvider +
           ", myExtraSourceProviders=" + myExtraSourceProviders +
           '}';
  }
}
