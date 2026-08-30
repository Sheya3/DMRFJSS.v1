package yimei.jss.rule.operation.composite;

import yimei.jss.jobshop.OperationOption;
import yimei.jss.jobshop.WorkCenter;
import yimei.jss.rule.AbstractRule;
import yimei.jss.rule.RuleType;
import yimei.jss.simulation.state.SystemState;

import java.util.List;

/**
 * Created by YiMei on 27/09/16.
 */
public class LWKRplusPT extends AbstractRule {

    public LWKRplusPT(RuleType t) {
        name = "\"LWKR+SPT\"";
        this.type = t;
    }

    @Override
    public double priority(OperationOption op, WorkCenter workCenter, SystemState systemState) {
        return op.getWorkRemaining() + op.getProcTime();
    }

    @Override
    public double[] priorityMulTree(OperationOption op, WorkCenter workCenter, SystemState systemState) {
        return new double[0];
    }

    public double priority(OperationOption op, List<WorkCenter> workCenterset, SystemState systemState) {
        return op.getWorkRemaining() + op.getProcTime();
    }
    }
