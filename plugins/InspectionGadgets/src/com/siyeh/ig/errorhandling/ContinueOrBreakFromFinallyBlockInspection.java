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
package com.siyeh.ig.errorhandling;

import com.intellij.codeInsight.daemon.GroupNames;
import com.intellij.psi.PsiBreakStatement;
import com.intellij.psi.PsiContinueStatement;
import com.intellij.psi.PsiStatement;
import com.siyeh.ig.BaseInspectionVisitor;
import com.siyeh.ig.StatementInspection;
import com.siyeh.ig.StatementInspectionVisitor;
import com.siyeh.ig.psiutils.ControlFlowUtils;
import org.jetbrains.annotations.NotNull;

public class ContinueOrBreakFromFinallyBlockInspection extends StatementInspection {

    public String getGroupDisplayName() {
        return GroupNames.ERRORHANDLING_GROUP_NAME;
    }

    public boolean isEnabledByDefault() {
        return true;
    }

    public BaseInspectionVisitor buildVisitor() {
        return new ContinueOrBreakFromFinallyBlockVisitor();
    }

    private static class ContinueOrBreakFromFinallyBlockVisitor extends StatementInspectionVisitor {

        public void visitContinueStatement(@NotNull PsiContinueStatement statement) {
            super.visitContinueStatement(statement);
            if (!ControlFlowUtils.isInFinallyBlock(statement)) {
                return;
            }
            final PsiStatement continuedStatement = statement.findContinuedStatement();
            if (continuedStatement == null) {
                return;
            }
            if (ControlFlowUtils.isInFinallyBlock(continuedStatement)) {
                return;
            }
            registerStatementError(statement);
        }

        public void visitBreakStatement(@NotNull PsiBreakStatement statement) {
            super.visitBreakStatement(statement);
            if (!ControlFlowUtils.isInFinallyBlock(statement)) {
                return;
            }
            final PsiStatement exitedStatement = statement.findExitedStatement();
            if (exitedStatement == null) {
                return;
            }
            if (ControlFlowUtils.isInFinallyBlock(exitedStatement)) {
                return;
            }
            registerStatementError(statement);
        }
    }
}