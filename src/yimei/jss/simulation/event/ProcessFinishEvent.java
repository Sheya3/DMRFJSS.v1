package yimei.jss.simulation.event;

import yimei.jss.jobshop.Process;
import yimei.jss.jobshop.*;
import yimei.jss.simulation.RoutingDecisionSituation;
import yimei.jss.simulation.SequencingDecisionSituation;
import yimei.jss.simulation.Simulation;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by yimei on 22/09/16.
 */
public class ProcessFinishEvent extends AbstractEvent {

    private Process process;
    //fzhang 29.8.2018 in order to record the completion time of jobs
    protected long jobSeed;

    public ProcessFinishEvent(double time, Process process) {
        super(time);
        this.process = process;
    }

    public ProcessFinishEvent(Process process) {
        this(process.getFinishTime(), process);
    }

    @Override
//    public void trigger(Simulation simulation) {
//        WorkCenter workCenter = process.getWorkCenter();
//        process.getOperationOption().getJob().addProcessFinishEvent(this);
//
//        if (!workCenter.getQueue().isEmpty()) {
//            SequencingDecisionSituation sequencingDecisionSituation =
//                    new SequencingDecisionSituation(workCenter.getQueue(), workCenter,
//                            simulation.getSystemState());
//
//            //System.out.println("=======================================sequencing==========================================");
//            OperationOption dispatchedOp =
//                    simulation.getSequencingRule().priorOperation(sequencingDecisionSituation);
//
//            workCenter.removeFromQueue(dispatchedOp);
//
//            //must wait for machine to be ready
//            double processStartTime = Math.max(workCenter.getReadyTime(), time);
//
//            Process nextP = new Process(workCenter, process.getMachineId(),
//                    dispatchedOp, processStartTime);
//            simulation.addEvent(new ProcessStartEvent(nextP));
//        }
//
//        OperationOption nextOp = process.getOperationOption().getNext(simulation.getSystemState(),
//                simulation.getRoutingRule());
//
//        if (nextOp == null) {
//            workCenter.setMachinePortArea(0,process.getOperationOption().getObjectPortArea());
//            Job job = process.getOperationOption().getJob();
//            job.setCompletionTime(process.getFinishTime());
//            simulation.completeJob(job);
//
//            //fzhang 29.8.2018 when a job is finished, record the completion time of this job. So, we have 5000 jobs, 5000 information
//            //too much i+nformation. This is suitable in test process and set the job number a relative smaller number.
//            /*System.out.println("Job ID: "+job.getId());
//            System.out.println("Number of Operations: "+job.getOperations().size());
//            System.out.println("Arrival Time: "+job.getArrivalTime());
//            System.out.println("Completion Time: "+job.getCompletionTime()); //getCompletionTime is a time point.  flowtime = completionTime - arrivalTime
//            System.out.println("Total Processing Time: "+job.getTotalProcTime());
//            System.out.println("Average Processing Time: "+job.getAvgProcTime()); //getTotalProcTime/numOfOperations
//            System.out.println("Flow Time: "+job.flowTime());
//            System.out.println("Waiting Time: "+job.getWaitingTime());*/
//        }
//        else {
//            simulation.addEvent(new OperationVisitEvent(time, nextOp));
//        }
//    }

