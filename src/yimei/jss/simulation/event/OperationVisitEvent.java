package yimei.jss.simulation.event;

import yimei.jss.jobshop.Machine;
import yimei.jss.jobshop.OperationOption;
import yimei.jss.jobshop.Process;
import yimei.jss.jobshop.WorkCenter;
import yimei.jss.simulation.RoutingDecisionSituation;
import yimei.jss.simulation.SequencingDecisionSituation;
import yimei.jss.simulation.Simulation;

import java.util.List;

/**
 * Created by YiMei on 25/09/16.
 */
public class OperationVisitEvent extends AbstractEvent {

    private OperationOption operationOption;

    public OperationVisitEvent(double time, OperationOption operationOption) {
        super(time);
        this.operationOption = operationOption;
    }

    public OperationVisitEvent(OperationOption operation) {
        this(operation.getReadyTime(), operation);
    }

    @Override
    //operationvisit -->process
    public void trigger(Simulation simulation) {
        operationOption.setReadyTime(time); //it's ship or operation arrival time?
//        //for jss
//        WorkCenter workCenter = operationOption.getWorkCenter();
//        Machine earliestMachine = workCenter.earliestReadyMachine();
//        Process p = new Process(workCenter, earliestMachine.getId(), operationOption, time);
//        if (earliestMachine.getReadyTime() > time || !simulation.canAddToQueue(p)) {
//            workCenter.addToQueue(operationOption); //oper wait for
//        }
//        else {
//            simulation.addEvent(new ProcessStartEvent(p)); // oper not wait
//        }


        //LIUFEIGE for tugboat
        //compare tugboat release time of last process in stage3 with
        // ship arrival time - pt in stage 1
//        if(operationOption.getJob().getId()==1598) {
//            int a=0;
//        }

        List<WorkCenter> workCenterSet = operationOption.getWorkCenterSet();
        //
        int sign=0;
        // 1 more stronger constraints
        for (WorkCenter workCenter : workCenterSet) {
            Machine earliestMachine = workCenter.earliestReadyMachine();
            double machineRTInS1 = earliestMachine.getReadyTime();
            double s1 = simulation.getSystemState().getDSTS()[workCenter.getMachinePortArea().get(0) - 1] //s1
                    [operationOption.getObjectPortArea() - 1] / 13;
//            Process p = new Process(workCenterSet, earliestMachine.getId(),
//                    operationOption,simulation.getSystemState(), time);
            //the decision point move forward
            machineRTInS1 += s1;
//            if (earliestMachine.getReadyTime() > time) {
            if (machineRTInS1 > time
//                  || !simulation.canAddToQueue(p)
            ) {
                sign = 1;
//                workCenterSet.get(i).addToQueue(operationOption);
            }
        }
        // 2 just consider the release time in last task
//        for (WorkCenter workCenter : workCenterSet) {
//            Machine earliestMachine = workCenter.earliestReadyMachine();
//            double machineRTInS1 = earliestMachine.getReadyTime();
////            double s1 = simulation.getSystemState().getDSTS()[workCenter.getMachinePortArea().get(0) - 1] //s1
////                    [operationOption.getObjectPortArea() - 1] / 13;
//////            Process p = new Process(workCenterSet, earliestMachine.getId(),
//////                    operationOption,simulation.getSystemState(), time);
////            //the decision point move forward
////            machineRTInS1 += s1;
////            if (earliestMachine.getReadyTime() > time) {
//            if (machineRTInS1 > time
////                  || !simulation.canAddToQueue(p)
//            ) {
//                sign = 1;
////                workCenterSet.get(i).addToQueue(operationOption);
//            }
//        }

        //if all machine ready, start; part of machine ready, in queue;
        if (sign==0) {
            //modified by feige liu
            //
            Process ptug = new Process(operationOption.getWorkCenterSet(),0,operationOption,
                    simulation.getSystemState(),time);
            for (int i = 0; i < ptug.getPtForMachineInS1S2S3().size(); i++) {
                double a = ptug.getWorkCenterSet().get(i).getmachineTimeC1().get(0);
                double b = ptug.getPtForMachineInS1S2S3().get(i)[0];
                ptug.getWorkCenterSet().get(i).setmachineTimeC1(0,a+b);
                double a1 = ptug.getWorkCenterSet().get(i).getmachineTimeC0().get(0);
                double b1 = ptug.getPtForMachineInS1S2S3().get(i)[1];
                ptug.getWorkCenterSet().get(i).setmachineTimeC0(0,a1+b1);
            }
            ProcessStartEvent eventST=new ProcessStartEvent(ptug);
            simulation.addEvent(eventST); //the address of ptug is not the address of this job's operationOption
            for (int i = 0; i < ptug.getWorkCenterSet().size(); i++) {
                ptug.getWorkCenterSet().get(i).setCurrentProcessOnMachine(eventST);
            }
        }
        else {
            for (int j = 0; j < workCenterSet.size(); j++) {
//                    workCenterSet.get(j).addToQueue(operationOption);
                workCenterSet.get(j).addToQueueTug(simulation.getSystemState(),operationOption);
                }
        }

    }

