package yimei.jss.rule.workcenter.basic;

import yimei.jss.jobshop.OperationOption;
import yimei.jss.jobshop.WorkCenter;
import yimei.jss.rule.AbstractRule;
import yimei.jss.rule.RuleType;
import yimei.jss.simulation.state.SystemState;

import java.util.List;

/**
 * Created by dyska on 6/06/17.
 * Longest ready time.
 * This rule should have a priority of the negative of the workCenter's ready time.
 */
public class LRT extends AbstractRule {
    private RuleType type;

    public LRT(RuleType t) {
        name = "\"LRT\"";
        this.type = t;
    }


    @Override
    public double priority(OperationOption op, WorkCenter workCenter, SystemState systemState) {
        return - workCenter.getReadyTime();
    }


    @Override
    public double[] priorityMulTree(OperationOption op, WorkCenter workCenter, SystemState systemState) {
        return new double[0];
    }

    public double priority(OperationOption op, List<WorkCenter> workCenterset, SystemState systemState) {
        double re = 0;
        for (int i = 0; i < workCenterset.size(); i++) {
            re = re + workCenterset.get(i).getReadyTime() / workCenterset.size();
        }
        return re;
    }
    }
