package yimei.jss.jobshop;

import yimei.jss.rule.AbstractRule;
import yimei.jss.simulation.state.SystemState;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by dyska on 7/05/17.
 *
 * An operation contains one or more operation options.
 *
 */
public class OperationOption implements Comparable<OperationOption> {

    private final Operation operation;
    private final int optionId;
    private double procTime;
    private WorkCenter workCenter;

    // Attributes for simulation.
    private double readyTime;
    private double workRemaining;
    private int numOpsRemaining;
    private double flowDueDate;
    private double nextProcTime;
    private double priority;

    //LIU FEIGE for tugboat schedule task
    private double[] priorityMultiTree;

    private int numNeedTug;
    private double upperHorsepower;
    private int objectPortArea;

    private double ptForShipInS2;

    private List<WorkCenter> workCenterSet;


    private double WAITING_TIME;


    //LIU FEIGE

    public double getPtForShipInS2() {
        return ptForShipInS2;
    }

    public void setPtForShipInS2(double ptForShipInS2) {
        this.ptForShipInS2 = ptForShipInS2;
    }

    public double[] getPriorityMultiTree() {
        return priorityMultiTree;
    }

    public void setPriorityMultiTree(double[] priorityMultiTree) {
        this.priorityMultiTree = priorityMultiTree;
    }

    public void setWorkCenter(WorkCenter workCenter) {
        this.workCenter = workCenter;
    }

    public void setWorkCenterSet(List<WorkCenter> workCenterSet) {
        this.workCenterSet = workCenterSet;
    }

    public List<WorkCenter> getWorkCenterSet() {
        return workCenterSet;
    }

    public List<WorkCenter> cloneWorkCenterSet() {
        LinkedList<WorkCenter> workCenters = new LinkedList<>();
        for (int i = 0; i < workCenterSet.size(); i++) {
            workCenters.add(workCenterSet.get(i));
        }
        return workCenters;
    }

    //lack number return 1,lack power return -1



    public int getObjectPortArea() {
        return objectPortArea;
    }

    public void setObjectPortArea(int objectPortArea) {
        this.objectPortArea = objectPortArea;
    }

    public int getNumNeedTug() {
        return numNeedTug;
    }

    public void setNumNeedTug(int numNeedTug) {
        this.numNeedTug = numNeedTug;
    }

    public double getUpperHorsepower() {
        return upperHorsepower;
    }

    public void setUpperHorsepower(double upperHorsepower) {
        this.upperHorsepower = upperHorsepower;
    }


    public double getWAITING_TIME() {
        return WAITING_TIME;
    }

    public void setWAITING_TIME(double WAITING_TIME) {
        this.WAITING_TIME = WAITING_TIME;
    }

    //LIUFEIGE
    public OperationOption(Operation operation, int optionId, double procTime, List<WorkCenter> workCenterSet,
                           double UpperHorse, int needTugNumber,int objectPortArea) {
        this.operation = operation;
        this.optionId = optionId;
        this.procTime = procTime;
        this.workCenterSet = workCenterSet;
        this.upperHorsepower = UpperHorse;
        this.numNeedTug = needTugNumber;
        this.objectPortArea = objectPortArea;
    }

    public OperationOption clone(){
        OperationOption stu = new OperationOption(operation,optionId,procTime,workCenter);
        stu.setWorkCenterSet(workCenterSet);
        stu.setUpperHorsepower(upperHorsepower);
        stu.setNumNeedTug(numNeedTug);
        stu.setObjectPortArea(objectPortArea);
        return stu;
    }

    public OperationOption(Operation operation, int optionId, double procTime, WorkCenter workCenter) {
        this.operation = operation;
        this.optionId = optionId;
        this.procTime = procTime;
        this.workCenter = workCenter;
    }

    public Operation getOperation() {
        return operation;
    }

    public int getOptionId() {
        return optionId;
    }

    public double getProcTime() {
        return procTime;
    }