    //TUGBOAT if just one machine is in busy time, and other mahcines (1 or 2) are idle, turn to processStartEvent
/*    public void trigger(Simulation simulation) {
        operationOption.setReadyTime(time); //it's ship or operation arrival time?
//        //for jss
//        WorkCenter workCenter = operationOption.getWorkCenter();
//        Machine earliestMachine = workCenter.earliestReadyMachine();
//        Process p = new Process(workCenter, earliestMachine.getId(), operationOption, time);
//        if (earliestMachine.getReadyTime() > time || !simulation.canAddToQueue(p)) {
//            workCenter.addToQueue(operationOption); //oper wait for
//        }
//        else {
//            simulation.addEvent(new ProcessStartEvent(p)); // oper not wait
//        }


        //LIUFEIGE for tugboat
        //compare tugboat release time of last process in stage3 with
        // ship arrival time - pt in stage 1

        if(operationOption.getJob().getId()==1126) {
            int a=0;
        }

        List<WorkCenter> workCenterSet = operationOption.getWorkCenterSet();
        //
        int sign=0;
        for (int i = 0; i < workCenterSet.size(); i++) {
            WorkCenter workCenter=workCenterSet.get(i);
            Machine earliestMachine = workCenter.earliestReadyMachine();
            double machineRTInS1 = earliestMachine.getReadyTime();
            double s1 = simulation.getSystemState().getDSTS()[workCenterSet.get(i).getMachinePortArea().get(0)-1] //s1
                    [operationOption.getObjectPortArea()-1]/13;

//            Process p = new Process(workCenterSet, earliestMachine.getId(),
//                    operationOption,simulation.getSystemState(), time);
            //the decision point move forward
//            machineRTInS1+=s1;
//            if (earliestMachine.getReadyTime() > time) {
            if (machineRTInS1 > time
//                  || !simulation.canAddToQueue(p)
            ) {
//                sign=1;
                sign++;
//                workCenterSet.get(i).addToQueue(operationOption);
            }
        }

        Process ptug = new Process(operationOption.getWorkCenterSet(),0,operationOption,
                simulation.getSystemState(),time);
        for (int i = 0; i < ptug.getPtForMachineInS1S2S3().size(); i++) {
            double a = ptug.getWorkCenterSet().get(i).getmachineTimeC1().get(0);
            double b = ptug.getPtForMachineInS1S2S3().get(i)[0];
            ptug.getWorkCenterSet().get(i).setmachineTimeC1(0,a+b);
            double a1 = ptug.getWorkCenterSet().get(i).getmachineTimeC0().get(0);
            double b1 = ptug.getPtForMachineInS1S2S3().get(i)[1];
            ptug.getWorkCenterSet().get(i).setmachineTimeC0(0,a1+b1);
        }

        if (workCenterSet.size()-sign!=0) {
            //modified by feige liu
            //
            ProcessStartEvent eventST=new ProcessStartEvent(ptug);
            simulation.addEvent(eventST); //the address of ptug is not the address of this job's operationOption
            for (int i = 0; i < ptug.getWorkCenterSet().size(); i++) {
                ptug.getWorkCenterSet().get(i).setCurrentProcessOnMachine(eventST);
            }
        }
        else {
            for (int j = 0; j < workCenterSet.size(); j++) {
//                    workCenterSet.get(j).addToQueue(operationOption);
                workCenterSet.get(j).addToQueueTug(simulation.getSystemState(),operationOption);
            }
        }

    }*/


    @Override
    public void addSequencingDecisionSituation(Simulation simulation,
                                     List<SequencingDecisionSituation> situations,
                                     int minQueueLength) {
        trigger(simulation);

    }

    @Override
    public void addRoutingDecisionSituation(Simulation simulation,
                                               List<RoutingDecisionSituation> situations,
                                               int minQueueLength) {
        trigger(simulation);
    }

    @Override
    public String toString() {
        return String.format("%.1f: job %d op %d visits.\n",
                time, operationOption.getJob().getId(), operationOption.getOperation().getId());
    }

    @Override
    public int compareTo(AbstractEvent other) {
        if (time < other.time)
            return -1;

        if (time > other.time)
            return 1;

        if (other instanceof JobArrivalEvent)
            return 1;

        if (other instanceof OperationVisitEvent)
            return 0;

        return -1;
    }

    public OperationOption getOperationOption() {return operationOption; }
}
