package yimei.jss.rule.workcenter.basic;

import yimei.jss.jobshop.OperationOption;
import yimei.jss.jobshop.WorkCenter;
import yimei.jss.rule.AbstractRule;
import yimei.jss.rule.RuleType;
import yimei.jss.simulation.state.SystemState;

import java.util.List;

/**
 * Created by dyska on 6/06/17.
 * Longest busy time.
 * This rule should have as its priority the negative of the busy time of the workCenter.
 * Should always be a non-negative quantity before taking its negative.
 */
public class LBT extends AbstractRule {
    private RuleType type;

    public LBT(RuleType type) {
        name = "\"LBT\"";
        this.type = type;
    }

    @Override
    public double priority(OperationOption op, WorkCenter workCenter, SystemState systemState) {
        return -workCenter.getBusyTime();
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
