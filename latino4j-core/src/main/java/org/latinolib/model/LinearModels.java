package org.latinolib.model;

import de.bwaldvogel.liblinear.Parameter;
import de.bwaldvogel.liblinear.SolverType;

/**
 * Author mIHA
 */
public enum LinearModels
{
    LOGISTIC_REGRESSION {
        @Override
        public Parameter getDefaultParameter() {
            return new Parameter(SolverType.L2R_LR, 1.0, 0.01);
        }
    },
    SVM_MULTICLASS { // Crammer and Singer
        @Override
        public Parameter getDefaultParameter() {
            return new Parameter(SolverType.MCSVM_CS, 1.0, 0.1);
        }
    },
    SVM_CLASSIFIER {
        @Override
        public Parameter getDefaultParameter() {
            return new Parameter(SolverType.L2R_L2LOSS_SVC, 1.0, 0.01);
        }
    },
    SVM_REGRESSION {
        @Override
        public Parameter getDefaultParameter() {
            return new Parameter(SolverType.L2R_L2LOSS_SVR, 1.0, 0.001);
        }
    };

    public abstract Parameter getDefaultParameter();
}
