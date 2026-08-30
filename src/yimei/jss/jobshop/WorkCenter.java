package yimei.jss.jobshop;

import yimei.jss.simulation.event.ProcessStartEvent;
import yimei.jss.simulation.state.SystemState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by yimei on 24/09/16.
 */
public class WorkCenter {

    private final int id;
    private int numMachines;

    // Attributes for simulation.
    private LinkedList<OperationOption> queue;
    private List<Double> machineReadyTimes;

    //LIU FEIGE for tugboat, each tugboat as a machine and only have one stage
    private List<Integer> mahinePosition;
    private List<Double> machineSpeed;
    private List<Double> machineHorsepower;
    private List<Integer> machinePortArea; //tugboat belong to which tugboat-base in which port area

    private List<Double> machineTimeC0; //tugboat berthing total time

    private List<Double> machineTimeC1; //tugboat sailing time
    double[] Horse_C0_C1_VTi;

    //complete job/operation in the work center
    private LinkedList<Job> queueCompleteJob= new LinkedList<>();

    private ProcessStartEvent currentProcessOnMachine;


    private double workInQueue;
    //numOperation in queue    modfied by fzhang 20.4.2018
    private int numOperationInQueue;
    private double busyTime;
    public WorkCenter(int id, int numMachines,
                      LinkedList<OperationOption> queue,
                      List<Double> machineReadyTimes,
                      double workInQueue, double busyTime
               ) {
        this.id = id;
        this.numMachines = numMachines;
        this.queue = queue;
        this.machineReadyTimes = machineReadyTimes;
        this.workInQueue = workInQueue;
        this.busyTime = busyTime;
    }

    public WorkCenter(int id, int numMachines,
                      LinkedList<OperationOption> queue,
                      List<Double> machineReadyTimes,
                      double workInQueue, double busyTime,
                      List<Double> machineSpeed,
                      List<Double> machineHorsepower,
                      List<Integer> machinePortArea,
                      List<Double> machineTimec0,
                      List<Double> machineTimec1) {
        this.id = id;
        this.numMachines = numMachines;
        this.queue = queue;
        this.machineReadyTimes = machineReadyTimes;
        this.workInQueue = workInQueue;
        this.busyTime = busyTime;
        //LIUFEIGE
        this.machineSpeed = machineSpeed;
        this.machineHorsepower = machineHorsepower;
        this.machinePortArea = machinePortArea;
        this.mahinePosition = new ArrayList<>();
        mahinePosition.add(0,machinePortArea.get(0));
        this.machineTimeC0 = machineTimec0;
        this.machineTimeC1 = machineTimec1;
    }

    public WorkCenter(int id, int numMachines) {
        this(id, numMachines, new LinkedList<>(),
                new ArrayList<>(Collections.nCopies(numMachines, 0.0)),
                0.0, 0.0);
    }

    public WorkCenter(int id, int numMachines,
                      List<Double> machineSpeed,
                      List<Double> machineHorsepower,
                      List<Integer> machinePortArea){
        this(id, numMachines, new LinkedList<>(),
                new ArrayList<>(Collections.nCopies(numMachines, 0.0)),
                0.0, 0.0,
                machineSpeed,machineHorsepower,machinePortArea,
                new ArrayList<>(Collections.nCopies(numMachines, 0.0)),
                new ArrayList<>(Collections.nCopies(numMachines, 0.0)));
    }

    public WorkCenter(int id) {
        this(id, 1);
    }

    public int getId() {
        return id;
    }

    public int getNumMachines() {
        return numMachines;
    }

    public LinkedList<OperationOption> getQueue() {
        return queue;
    }

    // LIU FEIGE

    public ProcessStartEvent getCurrentProcessOnMachine() {
        return currentProcessOnMachine;
    }

    public void setCurrentProcessOnMachine(ProcessStartEvent currentProcessOnMachine) {
        this.currentProcessOnMachine = currentProcessOnMachine;
    }

    public LinkedList<Job> getQueueCompleteJob() {
        return queueCompleteJob;
    }

    public void setQueueCompleteJob(LinkedList<Job> queueCompleteJob) {
        this.queueCompleteJob = queueCompleteJob;
    }

    public List<Integer> getMahinePosition() {
        return mahinePosition;
    }

    public void setMahinePosition(List<Integer> mahinePosition) {
        this.mahinePosition = mahinePosition;
    }

    public double[] getHorse_C0_C1_VTi() {
        return Horse_C0_C1_VTi;
    }

    public void setHorse_C0_C1_VTi(double[] horse_C0_C1_VTi) {
        Horse_C0_C1_VTi = horse_C0_C1_VTi;
    }

