package yimei.jss.niching;

import yimei.jss.jobshop.FlexibleStaticInstance;
import yimei.jss.jobshop.OperationOption;
import yimei.jss.jobshop.WorkCenter;
import yimei.jss.rule.AbstractRule;
import yimei.jss.rule.RuleType;
import yimei.jss.rule.operation.weighted.WSPT;
import yimei.jss.rule.workcenter.basic.WIQ;
import yimei.jss.simulation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * The phenotypic characterisation of rules.
 *
 * Created by YiMei on 3/10/16.
 */
//in this file, we have two class, the first one is PhenoCharacterisation
public abstract class PhenoCharacterisation {
    protected AbstractRule referenceRule;

    public PhenoCharacterisation(AbstractRule referenceRule) {
        this.referenceRule = referenceRule;
    }

    public AbstractRule getReferenceRule() {
        return referenceRule;
    }

    abstract void calcReferenceIndexes();

    public void setReferenceRule(AbstractRule rule) {
        this.referenceRule = rule;
        calcReferenceIndexes();
    }



    public abstract int[] characterise(AbstractRule rule);

    //the difference of the two arrays: sqrt
    public static double distance(int[] charList1, int[] charList2) {
        double distance = 0.0;
        for (int i = 0; i < charList1.length; i++) {
            double diff = charList1[i] - charList2[i];
            distance += diff * diff;
        }

        return Math.sqrt(distance);
    }
}

