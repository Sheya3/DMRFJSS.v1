package yimei.jss.rule;

import ec.EvolutionState;
import ec.Fitness;
import ec.multiobjective.MultiObjectiveFitness;
import ec.util.Parameter;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import yimei.jss.jobshop.Objective;
import yimei.jss.jobshop.OperationOption;
import yimei.jss.jobshop.SchedulingSet;
import yimei.jss.jobshop.WorkCenter;
import yimei.jss.ruleanalysis.TestResult;
import yimei.jss.simulation.RoutingDecisionSituation;
import yimei.jss.simulation.SequencingDecisionSituation;
import yimei.jss.simulation.Simulation;
import yimei.jss.simulation.state.SystemState;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * The abstract dispatching rule for job shop scheduling.
 * <p>
 * Created by yimei on 22/09/16.
 */
public abstract class AbstractRule extends EvolutionState{

	protected String name;
	protected RuleType type;

	//fzhang 2018.10.10  get seed
    protected long jobSeed;

	public String getName() {
		return name;
	}

	public RuleType getType() {
		return type;
	}


	@Override
	public String toString() {
		return name;
	}

	public RealMatrix objectiveValueMatrix(SchedulingSet schedulingSet, List<Objective> objectives) {
		int rows = schedulingSet.getObjectiveLowerBoundMtx().getRowDimension();
		int cols = schedulingSet.getObjectiveLowerBoundMtx().getColumnDimension();

		RealMatrix matrix = new Array2DRowRealMatrix(rows, cols);
		List<Simulation> simulations = schedulingSet.getSimulations();
		int col = 0;

		for (int j = 0; j < simulations.size(); j++) {
			Simulation simulation = simulations.get(j);
			simulation.setSequencingRule(this);

			simulation.run();
			// System.out.println(simulation.workCenterUtilLevelsToString());

			for (int i = 0; i < objectives.size(); i++) {
				matrix.setEntry(i, col, simulation.objectiveValue(objectives.get(i)));
			}

			col++;

			for (int k = 1; k < schedulingSet.getReplications().get(j); k++) {
				simulation.rerun();
				// System.out.println(simulation.workCenterUtilLevelsToString());

				for (int i = 0; i < objectives.size(); i++) {
					matrix.setEntry(i, col, simulation.objectiveValue(objectives.get(i)));
				}

				col++;
			}

			simulation.reset();
		}

		return matrix;
	}

