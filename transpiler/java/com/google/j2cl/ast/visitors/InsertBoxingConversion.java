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
package com.google.j2cl.ast.visitors;

import com.google.j2cl.ast.AstUtils;
import com.google.j2cl.ast.CastExpression;
import com.google.j2cl.ast.CompilationUnit;
import com.google.j2cl.ast.Expression;
import com.google.j2cl.ast.TypeDescriptor;
import com.google.j2cl.ast.TypeDescriptors;

/**
 * Inserts a boxing operation when a primitive type is being put into a reference type slot in
 * assignment or method invocation conversion contexts.
 */
public class InsertBoxingConversion extends ConversionContextVisitor {

  public static void applyTo(CompilationUnit compilationUnit) {
    compilationUnit.accept(new InsertBoxingConversion());
  }

  public InsertBoxingConversion() {
    super(
        new ContextRewriter() {

          @Override
          public Expression rewriteAssignmentContext(
              TypeDescriptor toTypeDescriptor, Expression expression) {
            // There should be a following 'widening reference conversion' if the targeting type is
            // not the boxed type, but as widening reference conversion is always NOOP, and it is
            // mostly impossible to be optimized by JSCompiler, just avoid the insertion of the
            // NOOP cast here.
            return maybeBox(toTypeDescriptor, expression);
          }

          @Override
          public Expression rewriteCastContext(CastExpression castExpression) {
            TypeDescriptor toTypeDescriptor = castExpression.getCastTypeDescriptor();
            TypeDescriptor fromTypeDescriptor = castExpression.getExpression().getTypeDescriptor();
            if (!TypeDescriptors.isNonVoidPrimitiveType(toTypeDescriptor)
                && TypeDescriptors.isNonVoidPrimitiveType(fromTypeDescriptor)
                && !TypeDescriptors.isPrimitiveBooleanOrDouble(fromTypeDescriptor)) {
              // Actually remove the cast and replace it with the boxing.
              Expression boxedExpression = AstUtils.box(castExpression.getExpression());
              // It's possible that casting a primitive type to a non-boxed reference type.
              // e.g. (Object) i; in this case, just keep the NOOP casting after boxing.
              if (!boxedExpression.getTypeDescriptor().equalsIgnoreNullability(toTypeDescriptor)) {
                return CastExpression.create(boxedExpression, toTypeDescriptor);
              }
              return boxedExpression;
            }
            return castExpression;
          }

          @Override
          public Expression rewriteMethodInvocationContext(
              TypeDescriptor parameterTypeDescriptor, Expression argumentExpression) {
            return maybeBox(parameterTypeDescriptor, argumentExpression);
          }
        });
  }

  private static Expression maybeBox(TypeDescriptor toTypeDescriptor, Expression expression) {
    if (!TypeDescriptors.isNonVoidPrimitiveType(toTypeDescriptor)
        && TypeDescriptors.isNonVoidPrimitiveType(expression.getTypeDescriptor())
        && !TypeDescriptors.isPrimitiveBooleanOrDouble(expression.getTypeDescriptor())) {
      return AstUtils.box(expression);
    }
    return expression;
  }
}