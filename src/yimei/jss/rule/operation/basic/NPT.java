package yimei.jss.rule.operation.basic;

import yimei.jss.jobshop.OperationOption;
import yimei.jss.jobshop.WorkCenter;
import yimei.jss.rule.AbstractRule;
import yimei.jss.rule.RuleType;
import yimei.jss.simulation.state.SystemState;

import java.util.List;

/**
 * Created by yimei on 5/12/16.
 */
public class NPT extends AbstractRule {

    public NPT(RuleType type) {
        name = "\"NPT\"";
        this.type = type;
    }

    @Override
    public double priority(OperationOption op, WorkCenter workCenter, SystemState systemState) {
        return op.getNextProcTime();
    }

    @Override
    public double[] priorityMulTree(OperationOption op, WorkCenter workCenter, SystemState systemState) {
        return new double[0];
    }

    public double priority(OperationOption op, List<WorkCenter> workCenterset, SystemState systemState) {
        return op.getNextProcTime();
    }
    }