	public void calcFitness(Fitness fitness, EvolutionState state, SchedulingSet schedulingSet, AbstractRule otherRule,
			List<Objective> objectives) {
		// whenever fitness is calculated, need a routing rule and a sequencing rule
		if (this.getType() == otherRule.getType()) {
			System.out.println(
					"We need one routing rule and one sequencing rule, not 2 " + otherRule.getType() + " rules.");
			return;
		}
		AbstractRule routingRule;
		AbstractRule sequencingRule;
		// check type, not here
		if (this.getType() == RuleType.ROUTING) {
			routingRule = this;
			sequencingRule = otherRule;
		} else {
			routingRule = otherRule;
			sequencingRule = this;
		}

		double[] fitnesses = new double[objectives.size()];

		List<Simulation> simulations = schedulingSet.getSimulations();
		int col = 0;
		int badrun = 0;
		//System.out.println("The simulation size is "+simulations.size()); //1
		for (int j = 0; j < simulations.size(); j++) {
			Simulation simulation = simulations.get(j);
			simulation.setSequencingRule(sequencingRule);
			simulation.setRoutingRule(routingRule);
			// }
			simulation.rerun();
//			System.out.println(simulation.getSystemState().getJobsCompleted());
			for (int i = 0; i < objectives.size(); i++) {
				// System.out.println("Makespan:
				// "+simulation.objectiveValue(objectives.get(i)));
				// System.out.println("Benchmark makespan:
				// "+schedulingSet.getObjectiveLowerBound(i, col));
				
				//fzhang 2018.10.23  cancel normalizing objective
//				double normObjValue = simulation.objectiveValue(objectives.get(i))
//						/ schedulingSet.getObjectiveLowerBound(i, col);
				
				double ObjValue = simulation.objectiveValue(objectives.get(i));

				//modified by fzhang, 26.4.2018  check in test process, whether there is ba
				//fzhang 2018.10.23  cancel normalizing objective
//				fitnesses[i] += normObjValue;
				if(ObjValue>9999999){
//					ObjValue = 0;
					badrun++;
				}
				fitnesses[i] += ObjValue;
				System.out.println(ObjValue);
			}

			col++;

			//System.out.println("The value of replication is "+schedulingSet.getReplications()); //50
			// if the constraint is over 500, the objective is recorded as the largest one

			for (int k = 1; k < schedulingSet.getReplications().get(j); k++) {
//			for (int k = 1; k < 2; k++) {
				if(k==11){
					int aaa=0;
				}
			simulation.rerun();
				System.out.println(simulation.getSystemState().getWorkCenters());
				for (int i = 0; i < objectives.size(); i++) {
//					double normObjValue = simulation.objectiveValue(objectives.get(i))
//							/ schedulingSet.getObjectiveLowerBound(i, col);
//					fitnesses[i] += normObjValue;
				
					//fzhang 2018.10.23  cancel normalizing objective
					double ObjValue = simulation.objectiveValue(objectives.get(i));
					if(ObjValue>9999999){
						ObjValue = 0;
						badrun++;
						WorkCenter overM = simulation.getSystemState().getWorkCenter(0);
						for (int l = 0; l < simulation.getSystemState().getWorkCenters().size(); l++) {
							if(simulation.getSystemState().getWorkCenters().get(l).getQueue().size()>500)
								overM=simulation.getSystemState().getWorkCenters().get(l);
						}
//						System.out.println("completed: "+ simulation.getSystemState().getJobsCompleted().size()+
//								"  badmachine: " +  overM);
					}
					fitnesses[i] += ObjValue;
				}
				col++;
			}

			simulation.reset();
		}
		System.out.println("badindIn50Instance: " + badrun);
		for (int i = 0; i < fitnesses.length; i++) {
//			fitnesses[i] /= col;
			fitnesses[i] /= (col-badrun);
		}

		MultiObjectiveFitness f = (MultiObjectiveFitness) fitness;
		f.setObjectives(state, fitnesses);
	}