    //LIUFEIGE
    public void trigger(Simulation simulation) {
        List<WorkCenter> workCenterSet = process.getWorkCenterSet();
        process.getOperationOption().getJob().addProcessFinishEvent(this);

        //method 2
        //all machine?
        //yes, all machines of the finishing event can be free
        //question: choose a event or more events in the queue ? more events
        //question: are these event could be same events? maybe the seq rule will choose a same event from different
        //machines' queue, so when these are same, we just add an event into the simulation queue
        LinkedList<OperationOption> SeqChoosedEvent = new LinkedList<>();
        for (int i = 0; i < workCenterSet.size(); i++) {
            if (!workCenterSet.get(i).getQueue().isEmpty()) {
                SequencingDecisionSituation sequencingDecisionSituationi =
                        new SequencingDecisionSituation(workCenterSet.get(i).getQueue(), workCenterSet.get(i),
                                simulation.getSystemState());
                OperationOption dispatchedOp =
                        simulation.getSequencingRule().priorOperation(sequencingDecisionSituationi);
//                for (int ii = 0; ii < dispatchedOp.getWorkCenterSet().size(); ii++) {
//                    dispatchedOp.getWorkCenterSet().get(ii).removeFromQueue(dispatchedOp);
//                }
                SeqChoosedEvent.add(dispatchedOp);
            }
        }
        //after all operations in each tug's queue with highest priority
        SeqChoosedEvent.sort(Comparator.comparing(OperationOption::getPriority));
        for (int i = 0; i < SeqChoosedEvent.size(); i++) {
            for (int j = SeqChoosedEvent.size()-1; j >i; j--) {
                if(SeqChoosedEvent.get(i)==SeqChoosedEvent.get(j)) SeqChoosedEvent.remove(j);
            }
            //if op1 and op2 have a same machine
            for (int j = SeqChoosedEvent.size()-1; j >i; j--) {
                OperationOption op1 = SeqChoosedEvent.get(i);
                OperationOption op2 = SeqChoosedEvent.get(j);
                int sign_have_sameMachine=0;
                for (int k = 0; k < op1.getWorkCenterSet().size(); k++) {
                    for (int l = 0; l < op2.getWorkCenterSet().size(); l++) {
                        if(op1.getWorkCenterSet().get(k)==op2.getWorkCenterSet().get(l)){
                            sign_have_sameMachine=1;
                            break;
                        }
                    }
                }
                if(sign_have_sameMachine==1){
                    SeqChoosedEvent.remove(j);
                }
            }
        }
        //SeqEvent -->ProcessStartEvent
        for (int i = 0; i < SeqChoosedEvent.size(); i++) {
            OperationOption op = SeqChoosedEvent.get(i);
            //if machines of op are all idle, turn to process start event, event removed from the queue of each machine
            //else ? the op can be removed from the queue of the machines
            int sign_MachinesAreIdle =1;
            for (int j = 0; j < op.getWorkCenterSet().size(); j++) {
                if(op.getWorkCenterSet().get(j).getCurrentProcessOnMachine()!=null){
                    sign_MachinesAreIdle=0;
                }
            }
            if(sign_MachinesAreIdle==1){
                //remove
                for (int j = 0; j < op.getWorkCenterSet().size(); j++) {
                    op.getWorkCenterSet().get(j).removeFromQueue(op);
                }
                //turn to PS
                double processStartTime = op.getReadyTime();
                Operation processNext = op.getOperation();
                List<WorkCenter> workCenterSetNext = processNext.getOperationOption().getWorkCenterSet();
                Process nextP = new Process(workCenterSetNext, 0,
                        op,simulation.getSystemState(), processStartTime);
                simulation.addEvent(new ProcessStartEvent(nextP));
            }else {
                int aa = 0;
            }
        }

        int a=SeqChoosedEvent.size();
        //method 1
/*        WorkCenter choosedMachine = workCenterSet.get(0);
        for (int i = 0; i < workCenterSet.size(); i++) {
            if(workCenterSet.get(i).getQueue().size()>choosedMachine.getQueue().size())
                choosedMachine = workCenterSet.get(i);
        }

        if (!choosedMachine.getQueue().isEmpty()) {
            SequencingDecisionSituation sequencingDecisionSituation =
                    new SequencingDecisionSituation(choosedMachine.getQueue(), choosedMachine,
                            simulation.getSystemState());
            //System.out.println("=======================================sequencing==========================================");

            OperationOption dispatchedOp =
                    simulation.getSequencingRule().priorOperation(sequencingDecisionSituation);

            for (int i = 0; i < dispatchedOp.getWorkCenterSet().size(); i++) {
                dispatchedOp.getWorkCenterSet().get(i).removeFromQueue(dispatchedOp);
            }

            //must wait for machine to be ready
//            int maxmahineArea = workCenterSet.get(0).getMahinePosition().get(0);
//            int jobarea = process.getOperationOption().getObjectPortArea();
//            double maxmachineTime=workCenterSet.get(0).getReadyTime()+
//                    simulation.getSystemState().getDSTS()[maxmahineArea-1][jobarea-1]/13;
//            for (int i = 0; i < workCenterSet.size(); i++) {
//                int mahineArea = workCenterSet.get(i).getMahinePosition().get(0);
//                double machineTime=workCenterSet.get(i).getReadyTime()+
//                        simulation.getSystemState().getDSTS()[maxmahineArea-1][jobarea-1]/13;
//                if(machineTime>maxmachineTime){
//                    maxmahineArea = mahineArea;
//                    maxmachineTime = machineTime;
//                }
//            }

            // it is the ship arrival time ,the ship just have a process
            double processStartTime = dispatchedOp.getReadyTime();
//            double processStartTime = Math.max(maxmachineTime, time); //finishi step1
            Operation processNext = dispatchedOp.getOperation();
            List<WorkCenter> workCenterSetNext = processNext.getOperationOption().getWorkCenterSet();
            Process nextP = new Process(workCenterSetNext, 0,
                    dispatchedOp,simulation.getSystemState(), processStartTime);
            simulation.addEvent(new ProcessStartEvent(nextP));
        }*/

       /* for (int i1 = 0; i1 < workCenterSet.size(); i1++) {
//            WorkCenter workCenter = workCenterSet.get(i1);
//            if (!workCenter.getQueue().isEmpty()) {
//                SequencingDecisionSituation sequencingDecisionSituation =
//                        new SequencingDecisionSituation(workCenter.getQueue(), workCenter,
//                                simulation.getSystemState());
//
//                //System.out.println("=======================================sequencing==========================================");
//                OperationOption dispatchedOp =
//                        simulation.getSequencingRule().priorOperation(sequencingDecisionSituation);
//
//                for (int i = 0; i < dispatchedOp.getWorkCenterSet().size(); i++) {
//                    dispatchedOp.getWorkCenterSet().get(i).removeFromQueue(dispatchedOp);
//                }
//
//                //must wait for machine to be ready
//                int maxmahineArea = workCenterSet.get(0).getMahinePosition().get(0);
//                int jobarea = process.getOperationOption().getObjectPortArea();
//                double maxmachineTime=workCenterSet.get(0).getReadyTime()+
//                        simulation.getSystemState().getDSTS()[maxmahineArea-1][jobarea-1]/13;
//                for (int i = 0; i < workCenterSet.size(); i++) {
//                    int mahineArea = workCenterSet.get(i).getMahinePosition().get(0);
//                    double machineTime=workCenterSet.get(i).getReadyTime()+
//                            simulation.getSystemState().getDSTS()[maxmahineArea-1][jobarea-1]/13;
//                    if(machineTime>maxmachineTime){
//                        maxmahineArea = mahineArea;
//                        maxmachineTime = machineTime;
//                    }
//                }
//
//                double processStartTime = Math.max(maxmachineTime, time); //finishi step1
//
//                Process nextP = new Process(workCenterSet, process.getMachineId(),
//                        dispatchedOp,simulation.getSystemState(), processStartTime);
//                simulation.addEvent(new ProcessStartEvent(nextP));
//            }
        }*/

        OperationOption nextOp = process.getOperationOption().getNext(simulation.getSystemState(),
                simulation.getRoutingRule());

        if (nextOp == null) {
//            for (int i = 0; i < workCenterSet.size(); i++) {
//                workCenterSet.get(i).setMachinePortArea(0,process.getOperationOption().getObjectPortArea());
//            }
            Job job = process.getOperationOption().getJob();
            job.setCompletionTime(process.getFinishTime());
            simulation.completeJob(job);
            for (int i = 0; i < workCenterSet.size(); i++) {
                workCenterSet.get(i).getQueueCompleteJob().add(job);
            }
            //fzhang 29.8.2018 when a job is finished, record the completion time of this job. So, we have 5000 jobs, 5000 information
            //too much i+nformation. This is suitable in test process and set the job number a relative smaller number.
            /*System.out.println("Job ID: "+job.getId());
            System.out.println("Number of Operations: "+job.getOperations().size());
            System.out.println("Arrival Time: "+job.getArrivalTime());
            System.out.println("Completion Time: "+job.getCompletionTime()); //getCompletionTime is a time point.  flowtime = completionTime - arrivalTime
            System.out.println("Total Processing Time: "+job.getTotalProcTime());
            System.out.println("Average Processing Time: "+job.getAvgProcTime()); //getTotalProcTime/numOfOperations
            System.out.println("Flow Time: "+job.flowTime());
            System.out.println("Waiting Time: "+job.getWaitingTime());*/
        }
        else {
            simulation.addEvent(new OperationVisitEvent(time, nextOp));
        }

    }

