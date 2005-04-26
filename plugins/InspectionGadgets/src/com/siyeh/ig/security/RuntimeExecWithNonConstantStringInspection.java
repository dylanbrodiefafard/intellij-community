package com.siyeh.ig.security;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.psi.*;
import com.intellij.psi.util.ConstantExpressionUtil;
import com.siyeh.ig.BaseInspection;
import com.siyeh.ig.BaseInspectionVisitor;
import com.siyeh.ig.ExpressionInspection;
import com.siyeh.ig.GroupNames;

public class RuntimeExecWithNonConstantStringInspection extends ExpressionInspection {
    public String getID(){
        return "CallToRuntimeExecWithNonConstantString";
    }
    public String getDisplayName() {
        return "Call to 'Runtime.exec()' with with-constant string";
    }

    public String getGroupDisplayName() {
        return GroupNames.SECURITY_GROUP_NAME;
    }

    public String buildErrorString(PsiElement location) {
        return "Call to Runtime.#ref() is non-constant argument #loc";
    }

    public BaseInspectionVisitor createVisitor(InspectionManager inspectionManager, boolean onTheFly) {
        return new RuntimeExecVisitor(this, inspectionManager, onTheFly);
    }

    private static class RuntimeExecVisitor extends BaseInspectionVisitor {
        private RuntimeExecVisitor(BaseInspection inspection, InspectionManager inspectionManager, boolean isOnTheFly) {
            super(inspection, inspectionManager, isOnTheFly);
        }

        public void visitMethodCallExpression(PsiMethodCallExpression expression) {
            super.visitMethodCallExpression(expression);
            final PsiReferenceExpression methodExpression = expression.getMethodExpression();
            if (methodExpression == null) {
                return;
            }
            final String methodName = methodExpression.getReferenceName();
            if (!"exec".equals(methodName)) {
                return;
            }
            final PsiMethod method = expression.resolveMethod();
            if (method == null) {
                return;
            }
            final PsiClass aClass = method.getContainingClass();
            final String className = aClass.getQualifiedName();
            if (!"java.lang.Runtime".equals(className)) {
                return;
            }
            final PsiExpressionList argumentList = expression.getArgumentList();
            if(argumentList == null)
            {
                return;
            }
            final PsiExpression[] args = argumentList.getExpressions();
            if(args == null || args.length==0)
            {
                return;
            }
            final PsiExpression arg = args[0];
            final PsiType type = arg.getType();
            if(type == null)
            {
                return;
            }
            final String typeText = type.getCanonicalText();
            if(!"java.lang.String".equals(typeText))
            {
                return;
            }
            final String stringValue =
                    (String) ConstantExpressionUtil.computeCastTo(arg, type);
            if(stringValue!=null)
            {
                return;
            }
            registerMethodCallError(expression);
        }
    }

}