	public void calcFitness(TestResult result, EvolutionState state, SchedulingSet schedulingSet, AbstractRule otherRule,
							List<Objective> objectives, int Gen) {
		Fitness fitness=result.getGenerationalTestFitness(Gen);
		// whenever fitness is calculated, need a routing rule and a sequencing rule
		if (this.getType() == otherRule.getType()) {
			System.out.println(
					"We need one routing rule and one sequencing rule, not 2 " + otherRule.getType() + " rules.");
			return;
		}
		AbstractRule routingRule;
		AbstractRule sequencingRule;
		// check type, not here
		if (this.getType() == RuleType.ROUTING) {
			routingRule = this;
			sequencingRule = otherRule;
		} else {
			routingRule = otherRule;
			sequencingRule = this;
		}

		double[] fitnesses = new double[objectives.size()];

		List<Simulation> simulations = schedulingSet.getSimulations();
		int col = 0;

		//System.out.println("The simulation size is "+simulations.size()); //1
		for (int j = 0; j < simulations.size(); j++) {
			Simulation simulation = simulations.get(j);
			simulation.setSequencingRule(sequencingRule);
			simulation.setRoutingRule(routingRule);
			// }
			simulation.rerun();

			for (int i = 0; i < objectives.size(); i++) {
				// System.out.println("Makespan:
				// "+simulation.objectiveValue(objectives.get(i)));
				// System.out.println("Benchmark makespan:
				// "+schedulingSet.getObjectiveLowerBound(i, col));

				//fzhang 2018.10.23  cancel normalizing objective
//				double normObjValue = simulation.objectiveValue(objectives.get(i))
//						/ schedulingSet.getObjectiveLowerBound(i, col);

				double ObjValue = simulation.objectiveValue(objectives.get(i));

				//modified by fzhang, 26.4.2018  check in test process, whether there is ba
				//fzhang 2018.10.23  cancel normalizing objective
//				fitnesses[i] += normObjValue;

				if(ObjValue<9999999){
					fitnesses[i] += ObjValue;
					if(ObjValue>result.getMaxFitnessIn50Instance()[0])
						result.getMaxFitnessIn50Instance()[0] = ObjValue;
				}else {
					result.getNumberBagindInGen()[0][Gen]=1;
				}
			}

			col++;

			//System.out.println("The value of replication is "+schedulingSet.getReplications()); //50
			// if the constraint is over 500, the objective is recorded as the largest one

			for (int k = 1; k < schedulingSet.getReplications().get(j); k++) {
				simulation.rerun();
				for (int i = 0; i < objectives.size(); i++) {
//					double normObjValue = simulation.objectiveValue(objectives.get(i))
//							/ schedulingSet.getObjectiveLowerBound(i, col);
//					fitnesses[i] += normObjValue;

					//fzhang 2018.10.23  cancel normalizing objective
					double ObjValue = simulation.objectiveValue(objectives.get(i));
					if(ObjValue<9999999){
						fitnesses[i] += ObjValue;
						if(ObjValue>result.getMaxFitnessIn50Instance()[k])
							result.getMaxFitnessIn50Instance()[k] = ObjValue;
					}else {
						result.getNumberBagindInGen()[k][Gen]=1;
					}

				}
				col++;
			}
			simulation.reset();
		}


//		for (int i = 0; i < fitnesses.length; i++) {
//			fitnesses[i] /= col;
//		}

		MultiObjectiveFitness f = (MultiObjectiveFitness) fitness;

		f.setObjectives(state, fitnesses);
		//
		if(Gen==99){
			double max=0;
			for (int i = 0; i < 50; i++) {
				if(result.getMaxFitnessIn50Instance()[i]>max) max = result.getMaxFitnessIn50Instance()[i];
			}
			for (int i = 0; i < 50; i++) {
				if(result.getMaxFitnessIn50Instance()[i]==0) result.getMaxFitnessIn50Instance()[i]=max;
			}
			for (int i = 0; i < 100; i++) {
			MultiObjectiveFitness fitness1 =(MultiObjectiveFitness)result.getGenerationalTestFitness(i);
			double fit = fitness1.getObjectives()[0];
				for (int j = 0; j < result.getNumberBagindInGen().length; j++) {
					if(result.getNumberBagindInGen()[j][i]==1){
						fit+=result.getMaxFitnessIn50Instance()[j];
					}
				}
				fit/=50;
				double[] fitnesses0 = new double[objectives.size()]; fitnesses0[0]=fit;
				fitness1.setObjectives(state,fitnesses0);
			}
		}
	}


