package yimei.jss.rule.workcenter.basic;

import yimei.jss.jobshop.OperationOption;
import yimei.jss.jobshop.WorkCenter;
import yimei.jss.rule.AbstractRule;
import yimei.jss.rule.RuleType;
import yimei.jss.simulation.state.SystemState;

import java.util.List;

/**
 * Created by dyska on 6/06/17.
 * Shortest busy time.
 * This rule should have a priority of the busy time of the workCenter.
 */
public class SBT extends AbstractRule {
    private RuleType type;

    public SBT(RuleType t) {
        name = "\"SBT\"";
        this.type = t;
    }

    @Override
    public double priority(OperationOption op, WorkCenter workCenter, SystemState systemState) {
        return workCenter.getBusyTime();
    }

    @Override
    public double[] priorityMulTree(OperationOption op, WorkCenter workCenter, SystemState systemState) {
        return new double[0];
    }


    public double priority(OperationOption op, List<WorkCenter> workCenterset, SystemState systemState) {
        double re = 0;
        for (int i = 0; i < workCenterset.size(); i++) {
            re = re + workCenterset.get(i).getBusyTime() / workCenterset.size();
        }
        return re;
    }
    }
