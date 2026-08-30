package yimei.jss.gp;

import ec.EvolutionState;
import ec.Individual;
import ec.Problem;
import ec.simple.SimpleProblemForm;
import yimei.jss.jobshop.OperationOption;
import yimei.jss.jobshop.WorkCenter;
import yimei.jss.simulation.state.SystemState;

import java.util.List;

/**
 * Created by YiMei on 27/09/16.
 */
public class CalcPriorityProblem extends Problem implements SimpleProblemForm {

    private OperationOption operation;
    private WorkCenter workCenter;
    private SystemState systemState;

    private List<WorkCenter> workCenterSet;

    public CalcPriorityProblem(OperationOption operation,
                               WorkCenter workCenter,
                               SystemState systemState) {
        this.operation = operation;
        this.workCenter = workCenter;
        this.systemState = systemState;
    }
    //LIUFEIGE
    public CalcPriorityProblem(OperationOption operation,
                               List<WorkCenter> workCenterset,
                               SystemState systemState) {
        this.operation = operation;
        this.workCenterSet = workCenterset;
        this.systemState = systemState;
    }
    public CalcPriorityProblem(OperationOption operation,
                               WorkCenter workCenter,
                               List<WorkCenter> workCenterset,
                               SystemState systemState) {
        this.operation = operation;
        this.workCenter = workCenter;
        this.workCenterSet = workCenterset;
        this.systemState = systemState;
    }
    public List<WorkCenter> getWorkCenterSet() {
        return workCenterSet;
    }

    public OperationOption getOperation() {
        return operation;
    }

    public WorkCenter getWorkCenter() {
        return workCenter;
    }


    public SystemState getSystemState() {
        return systemState;
    }
    @Override
    public void evaluate(EvolutionState state, Individual ind,
                         int subpopulation, int threadnum) {
    }
	@Override
	public void normObjective(EvolutionState state, Individual ind,
			                  int subpopulation, int threadnum) {
		// TODO Auto-generated method stub

	}
}
