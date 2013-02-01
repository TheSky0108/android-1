/*
 * Copyright (C) 2013 The Android Open Source Project
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

package org.jetbrains.android.lang.rs;

import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import junit.framework.TestCase;

public class RenderscriptLexerTest extends TestCase {
  private void validateLexer(String input, IElementType... tokens) {
    int i = 0;

    RenderscriptLexer lexer = new RenderscriptLexer();

    for (lexer.start(input); lexer.getTokenType() != null; lexer.advance(), i++) {
      assertTrue(String.format("More tokens than expected (%1$d)", tokens.length),
                 i < tokens.length);
      assertEquals("Mismatch at '" + lexer.getTokenText() + "'", tokens[i], lexer.getTokenType());
    }
  }

  public void test1() {
    validateLexer("const int i;",
                  RenderscriptTokenType.KEYWORD,
                  TokenType.WHITE_SPACE,
                  RenderscriptTokenType.KEYWORD,
                  TokenType.WHITE_SPACE,
                  RenderscriptTokenType.IDENTIFIER,
                  RenderscriptTokenType.SEPARATOR);
  }

  public void testCStyleComment() {
    validateLexer("foo(var/* comment */);",
                  RenderscriptTokenType.IDENTIFIER,
                  RenderscriptTokenType.BRACE,
                  RenderscriptTokenType.IDENTIFIER,
                  RenderscriptTokenType.COMMENT,
                  RenderscriptTokenType.BRACE,
                  RenderscriptTokenType.SEPARATOR);
  }

  public void testCppComment() {
    validateLexer("float// foo\n\tdouble",
                  RenderscriptTokenType.KEYWORD,
                  RenderscriptTokenType.COMMENT,
                  TokenType.WHITE_SPACE,
                  RenderscriptTokenType.KEYWORD);
  }

  public void testString() {
    validateLexer("\"Simple string\\ntest\\rwith escape\\chars.\";",
                  RenderscriptTokenType.STRING,
                  RenderscriptTokenType.SEPARATOR);

    // current lexer recognizes a string even if it doesn't terminate with a quote
    validateLexer("\"No end quote", RenderscriptTokenType.STRING);

    // a newline should terminate a string as far as the lexer is concerned
    validateLexer("\"Line separator\n\"",
                  RenderscriptTokenType.STRING,   // string before \n
                  TokenType.WHITE_SPACE,          // \n
                  RenderscriptTokenType.STRING);  // the quote  after \n
  }

  public void testCharacterLiterals() {
    validateLexer("'a'", RenderscriptTokenType.CHARACTER);
    validateLexer("'\\n'", RenderscriptTokenType.CHARACTER);
    validateLexer("'\\\\'", RenderscriptTokenType.CHARACTER);

    // newline should terminate a character literal (as far as the lexer is concerned)
    validateLexer("'\n'",
                  RenderscriptTokenType.CHARACTER,  // first quote
                  TokenType.WHITE_SPACE,            // newline
                  RenderscriptTokenType.CHARACTER); // second quote
  }

  public void testNumbers() {
    validateLexer("3.14f", RenderscriptTokenType.NUMBER);
    validateLexer("2.1e-123", RenderscriptTokenType.NUMBER);
  }
}
