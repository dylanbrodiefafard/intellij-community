/*
 * Copyright 2003-2005 Dave Griffith
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.siyeh.ig.security;

import com.intellij.codeInsight.daemon.GroupNames;
import com.intellij.psi.*;
import com.siyeh.InspectionGadgetsBundle;
import com.siyeh.ig.BaseInspectionVisitor;
import com.siyeh.ig.ExpressionInspection;
import com.siyeh.ig.psiutils.ClassUtils;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class UnsecureRandomNumberGenerationInspection
        extends ExpressionInspection {

    public String getGroupDisplayName() {
        return GroupNames.SECURITY_GROUP_NAME;
    }

    public String buildErrorString(PsiElement location) {
        @NonNls final String text = location.getText();
        if ("random".equals(text)) {
            return InspectionGadgetsBundle.message(
                    "unsecure.random.number.generation.problem.descriptor1");
        }
        else if ("Random".equals(text)) {
            return InspectionGadgetsBundle.message(
                    "unsecure.random.number.generation.problem.descriptor2");
        }
        else {
            return InspectionGadgetsBundle.message(
                    "unsecure.random.number.generation.problem.descriptor3");
        }
    }

    public BaseInspectionVisitor buildVisitor() {
        return new InsecureRandomNumberGenerationVisitor();
    }

    private static class InsecureRandomNumberGenerationVisitor
            extends BaseInspectionVisitor {

        public void visitNewExpression(@NotNull PsiNewExpression expression) {
            super.visitNewExpression(expression);
            final PsiJavaCodeReferenceElement reference =
                    expression.getClassReference();
            if (reference == null) {
                return;
            }
            final PsiElement element = reference.resolve();
            if (!(element instanceof PsiClass)) {
                return;
            }
            final PsiClass aClass = (PsiClass) element;
            if (!ClassUtils.isSubclass(aClass, "java.util.Random")) {
                return;
            }
            final String qualifiedName = aClass.getQualifiedName();
            if ("java.security.SecureRandom".equals(qualifiedName)) {
                return;
            }
            registerError(reference);
        }

        public void visitMethodCallExpression(
                @NotNull PsiMethodCallExpression expression) {
            super.visitMethodCallExpression(expression);
            final PsiReferenceExpression methodExpression =
                    expression.getMethodExpression();
            @NonNls final String methodName =
                    methodExpression.getReferenceName();
            if (!"random".equals(methodName)) {
                return;
            }
            final PsiMethod method = expression.resolveMethod();
            if (method == null) {
                return;
            }
            final PsiClass containingClass = method.getContainingClass();
            if (containingClass == null) {
                return;
            }
            final String className = containingClass.getQualifiedName();
            if (!"java.lang.Math".equals(className)) {
                return;
            }
            registerMethodCallError(expression);
        }
    }
}