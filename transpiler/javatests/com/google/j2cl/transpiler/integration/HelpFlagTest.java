/*
 * Copyright 2015 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.j2cl.transpiler.integration;

import java.io.IOException;

/**
 * Tests that the help flag dumps help information.
 */
public class HelpFlagTest extends IntegrationTestCase {
  public void testHelpFlag() throws IOException, InterruptedException {
    String[] args = new String[] {IntegrationTestCase.TRANSPILER_BINARY, "-help"};
    TranspileResult transpileResult = transpile(args);
    assertEquals(0, transpileResult.exitCode);
    assertLogContainsSnippet(transpileResult.outputLines, "<source files>");
    assertLogContainsSnippet(transpileResult.outputLines, "-bootclasspath <path>");
    assertLogContainsSnippet(transpileResult.outputLines, "-classpath (-cp) <path>");
    assertLogContainsSnippet(transpileResult.outputLines, "-d <directory>");
    assertLogContainsSnippet(transpileResult.outputLines, "-encoding <encoding>");
    assertLogContainsSnippet(transpileResult.outputLines, "-h (-help)");
    assertLogContainsSnippet(transpileResult.outputLines, "-source <release>");
    assertLogContainsSnippet(transpileResult.outputLines, "-sourcepath <file>");
  }
}
