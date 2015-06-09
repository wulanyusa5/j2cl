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
 * Tests that unknown flags are reported.
 */
public class InvalidFlagTest extends IntegrationTestCase {
  public void testInvalidFlag() throws IOException, InterruptedException {
    String[] args = new String[] {IntegrationTestCase.TRANSPILER_BINARY, "-unknown", "abc"};
    TranspileResult transpileResult = transpile(args);
    assertLogContainsSnippet(
        transpileResult.errorLines, "invalid flag: \"-unknown\" is not a valid option");
    assertLogContainsSnippet(transpileResult.errorLines, "Valid options:");
    assertLogContainsSnippet(transpileResult.errorLines, "1 error(s).");
  }
}