	// modified by fzhang 26.4.2018 write bad run times to *.csv
/* 	public void WriteCompletionTime(EvolutionState state, final Parameter base) {
 		Parameter p;
 		// Get the job seed.
 		p = new Parameter("seed").push("" + 0);
 		jobSeed = state.parameters.getLongWithDefault(p, null, 0);
 		File completiontime = new File("job." + jobSeed + ".BadRun.csv");

 		try {
 			BufferedWriter writer = new BufferedWriter(new FileWriter(completiontime));
 			writer.write("jobID,arrivaltime,finishtime,completiontime");
 			writer.newLine();
 			  
 			writer.close();
 		} catch (IOException e) {
 			e.printStackTrace();
 		}
 	}*/
 	
    @Override
    public void addSequencingDecisionSituation(Simulation simulation,
                                     List<SequencingDecisionSituation> situations,
                                     int minQueueLength) {
        WorkCenter workCenter = process.getWorkCenter();
        process.getOperationOption().getJob().addProcessFinishEvent(this);

        if (!workCenter.getQueue().isEmpty()) {
            SequencingDecisionSituation sequencingDecisionSituation =
                    new SequencingDecisionSituation(workCenter.getQueue(), workCenter,
                            simulation.getSystemState());

            if (workCenter.getQueue().size() >= minQueueLength) { //when set operation with different processing time, the queue is hard to >= minQueueLength, an error happen here
                situations.add(sequencingDecisionSituation.clone());
            }

            OperationOption dispatchedOp =
                    simulation.getSequencingRule().priorOperation(sequencingDecisionSituation);

            workCenter.removeFromQueue(dispatchedOp);

            //must wait for machine to be ready
            double processStartTime = Math.max(workCenter.getReadyTime(), time);

            Process nextP = new Process(workCenter, process.getMachineId(),
                    dispatchedOp, processStartTime);
            simulation.addEvent(new ProcessStartEvent(nextP));
        }

        OperationOption nextOp = process.getOperationOption().getNext(simulation.getSystemState(),
                simulation.getRoutingRule());

        if (nextOp == null) {
            Job job = process.getOperationOption().getJob();
            job.setCompletionTime(process.getFinishTime());
            simulation.completeJob(job);
        }
        else {
            simulation.addEvent(new OperationVisitEvent(time, nextOp));
        }
    }

