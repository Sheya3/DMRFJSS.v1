package yimei.jss.jobshop;

import yimei.jss.simulation.event.ProcessFinishEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A job.
 *
 * Created by yimei on 22/09/16.
 */
public class Job implements Comparable<Job> {

    private final int id;
    private List<Operation> operations;
    private List<ProcessFinishEvent> processFinishEvents;
    private final double arrivalTime;
    private final double releaseTime;
    private double dueDate;
    private final double weight;

    //LIUFEIGE
    private double portArea;
    private double berthArea;
    private double disSTB;

    private double shipLength;
    private double DisStoB;

    private double upperHorsepower = -1;

    private double LowerHorsepower = -1;
    private int numNeedTug = -1;

    private int LeastNumNeedTug= -1; //it less than the numNeedTug. it is the least num of tugboat with horsepower constrains

    private double[] LeastTugNumType = {-1,-1};

    private double totalProcTime;
    private double avgProcTime;
    private double completionTime; // finish in stage 2?

    private List<Double> startTime1 = new ArrayList<>(); // it is starttime of stage 1 of each tugboat
    private List<Double> endTime3 = new ArrayList<>(); // it is finishing time of stage 3 of each tugboat

    private List<Double> endTime1 = new ArrayList<>(); // it is finishing time of stage 1 of each tugboat
    private double startTime2; // it is starttime of stage 2

    private double startTime3; // it is starttime of stage 2






    public Job(int id,
               List<Operation> operations,
               double arrivalTime,
               double releaseTime,
               double dueDate,
               double weight,
               //LIUFEIGE for tugboat
               int portArea,
               double disSTB,
               double shipLength,
               double DisStoB
               ) {
        this.id = id;
        this.operations = operations;
        this.arrivalTime = arrivalTime;
        this.releaseTime = releaseTime;
        this.dueDate = dueDate;
        this.weight = weight;
        this.processFinishEvents = new ArrayList<ProcessFinishEvent>();
        //for tugboat LIUFEIGE
        this.portArea = portArea;
        this.disSTB = disSTB;
        this.shipLength = shipLength;
        this.DisStoB = DisStoB;
//-------------------Instance-------ship need----------LIUFEIGE---------------
//        if(shipLength<120&&shipLength>=80) {upperHorsepower=6900;numNeedTug=1;LeastNumNeedTug=0;}
//        else if (shipLength<180&&shipLength>=120) {upperHorsepower=6900;numNeedTug=2;LeastNumNeedTug=0;LeastTugNumType= new double[]{1, 3000};} //1 tug more than 3000
//        else if (shipLength<230&&shipLength>=180) {upperHorsepower=6900;numNeedTug=2;LeastNumNeedTug=3000;}
//        else if (shipLength<270&&shipLength>=230) {upperHorsepower=6900;numNeedTug=2;LeastNumNeedTug=4000;}
//        else if (shipLength<350&&shipLength>=270) {upperHorsepower=6900;numNeedTug=3;LeastNumNeedTug=0;LeastTugNumType= new double[]{2, 4000};} // 2 tug more than 4000
        if(shipLength<120) {upperHorsepower=4000;numNeedTug=1;LeastNumNeedTug=0;LowerHorsepower=1600;}
        else if (shipLength<180) {upperHorsepower=5000;numNeedTug=2;LeastNumNeedTug=1;LowerHorsepower=3000;}
        else if (shipLength<230) {upperHorsepower=6000;numNeedTug=2;LeastNumNeedTug=2;LowerHorsepower=4000;}
        else if (shipLength<270) {upperHorsepower=6800;numNeedTug=2;LeastNumNeedTug=2;LowerHorsepower=5000;}
        else {upperHorsepower=6900;numNeedTug=2;LeastNumNeedTug=2;LowerHorsepower=6000;}
    }
    public int numberHorseTug (List<WorkCenter> workCenterSet){
        //----------LIUFEIGE--------------plan1--------------
//        if(workCenterSet.size()>=numNeedTug){
//            double sumHorse=0;
//            for (int i = 0; i < workCenterSet.size(); i++) {
//                sumHorse += workCenterSet.get(i).getMachineHorsepower().get(0);
//            }
//            if (sumHorse>=upperHorsepower){
//                return 0;
//            }else return -1;
//        }else return 1;
        //----------LIUFEIGE--------------plan2--------------
        if(workCenterSet.size()==numNeedTug){
            return 0;
        }else return 1;
        //------------LIUFEIGE------------plan3

//        if(workCenterSet.size()!=numNeedTug){
//            return 1;
//        }else {
//
//        }

    }