    public List<Double> getMachineSpeed() {
        return machineSpeed;
    }

    public void setMachineSpeed(int idex, double MachineSpeed) {
        machineSpeed.set(idex, MachineSpeed);
    }

    public List<Double> getMachineHorsepower() {
        return machineHorsepower;
    }

    public void setMachineHorsepower(int idex, double MachineHorse) {
        machineHorsepower.set(idex,MachineHorse);
    }

    public List<Integer> getMachinePortArea() {
        return machinePortArea;
    }

    public void setmachineTimeC0(int idex, double machineTimeC0) {
        this.machineTimeC0.set(idex,machineTimeC0);
    }
    public List<Double> getmachineTimeC0() {
        return machineTimeC0;
    }

    public void setmachineTimeC1(int idex, double machineTimeC0) {
        this.machineTimeC1.set(idex,machineTimeC0);
    }
    public List<Double> getmachineTimeC1() {
        return machineTimeC1;
    }

    public void setMachinePortArea(int idex, int MachinePortArea) {
        this.machinePortArea.set(idex,MachinePortArea);
    }


    //fzhang 1.6.2018 get the min work (with min processing time):in the queue the queue here is not the jobs before a machine  F
    public double getMinProcessTimeInQueue() {
    	if(getQueue().size() == 0)
    		return 0;

    	double minProcessTime = getQueue().get(0).getProcTime();
        for(int i = 1; i< getQueue().size();i++)
        {
        	if(minProcessTime > getQueue().get(i).getProcTime()) {
        		minProcessTime = getQueue().get(i).getProcTime();
        	}
        }
		return minProcessTime;
    }

    public double getMaxProcessTimeInQueue() {
    	if(getQueue().size() == 0)
    		return 0;

    	double maxProcessTime = getQueue().get(0).getProcTime();
        for(int i = 1; i< getQueue().size();i++)
        {
        	if(maxProcessTime < getQueue().get(i).getProcTime()) {
        		maxProcessTime = getQueue().get(i).getProcTime();
        	}
        }
		return maxProcessTime;
    }
    //==========================================================================================================


    public double getMachineReadyTime(int idx) {
        return machineReadyTimes.get(idx);
    }

    public double getWorkInQueue() {
        return workInQueue;
    }

    public double getBusyTime() {
        return busyTime;
    }


   /* //Created by fzhang on 18/04/18.
    public double getAverageCostInQueue() {
    	return workInQueue/queue.size();
    }

    //Created by fzhang on 18/04/18.
    double totalAverageCostInQueue = 0;
    public double getTotalAverageCostInQueue() {
    	for(int i = 0; i< numMachines; i++) {
    		//it is not right here, the cost need to multiple a factor according to different mahcines
           totalAverageCostInQueue += workInQueue/queue.size();
    	}
        return totalAverageCostInQueue/numMachines;
    }
    //Created by fzhang on 18/04/18.
    double totalAverageProcessTimeInQueue = 0;
    public double getTotalAverageProcesTimeInQueue() {
    	for(int i = 0; i< numMachines; i++) {
    		totalAverageProcessTimeInQueue += workInQueue/queue.size();
    	}
        return totalAverageProcessTimeInQueue/numMachines;
    }

    //Created by fzhang on 20/04/18.   getAverageProcessTimeInSystem
    double averageProcessTimeInSystem = 0;
    double totalProcessTimeInSystem =0;
    public double getAverageProcesTimeInSystem() {
    	for(int i = 0; i< numMachines; i++) {
    		totalProcessTimeInSystem += workInQueue;
    	}
        return totalProcessTimeInSystem/queue.size();
    }
*/
    public double getReadyTime() {
        double readyTime = machineReadyTimes.get(0);

        for (int i = 1; i < machineReadyTimes.size(); i++) {
            double t = machineReadyTimes.get(i);
            if (readyTime > t)
                readyTime = t;
        }

        return readyTime;
    }

    public void setMachineReadyTime(int idx, double readyTime) {
        machineReadyTimes.set(idx, readyTime);
    }

    // numOperationInQueue
    public int numOpsInQueue() {
        return queue.size();
    }

    public int getNumOpsInQueue() {
        return queue.size();
    }

    public void reset(double readyTime) {
        queue.clear();
        queueCompleteJob.clear(); //LIUFEIGE
        for (int i = 0; i < numMachines; i++) {
            machineReadyTimes.set(i, readyTime);
            //LIUFEIGE
            machineTimeC0.set(i,(double)0);
            machineTimeC1.set(i,(double)0);
        }
        workInQueue = 0.0;
        busyTime = readyTime;
    }

