package yimei.jss.jobshop;

import yimei.jss.simulation.state.SystemState;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by yimei on 22/09/16.
 */
public class Process implements Comparable<Process> {

    private WorkCenter workCenter;

    //LIUFEIGE
    private List<WorkCenter> workCenterSet;
    private List<double[]> ptForMachineInS1S2S3 = new LinkedList<>();
    private int machineId;
    private OperationOption operationOption;
    private double startTime;
    private double finishTime;

    private double finishTimeInS3;

    public Process(WorkCenter workCenter, int machineId, OperationOption operationOption, double startTime) {
        this.workCenter = workCenter;
        this.machineId = machineId;
        this.operationOption = operationOption;
        this.startTime = startTime;
        this.finishTime = startTime + operationOption.getProcTime();
    }

    //LIUFEIGE
    public Process(List<WorkCenter> workCenterSet, int machineId,
                   OperationOption operationOption,
                   SystemState systemState, double startTime) {
        this.workCenterSet = workCenterSet;
        this.machineId = machineId;
        this.operationOption = operationOption;
        this.startTime = startTime;  //it is job/ship release time in the tugboat scheduling

        //processtime ???
        //s1 consider all tugboat is in the tugboatbase and the distance in stage 1 is 0
        double maxStartTimeForStage2 = 0;
        double maxPtS1=0;
        for (int i = 0; i < workCenterSet.size(); i++) {
            double[] S1S2S3 = new double[3];
            ptForMachineInS1S2S3.add(S1S2S3);
            if(workCenterSet.get(i).getMachinePortArea().get(0)!=operationOption.getObjectPortArea()){
                double s1 = systemState.getDSTS()[workCenterSet.get(i).getMachinePortArea().get(0)-1] //s1
                        [operationOption.getObjectPortArea()-1]/13;
                double s2 = s1 +workCenterSet.get(i).getMachineReadyTime(0);
                this.getOperationOption().getJob().getStartTime1().add(workCenterSet.get(i).getMachineReadyTime(0));
                this.getOperationOption().getJob().getEndTime1().add(s2);
                if(s2>maxStartTimeForStage2)  maxStartTimeForStage2=s2;
                if(s1>maxPtS1) maxPtS1=s1;
                // each tugboat will arrival the start place and waiting for berthing process
                S1S2S3[0] = s1;
//                double totalsailingtime = workCenterSet.get(i).getmachineTimeC1().get(0);
//                double a = s1+totalsailingtime;
//                workCenterSet.get(i).setmachineTimeC1(0,s1+totalsailingtime);

            }else {
                double s2 = workCenterSet.get(i).getMachineReadyTime(0);
                this.getOperationOption().getJob().getStartTime1().add(workCenterSet.get(i).getMachineReadyTime(0));
                this.getOperationOption().getJob().getEndTime1().add(s2);
                if(s2>maxStartTimeForStage2) maxStartTimeForStage2=s2;
            }
        }

        if(startTime<maxStartTimeForStage2){
            operationOption.setProcTime(maxPtS1);  //set pt of ship? in the stage1,it's ship waiting time in the start area
            startTime=maxStartTimeForStage2;
        }else {
            operationOption.setProcTime(maxPtS1);
            //ship not waiting in the stage1
        }
        //set start time in stage 2
        operationOption.getJob().setStartTime(startTime);
        //s2
        double totalv=0;
        for (int i = 0; i < workCenterSet.size(); i++) {
            double v= workCenterSet.get(i).getHorse_C0_C1_VTi()[3];
            totalv += Math.pow(v,2);
        }
        totalv = Math.sqrt(totalv);

        double s2 = 2*operationOption.getJob().getDisStoB()/totalv;
        for (int i = 0; i < workCenterSet.size(); i++) {
            ptForMachineInS1S2S3.get(i)[1] = s2;
//            double totalberthingtime = workCenterSet.get(i).getmachineTimeC0().get(0);
//            workCenterSet.get(i).setmachineTimeC0(0,s2+totalberthingtime);
        }
        double pt = operationOption.getProcTime()+s2;
        operationOption.setProcTime(pt);  //pt=ship
        operationOption.setPtForShipInS2(s2);
        this.finishTime = startTime + s2;  //it's finish time in stage 2
    }



    public List<double[]> getPtForMachineInS1S2S3() {
        return ptForMachineInS1S2S3;
    }

    public void setPtForMachineInS1S2S3(List<double[]> ptForMachineInS1S2S3) {
        this.ptForMachineInS1S2S3 = ptForMachineInS1S2S3;
    }

    public void setWorkCenter(WorkCenter workCenter) {
        this.workCenter = workCenter;
    }

    public List<WorkCenter> getWorkCenterSet() {
        return workCenterSet;
    }

    public WorkCenter getWorkCenter() {
        return workCenter;
    }

    public int getMachineId() {
        return machineId;
    }

    public OperationOption getOperationOption() {
        return operationOption;
    }

    public double getStartTime() {
        return startTime;
    }

    public double getFinishTime() {
        return finishTime;
    }

    public double getDuration() {
        return finishTime - startTime;
    }

    @Override
//    public String toString() {
//        return String.format("([W%d,M%d], [J%d,O%d,O%d]: %.1f --> %.1f.\n",
//                workCenter.getId(), machineId, operationOption.getJob().getId(),
//                operationOption.getOperation().getId(), operationOption.getOptionId(), startTime, finishTime);
//    }

    public String toString() {
        return String.format("([J%d,O%d,O%d]: %.1f --> %.1f.\n",
                operationOption.getJob().getId(),
                operationOption.getOperation().getId(), operationOption.getOptionId(), startTime, finishTime);
    }

    @Override
    public int compareTo(Process other) {
        if (startTime < other.startTime)
            return -1;

        if (startTime > other.startTime)
            return 1;

        return 0;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Process process = (Process) o;

        if (machineId != process.machineId) return false;
        if (Double.compare(process.startTime, startTime) != 0) return false;
        if (Double.compare(process.finishTime, finishTime) != 0) return false;
        if (workCenter != null ? !workCenter.equals(process.workCenter) : process.workCenter != null) return false;
        return operationOption != null ? operationOption.equals(process.operationOption) : process.operationOption == null;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = workCenter != null ? workCenter.hashCode() : 0;
        result = 31 * result + machineId;
        result = 31 * result + (operationOption != null ? operationOption.hashCode() : 0);
        temp = Double.doubleToLongBits(startTime);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(finishTime);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    public void setFinishTimeInS3(double finishTimeInS3) {
        this.finishTimeInS3 = finishTimeInS3;

    }

    public double getFinishTimeInS3() {
        return finishTimeInS3;
    }
}
