/*
 * Copyright (C) 2019 The Android Open Source Project
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
package com.android.tools.idea.sqlite.ui.tableView;

import com.intellij.ui.table.JBTable;
import javax.swing.JPanel;

public class TablePanel {
  private JPanel myRoot;
  private JBTable myTable;
  private JPanel myControlsPanel;

  public JPanel getRoot() {
    return myRoot;
  }

  public JBTable getTable() {
    return myTable;
  }

  public JPanel getControlsPanel() {
    return myControlsPanel;
  }
}