    @Override
    public void addRoutingDecisionSituation(Simulation simulation,
                                               List<RoutingDecisionSituation> situations,
                                               int minOptions) {
        WorkCenter workCenter = process.getWorkCenter();
        process.getOperationOption().getJob().addProcessFinishEvent(this);

        if (!workCenter.getQueue().isEmpty()) {
            SequencingDecisionSituation sequencingDecisionSituation =
                    new SequencingDecisionSituation(workCenter.getQueue(), workCenter,
                            simulation.getSystemState());

            OperationOption dispatchedOp =
                    simulation.getSequencingRule().priorOperation(sequencingDecisionSituation);

            workCenter.removeFromQueue(dispatchedOp);

            //must wait for machine to be ready
            double processStartTime = Math.max(workCenter.getReadyTime(), time);

            Process nextP = new Process(workCenter, process.getMachineId(),
                    dispatchedOp, processStartTime);
            simulation.addEvent(new ProcessStartEvent(nextP));
        }

        if (process.getOperationOption().getOperation().getNext() != null) {
            if (process.getOperationOption().getOperation().getNext().getOperationOptions().size()
                    >= minOptions) {
                Operation o = process.getOperationOption().getOperation();
                RoutingDecisionSituation r = o.getNext().routingDecisionSituation(simulation.getSystemState());
                situations.add(r.clone());
            }
        }

        OperationOption nextOp = process.getOperationOption().getNext(simulation.getSystemState(),
                simulation.getRoutingRule());

        if (nextOp == null) {
            Job job = process.getOperationOption().getJob();
            job.setCompletionTime(process.getFinishTime());
            simulation.completeJob(job);
        }
        else {
            simulation.addEvent(new OperationVisitEvent(time, nextOp));
        }
    }


//    @Override
//    public String toString() {
//        return String.format("%.1f: job %d op %d finished on work center %d.\n",
//                time,
//                process.getOperationOption().getJob().getId(),
//                process.getOperationOption().getOperation().getId(),
//                process.getWorkCenter().getId());
//    }

    @Override
    public String toString() {
        return String.format("%.1f: job %d op %d finished on work center %d.\n",
                time,
                process.getOperationOption().getJob().getId(),
                process.getOperationOption().getOperation().getId(),
                process.getWorkCenterSet().size());
    }

    @Override
    public int compareTo(AbstractEvent other) {
        if (time < other.time)
            return -1;

        if (time > other.time)
            return 1;

        if (other instanceof ProcessFinishEvent) {
            ProcessFinishEvent otherPFE = (ProcessFinishEvent)other;
            //LIUFEIGE no finish
//            if (process.getWorkCenter().getId() < otherPFE.process.getWorkCenter().getId())
//                return -1;
//
//            if (process.getWorkCenter().getId() > otherPFE.process.getWorkCenter().getId())
//            return 1;
        }

        return 1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProcessFinishEvent that = (ProcessFinishEvent) o;

        return process != null ? process.equals(that.process) : that.process == null;
    }

    @Override
    public int hashCode() {
        return process != null ? process.hashCode() : 0;
    }


    public Process getProcess() {
        return process;
    }
}