    public Job(int id,
               List<Operation> operations,
               double arrivalTime,
               double releaseTime,
               double dueDate,
               double weight
    ) {
        this.id = id;
        this.operations = operations;
        this.arrivalTime = arrivalTime;
        this.releaseTime = releaseTime;
        this.dueDate = dueDate;
        this.weight = weight;
        this.processFinishEvents = new ArrayList<ProcessFinishEvent>();
    }

    public Job(int id, List<Operation> operations) {
        this(id, operations,
                0, 0, Double.POSITIVE_INFINITY, 1.0,
                0,0,80,0.5);
    }

    public int getId() {
        return id;
    }

    public List<Operation> getOperations() {
        return operations;
    }

    public List<ProcessFinishEvent> getProcessFinishEvents() { return processFinishEvents; }

    public void addProcessFinishEvent(ProcessFinishEvent processFinishEvent) {
//        for (ProcessFinishEvent p: processFinishEvents) {
//            if (p.getProcess().getOperationOption().getOperation().getId() ==
//                    processFinishEvent.getProcess().getOperationOption().getOperation().getId()) {
//                System.out.println("Shouldn't happen");
//            }
//        }
        processFinishEvents.add(processFinishEvent);
    }

    public Operation getOperation(int idx) {
        return operations.get(idx);
    }

    public double getArrivalTime() {
        return arrivalTime;
    }

    public double getReleaseTime() {
        return releaseTime;
    }

    public double getDueDate() {
        return dueDate;
    }

    public double getWeight() {
        return weight;
    }

    public double getTotalProcTime() {
        return totalProcTime; //the time that really eas used to process the jobs
    }

    public double getAvgProcTime() {
        return avgProcTime;
    }

    //fzhang 29.8.2018 get the unprocessingtime (waiting time)
    public double getWaitingTime() {
        return this.flowTime() - totalProcTime;
    }
    
    public double getCompletionTime() {
        return completionTime; //completionTime: the finish time(a time points)
    }


    //LIUFEIGE fot tugboat get
    public double getLowerHorsepower() {
        return LowerHorsepower;
    }

    public void setLowerHorsepower(double lowerHorsepower) {
        LowerHorsepower = lowerHorsepower;
    }

    public List<Double> getEndTime3() {
        return endTime3;
    }

    public void setEndTime3(List<Double> endTime3) {
        this.endTime3 = endTime3;
    }

    public List<Double> getEndTime1() {
        return endTime1;
    }

    public void setEndTime1(List<Double> endTime1) {
        this.endTime1 = endTime1;
    }

    public List<Double> getStartTime1() {
        return startTime1;
    }

    public void setStartTime1(List<Double> startTime1) {
        this.startTime1 = startTime1;
    }

    public double getStartTime() {
        return startTime2;
    }

    public void setStartTime(double startTime) {
        this.startTime2 = startTime;
    }

    public double getUpperHorsepower() {
        return upperHorsepower;
    }

    public int getNumNeedTug() {
        return numNeedTug;
    }

    public double getPortArea() {
        return portArea;
    }

    public double getBerthArea() {
        return berthArea;
    }

    public double getShipLength() {
        return shipLength;
    }

    public double getDisStoB() {
        return DisStoB;
    }

    public void setDueDate(double dueDate) {
        this.dueDate = dueDate;
    }

    public void setCompletionTime(double completionTime) {
        this.completionTime = completionTime;
    }

    public double flowTime() {
        return completionTime - arrivalTime;
        // the time period between the job arrives and the job is finished.
        // Including the waiting time
    }

    public double weightedFlowTime() {
        return weight * flowTime();
    }

    public double tardiness() {
        double tardiness = completionTime - dueDate;
        if (tardiness < 0)
            tardiness = 0;

        return tardiness;
    }

    public double weightedTardiness() {
        return weight * tardiness();
    }

    public void addOperation(Operation op) {
        operations.add(op);
    }