	public OperationOption priorOperation(SequencingDecisionSituation sequencingDecisionSituation) {
		
		List<OperationOption> queue = sequencingDecisionSituation.getQueue();
		WorkCenter workCenter = sequencingDecisionSituation.getWorkCenter();
		SystemState systemState = sequencingDecisionSituation.getSystemState();


		//fzhang 2018.10.23  original one
		//============================start==============================
		OperationOption priorOp = queue.get(0);
		priorOp.setPriority(priority(priorOp, workCenter, systemState));

		//FEIGE for tugboat ,both work center and set
//		OperationOption priorOp = queue.get(0);
//		List<WorkCenter> workCentersetPrio = priorOp.getWorkCenterSet();
//		priorOp.setPriority(priority(priorOp, workCenter ,workCentersetPrio, systemState));

		//FEIGE for tugboatset for rule 2
//		OperationOption priorOp = queue.get(0);
//		List<WorkCenter> workCentersetPrio = priorOp.getWorkCenterSet();
//		priorOp.setPriority(priority(priorOp, workCentersetPrio, systemState));

		//LIUFEIGE for tugboat
		for (int i = 1; i < queue.size(); i++) {
			OperationOption op = queue.get(i);
			//for jsp
			op.setPriority(priority(op, workCenter, systemState));
			//this part can be optimized, even in the A1 and A3,when sequencing ,it can calculate the value through the workcenter set
			//so that it need more features, including the features related to Workcenterset(it can for A2)

			//LIUFEIGE for tugboat  for rule 2
//			List<WorkCenter> workCenterset = op.getWorkCenterSet();
//			op.setPriority(priority(op, workCenterset, systemState));

			if (op.priorTo(priorOp))
				priorOp = op;
		}

		return priorOp;
		}
		//==============================end==============================
	
	//fzhang 2018.10.10  get the seed value
	public long getSeed(final Parameter base) {
		 Parameter p;
			// Get the job seed.
			p = new Parameter("seed").push(""+0);
	        return jobSeed = state.parameters.getLongWithDefault(p, null, 0);
	}
	
	// about routing rule: use priority to decide
	public OperationOption nextOperationOption(RoutingDecisionSituation routingDecisionSituation) {

		List<OperationOption> queue = routingDecisionSituation.getQueue();
		SystemState systemState = routingDecisionSituation.getSystemState();
    //================original=================
	//==================start==================
	OperationOption bestOperationOption = queue.get(0);
	double	priority = priority(bestOperationOption, bestOperationOption.getWorkCenter(), systemState);
	bestOperationOption
			.setPriority(priority);
	// loop all the options, save the best one as "selected" one
	for (int i = 1; i < queue.size(); i++) {
		OperationOption operationOption = queue.get(i);
		operationOption.setPriority(priority(operationOption, operationOption.getWorkCenter(), systemState));

		if (operationOption.priorTo(bestOperationOption)) {
			bestOperationOption = operationOption;
		}
	}
	return bestOperationOption;// this links which machine will be chosen.
	}

	//LIUFEIGE according to the tree, sort and choose machine set
	public OperationOption nextOperationOptionDmachineOnce(RoutingDecisionSituation routingDecisionSituation) {

		List<OperationOption> queue = routingDecisionSituation.getQueue();

		OperationOption bestOperationOption = queue.get(0).clone();

//		List<WorkCenter> queueM = routingDecisionSituation.getSystemState().cloneWorkCenterss3(bestOperationOption);
		List<WorkCenter> queueM = routingDecisionSituation.getSystemState().cloneWorkCenterss33(bestOperationOption);
		List<OperationOption> queueMwithJob = new ArrayList<>();
		SystemState systemState = routingDecisionSituation.getSystemState();
		//================original=================
		//==================start==================
		List<WorkCenter> workCenterSet = new ArrayList<>();
		bestOperationOption.setWorkCenterSet(workCenterSet);

		for (int i = 0; i < queueM.size(); i++) {
			WorkCenter machine = queueM.get(i);
			OperationOption operationOption = bestOperationOption.clone();
			operationOption.setWorkCenter(machine);
			operationOption.setPriority(priority(operationOption,machine,systemState));
			queueMwithJob.add(operationOption);
		}
		//sort according to priority
		queueMwithJob.sort(Comparator.comparing(OperationOption::getPriority));
//		int i=0;
		int i = queueMwithJob.size()-1;
		while(bestOperationOption.getJob().numberHorseTug(workCenterSet)!=0){
			//if, according
			//A Bi-objective green tugboat scheduling problem with the tidal port time windows
/*			if(bestOperationOption.getJob().MinNumberHorseTug(workCenterSet)==0){
				if(queueMwithJob.get(i).getWorkCenter().getHorse_C0_C1_VTi()[0]>=
						bestOperationOption.getUpperHorsepower()){
					bestOperationOption.getWorkCenterSet().add(queueMwithJob.get(i).getWorkCenter());
				}
			}else if(bestOperationOption.getJob().MinNumberHorseTug(workCenterSet)>0){
				bestOperationOption.getWorkCenterSet().add(queueMwithJob.get(i).getWorkCenter());
			}else {
				System.out.println("error: machine set can't in constrain");
			}
			i++;*/
			//plan 33,
			bestOperationOption.getWorkCenterSet().add(queueMwithJob.get(i).getWorkCenter());
//			i++;
			i--;
		}

		return bestOperationOption;// this links which machine will be chosen.
	}

