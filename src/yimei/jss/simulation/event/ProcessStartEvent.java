package yimei.jss.simulation.event;

import yimei.jss.jobshop.Process;
import yimei.jss.jobshop.WorkCenter;
import yimei.jss.simulation.RoutingDecisionSituation;
import yimei.jss.simulation.SequencingDecisionSituation;
import yimei.jss.simulation.Simulation;

import java.util.List;

/**
 * Created by YiMei on 25/09/16.
 */
public class ProcessStartEvent extends AbstractEvent {

    private Process process;

    public ProcessStartEvent(double time, Process process) {
        super(time);
        this.process = process;
    }

    public ProcessStartEvent(Process process) {
        this(process.getStartTime(), process);
    }

    public Process getProcess() {
        return process;
    }

    @Override
//    public void trigger(Simulation simulation) {
//        WorkCenter workCenter = process.getWorkCenter();
//        workCenter.setMachineReadyTime(
//                process.getMachineId(), process.getFinishTime());
//        workCenter.incrementBusyTime(process.getDuration());
//
//        simulation.addEvent(
//                new ProcessFinishEvent(process.getFinishTime(), process));
//    }

    //for tugboat
    public void trigger(Simulation simulation) {
//        if(process.getOperationOption().getJob().getId()==1598) {
//            int a=0;
//        }
        List<WorkCenter> workCenterset = process.getWorkCenterSet();
        //step1 anf step2 is calculated in previous event trigger
        //for tugboat step 3
        for (int i = 0; i < workCenterset.size(); i++) {
            double machineTime = simulation.getSystemState().getDSTE()
                    [process.getOperationOption().getObjectPortArea()-1]
                    [workCenterset.get(i).getMachinePortArea().get(0)-1]/13;
            double s3 = machineTime;
            process.getPtForMachineInS1S2S3().get(i)[2]=s3;
            machineTime+=process.getFinishTime(); //fishtime is ship finish step2
            //
            double busytimeOworkCenter = machineTime-process.getStartTime(); //？
            workCenterset.get(i).incrementBusyTime(busytimeOworkCenter); //

            process.getOperationOption().getJob().getEndTime3().add(machineTime); //record

            workCenterset.get(i).setMachineReadyTime(
                    process.getMachineId(), machineTime);
            //the process finished
            workCenterset.get(i).setCurrentProcessOnMachine(null);

            double a = workCenterset.get(i).getmachineTimeC1().get(0)+s3;
            workCenterset.get(i).setmachineTimeC1(0,a);

        }
        //set the pt in stage 1 2 3 of the ship task
        process.getOperationOption().setPtForShipInS2(process.getPtForMachineInS1S2S3().get(0)[1]);
        //process is the task
        double processFinishTime = 0;
        for (int i = 0; i < workCenterset.size(); i++) {
            if(workCenterset.get(i).getMachineReadyTime(0)>processFinishTime)
                processFinishTime = workCenterset.get(i).getMachineReadyTime(0);
        }
        //it is a task for a ship and tugboat, fishing stage 1 2 3 and get the finish time of a task
        process.setFinishTimeInS3(processFinishTime);

        // use the earliest tugboat release time as the process finishing in "time"
        // or use the latest tugboat release time as the process finishing t int time
        //
        simulation.addEvent(
                new ProcessFinishEvent(process.getFinishTime(), process));
    }


    @Override
    public void addSequencingDecisionSituation(Simulation simulation,
                                     List<SequencingDecisionSituation> situations,
                                     int minQueueLength) {
        trigger(simulation);
//        triggerFortugboat(simulation);
    }

    @Override
    public void addRoutingDecisionSituation(Simulation simulation,
                                               List<RoutingDecisionSituation> situations,
                                               int minQueueLength) {
        trigger(simulation);
//        triggerFortugboat(simulation);
    }

//    @Override
//    public String toString() {
//        return String.format("%.1f: job %d op %d started on work center %d.\n",
//                time,
//                process.getOperationOption().getJob().getId(),
//                process.getOperationOption().getOperation().getId(),
//                process.getWorkCenter().getId());
//    }

    @Override
    public String toString() {
        return String.format("%.1f: job %d op %d started on work centerSize %d.\n",
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

        if (other instanceof ProcessStartEvent)
            return 0;

        if (other instanceof ProcessFinishEvent)
            return -1;

        return 1;
    }
}