    public void linkOperations() {
        Operation next = null;
        double nextProcTime = 0.0;

        //double fdd = releaseTime;

//        for (int i = 0; i < operations.size(); i++) {
//            Operation operation = operations.get(i);
//            for (OperationOption option: operation.getOperationOptions()) {
//                option.setFlowDueDate(fdd + option.getProcTime());
//            }
//            fdd += operation.getOperationOption().getProcTime();
//        }

        //play with this - just use average values?
        //or average among the options?

        double workRemaining = 0.0;
        int numOpsRemaining = 0;
        for (int i = operations.size()-1; i > -1; i--) {
            Operation operation = operations.get(i);

            double medianProcTime;
            //for one operation, it has several options
            double[] procTimes = new double[operation.getOperationOptions().size()];
            //put different processing time in proceTimes[], but now here the value should be the same
            for (int j = 0; j < operation.getOperationOptions().size(); ++j) {
                procTimes[j] = operation.getOperationOptions().get(j).getProcTime();
            }
            Arrays.sort(procTimes);
            //get the median value
            if (procTimes.length % 2 == 0){
                //halfway between two points, as even number of elements
                medianProcTime = (procTimes[procTimes.length/2]
                        + procTimes[procTimes.length/2 - 1])/2;
            }
            else {
                medianProcTime = procTimes[procTimes.length / 2];
            }

            //set every option to the same values
            for (OperationOption option: operation.getOperationOptions()) {

                option.setWorkRemaining(workRemaining + medianProcTime);

                option.setNumOpsRemaining(numOpsRemaining);

                option.setNextProcTime(nextProcTime);
            }

            numOpsRemaining++;
            //workRemaining is a variable for the whole machines, but for one specific machine
            workRemaining += medianProcTime;

            operation.setNext(next);

            next = operation;
            nextProcTime = medianProcTime; //average guess 
            //nextProcTime is the median value of processing time.
            //in flexible job shop scheduling, we have different processing times, but we do not know the next job will
            //be assigned to which machine, so guess a value (use median time as next processing time)
        }
        totalProcTime = workRemaining;
        avgProcTime = totalProcTime / operations.size();
    }

//    @Override
//    public String toString() {
//        String string = String.format("Job %d, arrives at %.1f, due at %.1f, weight is %.1f. It has %d operations:\n",
//                id, arrivalTime, dueDate, weight, operations.size());
//        for (Operation operation: operations) {
//            string += operation.toString();
//        }
//
//        return string;
//    }

    public String toString() {
        String string = String.format("Job %d, arrives at %.1f, " +
                        "due at %.1f, start2 at %.1f, complete at %.1f, " +
                        "pt is %.2f. It has %d machine: ",
                id, arrivalTime, dueDate, startTime2, completionTime,
                flowTime(), operations.get(0).getOperationOptions().get(0).getWorkCenterSet().size());
        for (Operation operation: operations) {
            string += operation.getOperationOptions().get(0).getWorkCenterSet();
        }
        string +="\n";
        return string;
    }

    public boolean equals(Job other) {
        return id == other.id;
    }

    @Override
    public int compareTo(Job other) {
        if (arrivalTime < other.arrivalTime)
            return -1;

        if (arrivalTime > other.arrivalTime)
            return 1;

        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Job job = (Job) o;

        if (id != job.id) return false;
        if (Double.compare(job.arrivalTime, arrivalTime) != 0) return false;
        if (Double.compare(job.releaseTime, releaseTime) != 0) return false;
        if (Double.compare(job.dueDate, dueDate) != 0) return false;
        if (Double.compare(job.weight, weight) != 0) return false;
        if (Double.compare(job.totalProcTime, totalProcTime) != 0) return false;
        if (Double.compare(job.avgProcTime, avgProcTime) != 0) return false;
        if (Double.compare(job.completionTime, completionTime) != 0) return false;
        if (operations != null ? !operations.equals(job.operations) : job.operations != null) return false;
        return processFinishEvents != null ? processFinishEvents.equals(job.processFinishEvents) : job.processFinishEvents == null;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = id;
        result = 31 * result + (operations != null ? operations.hashCode() : 0);
        result = 31 * result + (processFinishEvents != null ? processFinishEvents.hashCode() : 0);
        temp = Double.doubleToLongBits(arrivalTime);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(releaseTime);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(dueDate);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(weight);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(totalProcTime);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(avgProcTime);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(completionTime);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
