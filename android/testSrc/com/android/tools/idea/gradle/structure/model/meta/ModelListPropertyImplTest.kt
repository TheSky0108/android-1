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
package com.android.tools.idea.gradle.structure.model.meta

import com.android.tools.idea.gradle.dsl.api.ext.ExtModel
import com.android.tools.idea.gradle.dsl.api.ext.GradlePropertyModel
import com.android.tools.idea.gradle.dsl.api.ext.ResolvedPropertyModel
import com.android.tools.idea.gradle.dsl.model.GradleFileModelTestCase
import com.android.tools.idea.gradle.structure.model.helpers.parseString
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

class ModelListPropertyImplTest : GradleFileModelTestCase() {

  object Model : ModelDescriptor<Model, Model, Model> {
    override fun getResolved(model: Model): Model? = null
    override fun getParsed(model: Model): Model? = this
    override fun setModified(model: Model) = Unit
  }

  private fun <T : Any> GradlePropertyModel.wrap(
    parse: (Nothing?, String) -> ParsedValue<T>,
    caster: ResolvedPropertyModel.() -> T?
  ): ModelListPropertyCore<T> {
    val resolved = resolve()
    return Model.listProperty(
      "description",
      resolvedValueGetter = { null },
      parsedPropertyGetter = { resolved },
      getter = { caster() },
      setter = { setValue(it) },
      parser = { context: Nothing?, value -> parse(context, value) }
    ).bind(Model)
  }

  @Test
  fun testPropertyValues() {
    val text = """
               ext {
                 propB = "2"
                 propC = "3"
                 propRef = propB
                 propInterpolated = "${'$'}{propB}nd"
                 propList = ["1", propB, propC, propRef, propInterpolated]
                 propListRef = propList
               }""".trimIndent()
    writeToBuildFile(text)

    val extModel = gradleBuildModel.ext()

    val propList = extModel.findProperty("propList").wrap(::parseString, ResolvedPropertyModel::asString)
    val propListRef = extModel.findProperty("propListRef").wrap(::parseString, ResolvedPropertyModel::asString)

    fun validateValues(list: ModelListPropertyCore<String>) {
      val editableValues = list.getEditableValues()
      val propA = editableValues[0]
      val propB = editableValues[1]
      val propC = editableValues[2]
      val propRef = editableValues[3]
      val propInterpolated = editableValues[4]

      assertThat(propA.testValue(), equalTo("1"))
      assertThat(propB.testValue(), equalTo("2"))
      assertThat(propC.testValue(), equalTo("3"))
      assertThat(propRef.testValue(), equalTo("2"))
      assertThat(propInterpolated.testValue(), equalTo("2nd"))
    }

    validateValues(propList)
    validateValues(propListRef)
  }

  @Test
  fun testWritePropertyValues() {
    val text = """
               ext {
                 propB = "2"
                 propC = "3"
                 propRef = propB
                 propInterpolated = "${'$'}{propB}nd"
                 propList = ["1", propB, propC, propRef, propInterpolated]
               }""".trimIndent()
    writeToBuildFile(text)

    val buildModel = gradleBuildModel
    val extModel = buildModel.ext()

    val list = extModel.findProperty("propList").wrap(::parseString, ResolvedPropertyModel::asString)
    var editableValues = list.getEditableValues()

    editableValues[0].testSetValue("A")
    editableValues[1].testSetReference("propC")
    editableValues[2].testSetInterpolatedString("${'$'}{propC}rd")
    editableValues[3].testSetValue("D")
    editableValues[4].testSetValue("E")

    fun verify(ext: ExtModel) {
      editableValues =
          ext.findProperty("propList").wrap(::parseString, ResolvedPropertyModel::asString).getEditableValues()
      val propA = editableValues[0]
      val prop3 = editableValues[1]
      val prop3rd = editableValues[2]
      val propD = editableValues[3]
      val propE = editableValues[4]

      assertThat(propA.testValue(), equalTo("A"))
      assertThat(prop3.testValue(), equalTo("3"))
      assertThat(prop3rd.testValue(), equalTo("3rd"))
      assertThat(propD.testValue(), equalTo("D"))
      assertThat(propE.testValue(), equalTo("E"))
    }

    verify(extModel)
    applyChangesAndReparse(buildModel)
    verify(buildModel.ext())
  }

  @Test
  fun testAddRemoveValues() {
    val text = """
               ext {
                 propB = "2"
                 propC = "3"
                 propRef = propB
                 propInterpolated = "${'$'}{propB}nd"
                 propList = ["1", propB, propC, propRef, propInterpolated]
               }""".trimIndent()
    writeToBuildFile(text)

    val buildModel = gradleBuildModel
    val extModel = buildModel.ext()

    val list = extModel.findProperty("propList").wrap(::parseString, ResolvedPropertyModel::asString)

    list.deleteItem(0)
    var editableValues = list.getEditableValues()
    editableValues[0].testSetReference("propC")
    editableValues[1].testSetInterpolatedString("${'$'}{propC}rd")
    editableValues[2].testSetValue("D")
    editableValues[3].testSetValue("E")

    list.addItem(4).testSetValue("ZZ")

    fun verify(ext: ExtModel) {
      editableValues =
          ext.findProperty("propList").wrap(::parseString, ResolvedPropertyModel::asString).getEditableValues()
      val prop3 = editableValues[0]
      val prop3rd = editableValues[1]
      val propD = editableValues[2]
      val propE = editableValues[3]
      val propZZ = editableValues[4]

      assertThat(prop3.testValue(), equalTo("3"))
      assertThat(prop3rd.testValue(), equalTo("3rd"))
      assertThat(propD.testValue(), equalTo("D"))
      assertThat(propE.testValue(), equalTo("E"))
      assertThat(propZZ.testValue(), equalTo("ZZ"))
    }

    verify(extModel)
    applyChangesAndReparse(buildModel)
    verify(buildModel.ext())
  }

  @Test
  fun testInsertRemoveValues() {
    val text = """
               ext {
                 propB = "2"
                 propC = "3"
                 propRef = propB
                 propInterpolated = "${'$'}{propB}nd"
                 propList = ["1", propB, propC, propRef, propInterpolated]
               }""".trimIndent()
    writeToBuildFile(text)

    val buildModel = gradleBuildModel
    val extModel = buildModel.ext()

    val list = extModel.findProperty("propList").wrap(::parseString, ResolvedPropertyModel::asString)

    list.deleteItem(2)
    var editableValues = list.getEditableValues()
    editableValues[0].testSetReference("propC")
    editableValues[1].testSetInterpolatedString("${'$'}{propC}rd")
    editableValues[2].testSetValue("D")
    editableValues[3].testSetValue("E")

    list.addItem(0).testSetValue("ZZ")

    fun verify(ext: ExtModel) {
      editableValues =
          ext.findProperty("propList").wrap(::parseString, ResolvedPropertyModel::asString).getEditableValues()
      val propZZ = editableValues[0]
      val prop3 = editableValues[1]
      val prop3rd = editableValues[2]
      val propD = editableValues[3]
      val propE = editableValues[4]

      assertThat(propZZ.testValue(), equalTo("ZZ"))
      assertThat(prop3.testValue(), equalTo("3"))
      assertThat(prop3rd.testValue(), equalTo("3rd"))
      assertThat(propD.testValue(), equalTo("D"))
      assertThat(propE.testValue(), equalTo("E"))
    }

    verify(extModel)
    applyChangesAndReparse(buildModel)
    verify(buildModel.ext())
  }
}