	//LIUFEIGE tree for machineset, get all possible machine sets and choose one of them
	public OperationOption nextOperationOptionDmachineset(RoutingDecisionSituation routingDecisionSituation) {

		List<OperationOption> queue = routingDecisionSituation.getQueue();
		OperationOption bestOperationOption = queue.get(0).clone();

//		List<WorkCenter> queueM = routingDecisionSituation.getSystemState().cloneWorkCenterss(bestOperationOption);
		List<WorkCenter> queueM = routingDecisionSituation.getSystemState().cloneWorkCenterss33(bestOperationOption);

		List<OperationOption> queueMwithJob = new ArrayList<>();
//		SystemState systemState = routingDecisionSituation.getSystemState().clone();
		//================original=================
		//==================start==================
		List<WorkCenter> bestworkCenterSet = new ArrayList<>();
		List<List<WorkCenter>> machineSetList = new LinkedList<>();
		machineSetList = findAllCombinations(queueM,bestOperationOption.getNumNeedTug());
//		for (int i = 0; i < machineSetList.size(); i++) {
//			if(bestOperationOption.getJob().numberHorseTug(machineSetList.get(i))!=0){
//				machineSetList.remove(i);
//			}
//		}
		bestworkCenterSet = machineSetList.get(0);
		bestOperationOption.setWorkCenterSet(bestworkCenterSet);
		bestOperationOption.setPriority(priority(bestOperationOption,machineSetList.get(0),
				routingDecisionSituation.getSystemState()));
		for (int i = 0; i < machineSetList.size(); i++) {
			OperationOption operationOption = bestOperationOption.clone();
			operationOption.setWorkCenterSet(machineSetList.get(i));
			operationOption.setPriority(priority(operationOption,machineSetList.get(i),
					routingDecisionSituation.getSystemState()));
			if (operationOption.priorTo(bestOperationOption)) {
				bestworkCenterSet = machineSetList.get(i);
				bestOperationOption.setWorkCenterSet(bestworkCenterSet);
				bestOperationOption.setPriority(operationOption.getPriority());
			}
		}
		return bestOperationOption;// this links which machine will be chosen.
	}
	public static List<List<WorkCenter>> findAllCombinations(
			List<WorkCenter> workCenters,
			int tugboatCount) {

		List<List<WorkCenter>> result = new ArrayList<>();

		// ???????????§³????????workcenters
		List<WorkCenter> eligibleCenters = workCenters;

		// ??????????workcenters????????§Ò?
		if (eligibleCenters.size() < tugboatCount) {
			return result;
		}

		// ??????????????????
		backtrack(eligibleCenters, tugboatCount, 0, new ArrayList<>(), result);

		return result;
	}

	// ?????????
	private static void backtrack(List<WorkCenter> workCenters,
								  int count,
								  int start,
								  List<WorkCenter> current,
								  List<List<WorkCenter>> result) {

		// ???????????????????????????
		if (current.size() == count) {
			result.add(new ArrayList<>(current));
			return;
		}

		// ???????????
		for (int i = start; i < workCenters.size(); i++) {
			current.add(workCenters.get(i));
			backtrack(workCenters, count, i + 1, current, result);
			current.remove(current.size() - 1);
		}
	}


