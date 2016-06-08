/*
 * Copyright (C) 2016 The Android Open Source Project
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
package com.android.tools.idea.uibuilder.mockup;

import com.android.tools.idea.uibuilder.model.NlComponent;
import com.android.tools.idea.uibuilder.model.NlModel;
import com.android.tools.idea.uibuilder.surface.DesignSurface;
import com.android.tools.idea.uibuilder.surface.ScreenView;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.ui.popup.IconButton;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.awt.RelativePoint;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

/**
 * Build and display the Mockup Editor dialog
 */
public class MockupEditor {
  public static final String TITLE = "Mockup Editor";
  private static final double RELATIVE_SIZE_TO_SOURCE = 0.80;
  private static final IconButton CANCEL_BUTTON = new IconButton("Close", AllIcons.Actions.Close, AllIcons.Actions.CloseHovered);
  private static JBPopup POPUP_INSTANCE = null;
  private final ScreenView myScreenView;
  private final MockupComponentAttributes myMockupComponentAttributes;
  NlModel myModel;

  // Form generated components (Do not removed if referenced in the form)
  private TextFieldWithBrowseButton myFileChooser;
  private MockupResizingPanel myMockupResizingPanel;
  private JSlider mySlider;
  private JPanel myContentPane;

  public MockupEditor(ScreenView screenView, MockupComponentAttributes mockupComponentAttributes, NlModel model) {
    myScreenView = screenView;
    myModel = model;
    myMockupComponentAttributes = mockupComponentAttributes;

    initFileChooserText();
    initFileChooserActionListener();
    initSlider();
  }

  private void initSlider() {
    mySlider.setValue(Math.round(myMockupComponentAttributes.getAlpha() * mySlider.getMaximum()));
    mySlider.addMouseListener(new MouseAdapter() {

      @Override
      public void mouseReleased(MouseEvent e) {
        MockupFileHelper.writeOpacityToXML(mySlider.getValue() / (float)mySlider.getMaximum(), myMockupComponentAttributes.getComponent());
      }
      
    });
    mySlider.addChangeListener(e -> {
      final JSlider source = (JSlider)e.getSource();
      myMockupComponentAttributes.setAlpha(source.getValue() / (float)source.getMaximum());
      myContentPane.repaint();
    });
  }

  private void initFileChooserActionListener() {
    myFileChooser.addActionListener(e -> {
      if (myMockupComponentAttributes == null) {
        return;
      }
      final FileChooserDescriptor descriptor = MockupFileHelper.getFileChooserDescriptor();
      VirtualFile selectedFile = myMockupComponentAttributes.getVirtualFile();

      FileChooser.chooseFile(descriptor, null, myContentPane, selectedFile, (virtualFile) -> {
        MockupFileHelper.writeFileNameToXML(virtualFile, myMockupComponentAttributes.getComponent());
        myFileChooser.setText(virtualFile.getName());
      });
    });
  }

  private void initFileChooserText() {
    if (myMockupComponentAttributes != null) {
      final VirtualFile virtualFile = myMockupComponentAttributes.getVirtualFile();
      if (virtualFile != null) {
        myFileChooser.setText(virtualFile.getName());
      }
    }
  }

  /**
   * Create a popup showing the tools to edit the mockup of the selected component
   *
   * @param designSurface
   * @param nlModel
   */
  public static void createPopup(DesignSurface designSurface, NlModel nlModel) {
    // Close any pop-up already opened
    if (POPUP_INSTANCE != null) {
      POPUP_INSTANCE.cancel();
    }

    // Do not show the popup if nothing is selected
    final List<NlComponent> selection = nlModel.getSelectionModel().getSelection();
    if (selection.isEmpty()) {
      return;
    }
    final NlComponent component = selection.get(0);
    final MockupComponentAttributes mockupAttributes = MockupComponentAttributes.create(component);
    if (mockupAttributes == null) {
      return;
    }

    final ScreenView screenView = new ScreenView(designSurface, ScreenView.ScreenViewType.BLUEPRINT, nlModel);
    final MockupEditor mockupEditor = new MockupEditor(screenView, mockupAttributes,
                                                       component.getModel());
    final Dimension minSize = new Dimension((int)Math.round(designSurface.getWidth() * RELATIVE_SIZE_TO_SOURCE),
                                            (int)Math.round(designSurface.getHeight() * RELATIVE_SIZE_TO_SOURCE));

    JBPopup builder = JBPopupFactory.getInstance()
      .createComponentPopupBuilder(mockupEditor.myContentPane, mockupEditor.myContentPane)
      .setTitle(TITLE)
      .setResizable(true)
      .setMovable(true)
      .setMinSize(minSize)
      .setRequestFocus(true)
      .setCancelOnClickOutside(false)
      .setLocateWithinScreenBounds(true)
      .setShowShadow(true)
      .setCancelOnWindowDeactivation(false)
      .setCancelButton(CANCEL_BUTTON)
      .createPopup();

    // Center the popup in the design surface
    RelativePoint point = new RelativePoint(
      designSurface,
      new Point(
        (int)Math.round(designSurface.getWidth() / 2 - minSize.getWidth() / 2),
        (int)Math.round(designSurface.getHeight() / 2 - minSize.getHeight() / 2))
    );
    builder.show(point);

    POPUP_INSTANCE = builder;
  }

  private void createUIComponents() {
    myMockupResizingPanel = new MockupResizingPanel(myScreenView, myMockupComponentAttributes);
  }
}
