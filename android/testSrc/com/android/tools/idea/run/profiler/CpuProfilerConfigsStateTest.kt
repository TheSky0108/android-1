/*
 * Copyright (C) 2018 The Android Open Source Project
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
package com.android.tools.idea.run.profiler

import com.android.tools.idea.flags.StudioFlags
import com.google.common.truth.Truth.*
import org.junit.Test

import org.junit.Before

class CpuProfilerConfigsStateTest {
  private val myConfigsState = CpuProfilerConfigsState()

  @Before
  fun setUp() {
    StudioFlags.PROFILER_USE_ATRACE.override(true)
  }

  @Test
  fun testDefaultConfigs() {
    val configs = CpuProfilerConfigsState.getDefaultConfigs()
    assertThat(configs.map { it.name }).containsExactly(
        CpuProfilerConfig.Technology.SAMPLED_JAVA.getName(),
        CpuProfilerConfig.Technology.INSTRUMENTED_JAVA.getName(),
        CpuProfilerConfig.Technology.SAMPLED_NATIVE.getName(),
        CpuProfilerConfig.Technology.ATRACE.getName()
    ).inOrder()
  }

  @Test
  fun getConfigByNameFromDefaultConfigs() {
    assertThat(myConfigsState.getConfigByName(CpuProfilerConfig.Technology.SAMPLED_JAVA.getName())?.technology)
      .isEqualTo(CpuProfilerConfig.Technology.SAMPLED_JAVA)
    assertThat(myConfigsState.getConfigByName(CpuProfilerConfig.Technology.SAMPLED_NATIVE.getName())?.technology)
      .isEqualTo(CpuProfilerConfig.Technology.SAMPLED_NATIVE)
    assertThat(myConfigsState.getConfigByName(CpuProfilerConfig.Technology.INSTRUMENTED_JAVA.getName())?.technology)
      .isEqualTo(CpuProfilerConfig.Technology.INSTRUMENTED_JAVA)
    assertThat(myConfigsState.getConfigByName(CpuProfilerConfig.Technology.ATRACE.getName())?.technology)
      .isEqualTo(CpuProfilerConfig.Technology.ATRACE)
  }

  @Test
  fun atraceConfigHas32MbBuffer() {
    assertThat(myConfigsState.getConfigByName(CpuProfilerConfig.Technology.ATRACE.getName())?.technology)
      .isEqualTo(CpuProfilerConfig.Technology.ATRACE)
    assertThat(myConfigsState.getConfigByName(CpuProfilerConfig.Technology.ATRACE.getName())?.bufferSizeMb)
      .isEqualTo(CpuProfilerConfig.DEFAULT_ATRACE_BUFFER_SIZE_MB)
  }

  @Test
  fun addUserConfig() {
    val added = myConfigsState.addUserConfig(CpuProfilerConfig("MyConfig", CpuProfilerConfig.Technology.SAMPLED_JAVA))
    assertThat(added).isTrue()
    assertThat(myConfigsState.userConfigs).hasSize(1)
    assertThat(myConfigsState.userConfigs[0].name).isEqualTo("MyConfig")
  }

  @Test
  fun addUserConfigWithDefaultName() {
    val config = CpuProfilerConfig(CpuProfilerConfig.Technology.SAMPLED_JAVA.getName(), CpuProfilerConfig.Technology.SAMPLED_JAVA)
    val added = myConfigsState.addUserConfig(config)
    assertThat(added).isFalse()
    assertThat(myConfigsState.userConfigs).hasSize(0)
  }

  @Test
  fun addUserConfigWithDuplicatedName() {
    val configSampled = CpuProfilerConfig("MyConfig", CpuProfilerConfig.Technology.SAMPLED_JAVA)
    assertThat(myConfigsState.addUserConfig(configSampled)).isTrue()
    val configInstrumented = CpuProfilerConfig("MyConfig", CpuProfilerConfig.Technology.INSTRUMENTED_JAVA)
    assertThat(myConfigsState.addUserConfig(configInstrumented)).isFalse()
  }

  @Test
  fun getConfigByNameCustomConfig() {
    myConfigsState.userConfigs = listOf(CpuProfilerConfig("MyConfig", CpuProfilerConfig.Technology.SAMPLED_JAVA))
    assertThat(myConfigsState.getConfigByName("MyConfig")?.name).isEqualTo("MyConfig")
  }

  @Test
  fun getConfigByNameInvalid() {
    assertThat(myConfigsState.getConfigByName("invalid")).isNull()
  }

  @Test
  fun defaultConfigsDisableLiveAllocation() {
    CpuProfilerConfigsState.getDefaultConfigs().forEach {
      config -> assertThat(config.isDisableLiveAllocation).isTrue()
    }
  }
}