package yimei.jss.rule.workcenter.basic;

import yimei.jss.jobshop.OperationOption;
import yimei.jss.jobshop.WorkCenter;
import yimei.jss.rule.AbstractRule;
import yimei.jss.rule.RuleType;
import yimei.jss.simulation.state.SystemState;

import java.util.List;

/**
 * Created by dyska on 6/06/17.
 * Work in queue.
 * The priority of this method should be the amount of work in the queue.
 */
public class WIQ extends AbstractRule {

    public WIQ(RuleType t) {
        name = "\"WIQ\"";
        this.type = t;
    }

    @Override
    public double priority(OperationOption op, WorkCenter workCenter, SystemState systemState) {
        return workCenter.getWorkInQueue();
    }

    @Override
    public double[] priorityMulTree(OperationOption op, WorkCenter workCenter, SystemState systemState) {
        return new double[0];
    }


    public double priority(OperationOption op, List<WorkCenter> workCenterset, SystemState systemState) {
        double re=0;
        for (int i = 0; i < workCenterset.size(); i++) {
            re=re+workCenterset.get(i).getWorkInQueue()/workCenterset.size();
        }
        return re;
    }
    }