    public void setProcTime(double procTime) {
        this.procTime = procTime;
    }

    public WorkCenter getWorkCenter() {
        return workCenter;
    }

    public Operation getNext() { return operation.getNext(); }

    public OperationOption getNext(SystemState systemState, AbstractRule routingRule) {
        Operation nextOperation = operation.getNext();
        if (nextOperation != null) {
            return nextOperation.chooseOperationOption(systemState, routingRule);
        } return null;
    }
    
    public double getReadyTime() {
        return readyTime;
    }

    public Job getJob() {
        return operation.getJob();
    }

    public double getWorkRemaining() {
        return workRemaining;
    }

    public int getNumOpsRemaining() {
        return numOpsRemaining;
    }

    public double getFlowDueDate() {
        return flowDueDate;
    }

    public double getNextProcTime() {
        return nextProcTime;
    }

    public double getPriority() {
        return priority;
    }

    public void setWorkRemaining(double workRemaining) {
        this.workRemaining = workRemaining;
    }

    public void setNumOpsRemaining(int numOpsRemaining) {
        this.numOpsRemaining = numOpsRemaining;
    }

    public void setReadyTime(double readyTime) {
        this.readyTime = readyTime;
    }

    public void setFlowDueDate(double flowDueDate) {
        this.flowDueDate = flowDueDate;
    }

    public void setNextProcTime(double nextProcTime) {this.nextProcTime = nextProcTime; }

    public void setPriority(double priority) {
        this.priority = priority;
    }

    /**
     * Compare with another process based on priority.
     * @param other the other process.
     * @return true if prior to other, and false otherwise.
     */
    //that is to say, the larger the better
    //fzhang 2018.11.7  prefer the highest priority value is easy to handle.
/*    public boolean priorTo(OperationOption other) {
        if (Double.compare(priority, other.priority) < 0)
            return false;

        if (Double.compare(priority, other.priority) > 0)
            return true;

        //the default setting, when the priority is the same, choose the operation that comes first
        return operation.getJob().getId() < other.operation.getJob().getId();
    }*/
    
    //that is to say, the smaller the better
    public boolean priorTo(OperationOption other) {
        if (Double.compare(priority, other.priority) < 0)
            return true;

        if (Double.compare(priority, other.priority) > 0)
            return false;

        return operation.getJob().getId() < other.operation.getJob().getId();
    }

    @Override
    public String toString() {
        return String.format("[J%d, WORK%d, PORT%d, T%.1f, RT%.1f]",
                getJob().getId(), optionId,objectPortArea, procTime,readyTime);
    }

    public boolean equals(OperationOption other) {
        return optionId == other.optionId && operation.getId() == other.operation.getId();
    }

    @Override
    public int compareTo(OperationOption other) {
        if (readyTime < other.readyTime)
            return -1;

        if (readyTime > other.readyTime)
            return 1;

        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OperationOption option = (OperationOption) o;
        if (operation != option.operation) return false;
        if (optionId != option.optionId) return false;
        if (Double.compare(option.procTime, procTime) != 0) return false;
        if (Double.compare(option.readyTime, readyTime) != 0) return false;
        if (Double.compare(option.workRemaining, workRemaining) != 0) return false;
        if (numOpsRemaining != option.numOpsRemaining) return false;
        if (Double.compare(option.flowDueDate, flowDueDate) != 0) return false;
        if (Double.compare(option.nextProcTime, nextProcTime) != 0) return false;
        if (Double.compare(option.priority, priority) != 0) return false;
        return workCenter != null ? workCenter.equals(option.workCenter) : option.workCenter == null;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = operation != null ? operation.hashCode() : 0;
        result = 31 * result + optionId;
        temp = Double.doubleToLongBits(procTime);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (workCenter != null ? workCenter.hashCode() : 0);
        temp = Double.doubleToLongBits(readyTime);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(workRemaining);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + numOpsRemaining;
        temp = Double.doubleToLongBits(flowDueDate);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(nextProcTime);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(priority);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