	//LIUFEIGE choose a machine and update the terminal value, then continue choosing a machine
	public OperationOption nextOperationOptionDmachine(RoutingDecisionSituation routingDecisionSituation) {

		List<OperationOption> queue = routingDecisionSituation.getQueue();
		OperationOption bestOperationOption = queue.get(0).clone();

//		List<WorkCenter> queueM = routingDecisionSituation.getSystemState().cloneWorkCenterss(bestOperationOption);
		List<WorkCenter> queueM = routingDecisionSituation.getSystemState().cloneWorkCenterss33(bestOperationOption);

		SystemState systemState = routingDecisionSituation.getSystemState(); //change no clone
		//================original=================
		//==================start==================
		List<WorkCenter> workCenterSet = new ArrayList<>();
		bestOperationOption.setWorkCenterSet(workCenterSet);

		while(bestOperationOption.getJob().numberHorseTug(workCenterSet)!=0){
			LinkedList<WorkCenter> bestMachineList = new LinkedList<>(); //have equal priority value machine
			WorkCenter bestmachine = queueM.get(0);
			bestOperationOption.setWorkCenter(bestmachine);
			double	priority = priority(bestOperationOption, bestmachine, systemState);
			bestOperationOption.setPriority(priority);
			for (int i = 0; i < queueM.size(); i++) {
				WorkCenter machine = queueM.get(i);
				OperationOption operationOption = bestOperationOption.clone();
				operationOption.setWorkCenter(machine);
				operationOption.setPriority(priority(operationOption,machine,systemState));
				if (operationOption.priorTo(bestOperationOption)) {
					bestmachine = machine;
					bestOperationOption.setPriority(operationOption.getPriority());
				}
			}
			bestOperationOption.getWorkCenterSet().add(bestmachine);
			queueM.remove(bestmachine);
		}

		return bestOperationOption;//this links which machine will
		// be chosen.
	}

	//LIUFEIGE individual is a special tree,choose machine according to its subtree
	public OperationOption nextOperationOptionDmachineTree(RoutingDecisionSituation routingDecisionSituation){


		//================original=================
		//==================start==================
		List<OperationOption> queue = routingDecisionSituation.getQueue();
		OperationOption bestOperationOption = queue.get(0).clone();

		List<WorkCenter> queueM = routingDecisionSituation.getSystemState().cloneWorkCenterss(bestOperationOption);
		SystemState systemState = routingDecisionSituation.getSystemState(); //change no clone
		List<WorkCenter> workCenterSet = new ArrayList<>();
		bestOperationOption.setWorkCenterSet(workCenterSet);
		int machineNum=0;

		//---------------plan 2----------------------
		LinkedList<OperationOption> optionLinkedList = new LinkedList<>();

		for (int i = 0; i < queueM.size(); i++) {
			WorkCenter machine = queueM.get(i);
			OperationOption operationOption = bestOperationOption.clone();
			operationOption.setWorkCenter(machine);
			double[] priority = priorityMulTree(bestOperationOption, machine, systemState);
			operationOption.setPriorityMultiTree(priority);
			optionLinkedList.add(operationOption);
		}
		int machineN=0;
		while(bestOperationOption.getJob().numberHorseTug(workCenterSet)!=0){
			OperationOption bestOper= optionLinkedList.get(0);
			for (int i = 1; i < optionLinkedList.size(); i++) {
				if(machineN>=bestOper.getPriorityMultiTree().length){
					machineN=bestOper.getPriorityMultiTree().length;
				}
				if(bestOper.getPriorityMultiTree()[machineN]>
						optionLinkedList.get(i).getPriorityMultiTree()[machineN]){
					bestOperationOption=optionLinkedList.get(i);
				}
			}
			bestOperationOption.getWorkCenterSet().add(bestOper.getWorkCenter());
			optionLinkedList.remove(bestOper);
			machineN++;
		}
		//LIUFEIGE
//		System.out.print(bestOperationOption.getOperation());
//		System.out.println(bestOperationOption.getWorkCenterSet());


		//--------------plan 1 ------------------------------//
//		while(bestOperationOption.getJob().numberHorseTug(workCenterSet)!=0){
//			WorkCenter bestmachine = queueM.get(0);
//			bestOperationOption.setWorkCenter(bestmachine);
////			double	priority = priority(bestOperationOption, bestmachine, systemState);
//			double[] priority = priorityMulTree(bestOperationOption, bestmachine, systemState);
//			bestOperationOption.setPriority(priority[machineNum]);
//
//			for (int i = 0; i < queueM.size(); i++) {
//				WorkCenter machine = queueM.get(i);
//				OperationOption operationOption = bestOperationOption.clone();
//				operationOption.setWorkCenter(machine);
//				operationOption.setPriority(priorityMulTree(operationOption,machine,systemState)[machineNum]);
//				if (operationOption.priorTo(bestOperationOption)) {
//					bestmachine = machine;
//				}
//			}
//			bestOperationOption.getWorkCenterSet().add(bestmachine);
//			machineNum++;
//			queueM.remove(bestmachine);
//		}

		return bestOperationOption;//this links which machine will be chosen.
	}


