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
package com.android.tools.idea.ddms.actions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.android.ddmlib.IDevice;
import com.android.tools.idea.ddms.DeviceContext;
import com.android.tools.idea.testing.AndroidProjectRule;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

public final class ScreenRecorderActionTest {
  @Rule
  public final AndroidProjectRule myRule = AndroidProjectRule.inMemory();

  private DeviceContext myContext;
  private Features myFeatures;

  private Presentation myPresentation;
  private AnActionEvent myEvent;

  @Before
  public void mockContext() {
    myContext = Mockito.mock(DeviceContext.class);
  }

  @Before
  public void mockFeatures() {
    myFeatures = Mockito.mock(Features.class);
  }

  @Before
  public void mockEvent() {
    myPresentation = new Presentation();

    myEvent = Mockito.mock(AnActionEvent.class);
    Mockito.when(myEvent.getPresentation()).thenReturn(myPresentation);
  }

  @Test
  public void updateIsntEnabled() {
    new ScreenRecorderAction(myRule.getProject(), myContext, myFeatures).update(myEvent);

    assertFalse(myPresentation.isEnabled());
    assertEquals("Screen Record", myPresentation.getText());
  }

  @Test
  public void updateDeviceIsWatch() {
    IDevice device = Mockito.mock(IDevice.class);
    Mockito.when(device.isOnline()).thenReturn(true);

    Mockito.when(myContext.getSelectedDevice()).thenReturn(device);
    Mockito.when(myFeatures.watch(device)).thenReturn(true);

    new ScreenRecorderAction(myRule.getProject(), myContext, myFeatures).update(myEvent);

    assertFalse(myPresentation.isEnabled());
    assertEquals("Screen Record Is Unavailable for Wear OS", myPresentation.getText());
  }

  @Test
  public void updateDeviceDoesntHaveScreenRecord() {
    IDevice device = Mockito.mock(IDevice.class);
    Mockito.when(device.isOnline()).thenReturn(true);

    Mockito.when(myContext.getSelectedDevice()).thenReturn(device);

    new ScreenRecorderAction(myRule.getProject(), myContext, myFeatures).update(myEvent);

    assertFalse(myPresentation.isEnabled());
    assertEquals("Screen Record", myPresentation.getText());
  }

  @Test
  public void updateDeviceHasScreenRecord() {
    IDevice device = Mockito.mock(IDevice.class);
    Mockito.when(device.isOnline()).thenReturn(true);

    Mockito.when(myContext.getSelectedDevice()).thenReturn(device);
    Mockito.when(myFeatures.screenRecord(device)).thenReturn(true);

    new ScreenRecorderAction(myRule.getProject(), myContext, myFeatures).update(myEvent);

    assertTrue(myPresentation.isEnabled());
    assertEquals("Screen Record", myPresentation.getText());
  }
}