    public void reset() {
        reset(0.0);
    }

    public void addToQueue(OperationOption o) {
        queue.add(o);
        workInQueue += o.getProcTime();
    }

    public void addToQueueTug(SystemState systemState, OperationOption o) {
        queue.add(o);
        workInQueue += o.getProcTime();
        //fot tugboat : just assume the process time in stage 1 and 2,
        // as the task process as soon as possible
        //so, add the pt in stage3 of the tugboat assumed
        double machineTime = systemState.getDSTE()
                [o.getObjectPortArea()-1]
                [this.getMachinePortArea().get(0)-1]/13;
        workInQueue += machineTime;

    }

    public void removeFromQueue(OperationOption o) {
        queue.remove(o);
       // System.out.println("workInQueue(The last work) " + workInQueue);
        //System.out.println("The time of last operation: "+o.getProcTime());
        //method1: but when we modify the processTime to int, this should be OK.
        //workInQueue -= o.getProcTime();

        //these are for system
        //System.out.println("The workInQueue after delete the last operaiton: "+workInQueue);
        //System.out.println("The number of operation after delete the last operaiton: "+o.getNumOpsRemaining());
       //method2:
      //modified by fzhang 10.5.2018    in orde to avoid negative, also positive (very equal to 0) value of workInQueue
      		if(queue.isEmpty())
      			workInQueue = 0.0;
      		else
      			workInQueue = workInQueue-o.getProcTime();

    }

    public Machine earliestReadyMachine() {
        Machine earliestReadyMachine =
                new Machine(0, this, machineReadyTimes.get(0));
        for (int i = 1; i < machineReadyTimes.size(); i++) {
            if (machineReadyTimes.get(i) < earliestReadyMachine.getReadyTime())
                earliestReadyMachine =
                        new Machine(i, this, machineReadyTimes.get(i));
        }

        return earliestReadyMachine;
    }

    public void incrementBusyTime(double value) {
        busyTime = value;
    }

    @Override
//    public String toString() {
//        return "W" + id + " [" + numMachines + "]";
//    }
    //LIUFEIGE for tugboat W[port][ID][Horsepower]
    public String toString() {
//        return String.format("PA%d, HP%d, RT%.2f, QN%d, CPN%d",
//                machinePortArea, machineHorsepower, machineReadyTimes.get(0), queue.size(), queueCompleteJob.size());
//        return "MA:" + machinePortArea + " HP," + machineHorsepower + " ,QZ "+ queue.size() +" ,RT "+ machineReadyTimes.get(0)
//                +", CPN"+ queueCompleteJob.size();
        return
                "MA:" +
                        machinePortArea +
                " HP," +
                        machineHorsepower +
                " ,QZ "+
                        queue.size() +
//                " ,RT "+
//                machineReadyTimes.get(0) +
                ", CPN"+
                        queueCompleteJob.size();
    }

    public boolean equals(WorkCenter other) {
        return id == other.id;
    }

    public WorkCenter clone() {
        LinkedList<OperationOption> clonedQ = new LinkedList<>(queue);
        List<Double> clonedMRT = new ArrayList<>(machineReadyTimes);

        return new WorkCenter(id, numMachines,
                clonedQ, clonedMRT, workInQueue, busyTime);
    }

    public String stateToString() {
        String string = "";
        for (int i = 0; i < machineReadyTimes.size(); i++) {
            string += String.format("(M%d,R%.1f) ", i, machineReadyTimes.get(i));
        }
        string += "\n Queue: ";
        for (OperationOption o : queue) {
            string += String.format("(J%d,O%d-%d,R%.1f) ",
                    o.getOperation().getJob().getId(), o.getOperation().getId(),
                    o.getOptionId(), o.getReadyTime());
        }
        string += "\n";

        return string;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WorkCenter that = (WorkCenter) o;

        if (id != that.id) return false;
        if (numMachines != that.numMachines) return false;
        if (Double.compare(that.workInQueue, workInQueue) != 0) return false;
        if (Double.compare(that.busyTime, busyTime) != 0) return false;
        if (queue != null ? !queue.equals(that.queue) : that.queue != null) return false;
        return machineReadyTimes != null ? machineReadyTimes.equals(that.machineReadyTimes) : that.machineReadyTimes == null;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = id;
        result = 31 * result + numMachines;
        result = 31 * result + (queue != null ? queue.hashCode() : 0);
        result = 31 * result + (machineReadyTimes != null ? machineReadyTimes.hashCode() : 0);
        temp = Double.doubleToLongBits(workInQueue);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(busyTime);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