	//============end============================================
	
		//===========================================AAAI2019========================================
		//fzhang 6.8.2018  incorporating knowledge (workload) into dispatching rule---start from here
		//=========================================start============================================
/*		double totalProcessTimeOfWorkCenterInSystem = 0;

		for (WorkCenter w : systemState.getWorkCenters()) {
			totalProcessTimeOfWorkCenterInSystem += w.getWorkInQueue();
		}

		OperationOption bestOperationOption = queue.get(0);

		//fzhang 4.6.2018 set the priority value related to workload/workloadInSystem
		//setPriority(): this method is set a double value as priority value to OperationOption
		double bestWorkLoadRatio = 0;
		if(bestOperationOption.getWorkCenter().getWorkInQueue() != 0) {
		     bestWorkLoadRatio = bestOperationOption.getWorkCenter().getWorkInQueue()
					/ totalProcessTimeOfWorkCenterInSystem;
		     bestOperationOption
				.setPriority(1/(1-bestWorkLoadRatio)*priority(bestOperationOption, bestOperationOption.getWorkCenter(), systemState));
		}
		else
			bestOperationOption
			.setPriority(priority(bestOperationOption, bestOperationOption.getWorkCenter(), systemState));
		
		
		// loop all the options, save the best one as "selected" one

		for (int i = 1; i < queue.size(); i++) {
			OperationOption operationOption = queue.get(i);
			double workLoadRatio = 0;
			
			if(operationOption.getWorkCenter().getWorkInQueue() != 0) {
				workLoadRatio = operationOption.getWorkCenter().getWorkInQueue()
						/ totalProcessTimeOfWorkCenterInSystem;
					operationOption.setPriority(1/(1-workLoadRatio) * priority(operationOption, operationOption.getWorkCenter(), systemState));
			}
			else
			  operationOption.setPriority(priority(operationOption, operationOption.getWorkCenter(), systemState));
			
			if (operationOption.priorTo(bestOperationOption)) {
				bestOperationOption = operationOption;
			}
		}

		return bestOperationOption;// this links which machine will be chosen.
}*/
//=========================================================end================================================
	public abstract double priority(OperationOption op, WorkCenter workCenter, SystemState systemState);

	//LIUFEIGE
	public abstract double[] priorityMulTree(OperationOption op, WorkCenter workCenter, SystemState systemState);
	public abstract double priority(OperationOption op, List<WorkCenter> workCenterset, SystemState systemState);

//	public abstract double priority(OperationOption op,WorkCenter workCenter,List<WorkCenter> workCenterset, SystemState systemState);

}

