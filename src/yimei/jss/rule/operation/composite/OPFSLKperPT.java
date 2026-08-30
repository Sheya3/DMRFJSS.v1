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
public class OPFSLKperPT extends AbstractRule {

    public OPFSLKperPT(RuleType t) {
        name = "\"OPFSLK/PT\"";
        this.type = t;
    }

    @Override
    public double priority(OperationOption op, WorkCenter workCenter, SystemState systemState) {
        double value = (systemState.getClockTime() + op.getProcTime()
                - op.getFlowDueDate()) / op.getProcTime();

        if (value < 0)
            value = 0;

        return value;
    }

    @Override
    public double[] priorityMulTree(OperationOption op, WorkCenter workCenter, SystemState systemState) {
        return new double[0];
    }

    public double priority(OperationOption op, List<WorkCenter> workCenterset, SystemState systemState) {
        double value = (systemState.getClockTime() + op.getProcTime()
                - op.getFlowDueDate()) / op.getProcTime();

        if (value < 0)
            value = 0;

        return value;
    }
    }
