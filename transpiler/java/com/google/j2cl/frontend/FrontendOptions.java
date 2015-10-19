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
package com.google.j2cl.frontend;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSet;
import com.google.j2cl.errors.Errors;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Frontend options, which can be initialized by a Flag instance that is already parsed.
 */
public class FrontendOptions {
  private final Errors errors;

  private List<String> classpathEntries;
  private List<String> sourcepathEntries;
  private List<String> bootclassPathEntries;
  private List<String> nativesourcezipEntries;
  private String output;
  private String encoding;
  private String sourceVersion;
  private List<String> sourceFilePaths;
  private List<String> superSourceFilePaths;
  private FileSystem outputFileSystem;

  private static final Set<String> VALID_JAVA_VERSIONS =
      ImmutableSet.of("1.8", "1.7", "1.6", "1.5");

  private static final String ZIP_EXTENSION = "zip";

  public FrontendOptions(Errors errors, FrontendFlags flags) {
    this.errors = errors;
    initOptions(flags);
  }

  /**
   * Initialize compiler options by parsed flags.
   */
  public void initOptions(FrontendFlags flags) {
    setClasspathEntries(flags.classpath);
    setSourcepathEntries(flags.sourcepath);
    setBootclassPathEntries(flags.bootclasspath);
    setNativeSourceZipEntries(flags.nativesourceszippath);
    setOutput(flags.output);
    setSourceFiles(flags.files);
    setSuperSourceFiles(flags.superfiles);
    setSourceVersion(flags.source);
    setEncoding(flags.encoding);
  }

  public List<String> getClasspathEntries() {
    return this.classpathEntries;
  }

  public void setClasspathEntries(String classpath) {
    this.classpathEntries = getPathEntries(classpath);
  }

  public List<String> getSourcepathEntries() {
    return this.sourcepathEntries;
  }

  public void setSourcepathEntries(String sourcepath) {
    this.sourcepathEntries = getPathEntries(sourcepath);
  }

  public List<String> getBootclassPathEntries() {
    return this.bootclassPathEntries;
  }

  public void setBootclassPathEntries(String bootclassPath) {
    this.bootclassPathEntries = getPathEntries(bootclassPath);
  }

  public List<String> getNativeSourceZipEntries() {
    return this.nativesourcezipEntries;
  }

  public void setNativeSourceZipEntries(String zipFilePath) {
    List<String> zipFilePaths =
        Splitter.on(File.pathSeparator).omitEmptyStrings().splitToList(zipFilePath);
    if (checkNativeSourceZipEntries(zipFilePaths)) {
      this.nativesourcezipEntries = zipFilePaths;
    }
  }

  private boolean checkNativeSourceZipEntries(List<String> zipFilePaths) {
    for (String path : zipFilePaths) {
      File file = new File(path);
      if (!file.exists()) {
        errors.error(Errors.Error.ERR_FILE_NOT_FOUND, path);
        return false;
      }
    }
    return true;
  }

  public String getOutput() {
    return this.output;
  }

  /**
   * Sets the output location.
   * <p>
   * The location can be either a directory or a zip file.
   */
  public void setOutput(String output) {
    Path outputPath = Paths.get(output);
    if (Files.exists(outputPath)
        && !Files.isDirectory(outputPath)
        && !output.endsWith(ZIP_EXTENSION)) {
      errors.error(Errors.Error.ERR_OUTPUT_LOCATION);
    }

    if (output.endsWith(ZIP_EXTENSION)) {
      // jar:file://output/Location/Path.zip!relative/File/Path.js
      initZipOutput(outputPath);
      return;
    }
    // file://output/Location/Path/relative/File/Path.js
    initDirOutput(output);
  }

  public FileSystem getOutputFileSystem() {
    return outputFileSystem;
  }

  public String getEncoding() {
    return this.encoding;
  }

  public void setEncoding(String encoding) {
    if (checkEncoding(encoding)) {
      this.encoding = encoding;
    }
  }

  public boolean checkEncoding(String encoding) {
    try {
      // Verify encoding has a supported charset.
      Charset.forName(encoding);
    } catch (UnsupportedCharsetException e) {
      errors.error(Errors.Error.ERR_UNSUPPORTED_ENCODING, encoding);
      return false;
    }
    return true;
  }

  public String getSourceVersion() {
    return this.sourceVersion;
  }

  public void setSourceVersion(String sourceVersion) {
    if (checkSourceVersion(sourceVersion)) {
      this.sourceVersion = sourceVersion;
    }
  }

  public boolean checkSourceVersion(String sourceVersion) {
    if (!VALID_JAVA_VERSIONS.contains(sourceVersion)) {
      errors.error(Errors.Error.ERR_INVALID_SOURCE_VERSION, sourceVersion);
      return false;
    }
    return true;
  }

  public List<String> getSourceFiles() {
    return this.sourceFilePaths;
  }

  public void setSourceFiles(List<String> sourceFiles) {
    if (checkJavaSourceFiles(sourceFiles)) {
      this.sourceFilePaths = sourceFiles;
    }
  }

  public List<String> getSuperSourceFiles() {
    return this.superSourceFilePaths;
  }

  public void setSuperSourceFiles(String superSourceFiles) {
    List<String> superSourceFilePaths =
        Splitter.on(File.pathSeparator).omitEmptyStrings().splitToList(superSourceFiles);
    if (checkJavaSourceFiles(superSourceFilePaths)) {
      this.superSourceFilePaths = superSourceFilePaths;
    }
  }

  private boolean checkJavaSourceFiles(List<String> sourceFiles) {
    for (String sourceFile : sourceFiles) {
      if (sourceFile.endsWith(".java")) {
        File file = new File(sourceFile);
        if (!file.exists()) {
          errors.error(Errors.Error.ERR_FILE_NOT_FOUND, sourceFile);
          return false;
        }
        if (!file.isFile()) {
          errors.error(Errors.Error.ERR_INVALID_SOURCE_FILE, sourceFile);
          return false;
        }
      } else {
        // does not support non-java files.
        errors.error(Errors.Error.ERR_INVALID_SOURCE_FILE, sourceFile);
        return false;
      }
    }
    return true;
  }

  private void initDirOutput(String output) {
    this.outputFileSystem = FileSystems.getDefault();
    this.output = output;
  }

  private void initZipOutput(Path outputPath) {
    try {
      Map<String, String> env = new HashMap<>();
      env.put("create", "true");
      this.outputFileSystem =
          FileSystems.newFileSystem(
              URI.create("jar:file:" + outputPath.toUri().getPath()), env, null);
    } catch (IOException e) {
      errors.error(Errors.Error.ERR_CANNOT_OPEN_ZIP, outputPath.toString());
    }
    this.output = null;
  }

  private static List<String> getPathEntries(String path) {
    List<String> entries = new ArrayList<>();
    for (String entry : Splitter.on(File.pathSeparatorChar).omitEmptyStrings().split(path)) {
      if (new File(entry).exists()) {
        entries.add(entry);
      }
    }
    return entries;
  }
}
