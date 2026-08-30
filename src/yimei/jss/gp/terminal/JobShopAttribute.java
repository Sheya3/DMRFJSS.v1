package yimei.jss.gp.terminal;

import org.apache.commons.lang3.math.NumberUtils;
import yimei.jss.jobshop.OperationOption;
import yimei.jss.jobshop.WorkCenter;
import yimei.jss.simulation.state.SystemState;

import java.util.*;

/**
 * The attributes of the job shop.
 * NOTE: All the attributes are relative to the current time.
 *       This is for making the decision making process memoryless,
 *       i.e. independent of the current time.
 *
 * @author yimei
 */

public enum JobShopAttribute {
    CURRENT_TIME("t"), // the current time

    // The machine-related attributes (independent of the jobs in the queue of the machine).
    NUM_OPS_IN_QUEUE("NIQ"), // the number of operations in the queue
    WORK_IN_QUEUE("WIQ"), // the work in the queue
    MACHINE_READY_TIME("MRT"), // the ready time of the machine

    // The job/operation-related attributes (depend on the jobs in the queue).
    PROC_TIME("PT"), // the processing time of the operation
    NEXT_PROC_TIME("NPT"), // the processing time of the next operation

    //modified by fzhang 31.5.2018
    LEAST_NEXT_PROC_TIME("LNPT"),
    MAX_NEXT_PROC_TIME("MNPT"),
    MEDIAN_NEXT_PROC_TIME("DNPT"),

    OP_READY_TIME("ORT"), // the ready time of the operation
    //NEXT_READY_TIME("NRT"), // the ready time of the next machine
    WORK_REMAINING("WKR"), // the work remaining
    NUM_OPS_REMAINING("NOR"), // the number of operations remaining
    //WORK_IN_NEXT_QUEUE("WINQ"), // the work in the next queue
    //NUM_OPS_IN_NEXT_QUEUE("NINQ"), // number of operations in the next queue
    //FLOW_DUE_DATE("FDD"), // the flow due date
    DUE_DATE("DD"), // the due date
    WEIGHT("W"), // the job weight
    ARRIVAL_TIME("AT"), // the arrival time

    // Relative version of the absolute time attributes
    MACHINE_WAITING_TIME("MWT"), // the waiting time of the machine = t - MRT
    OP_WAITING_TIME("OWT"), // the waiting time of the operation = t - ORT ---that is, time in queue(TIQ), how long an operation 
    //will in a queue
    //NEXT_WAITING_TIME("NWT"), // the waiting time for the next machine to be ready = NRT - t
    RELATIVE_FLOW_DUE_DATE("rFDD"), // the relative flow due date = FDD - t
    RELATIVE_DUE_DATE("rDD"), // the relative due date = DD - t

    //modified by fzhang 31.5.2018
    //information about jobs
    INTER_ARRIVAL_TIME_MEAN("IATM"),

    //modified by fzhang 31.5.2018 job-related
    DEVIATION_OF_JOB_IN_QUEUE("DJ"),

    //modified by fzhang  24.5.2018
	//information about the whole system  routing
    MACHINE_WORKLOAD_RATIO("MWR"),

   /* LEAST_MACHINE_WORKLOAD_RATIO("LWR"),  //for one machine, the workload over the workInSystem,ratio
    MAX_MACHINE_WORKLOAD_RATIO("MWR"),
    AVE_MACHINE_WORKLOAD_RATIO("AWR"),*/

    MACHINE_NUM_OPERATION_RATIO("MNR"),
    NUM_CANDIATE_MACHINE("NCM"), //for each job, if it has more options, maybe do not need to assign it to a machine, low chosen cost
                                 //for job has very limited candiate machines, maybe need give more attention
    AVE_PROC_TIME_IN_QUEUE("APTQ"),        //for one machine, the avearge needed time to finifsh all current jobs

    AVE_WORKLOAD_IN_SYSTEME("AWIS"), //the average workload for each machine (in current system)
    AVE_NUM_OPERATION_IN_SYSTEME("AOIS"), //the average number of operations for each machine (in current system)

    //look-ahead
    LEAST_WORK_IN_NEXT_QUEUE("LWINQ"), //among the candiate machines, which one has the least work in queue
    MAX_WORK_IN_NEXT_QUEUE("MWINQ"),  //among the candiate machines, which one has the max work in queue
    AVE_WORK_IN_NEXT_QUEUE("AWINQ"),  //for candiate machines, the average work in queue

    LEAST_NUM_OPERATIOM_IN_NEXT_QUEUE("LOINQ"),
    MAX_NUM_OPERATIOM_IN_NEXT_QUEUE("MOINQ"),
    AVE_NUM_OPERATIOM_IN_NEXT_QUEUE("AOINQ"),
    
    //fzhang 19.7.2018 current information
    TOTAL_WORK_IN_SYSTEM("TWIS"),
    TOTAL_OPERATION_IN_SYSTEM("TOIS"),
    
    //fzhang 19.7.2018 history terminals
    BUSY_TIME("BT"),
    AVERAGE_BUSY_TIME("ABT"),
    NUM_COMPLETED_JOB("NCJ"),
    WAITING_TIME("WT"), //the waiting time of ships in berth


    // Used in Su's paper
    TIME_IN_SYSTEM("TIS"), // time in system = t - releaseTime
    SLACK("SL"),
    AVE_MACHINE_WAITING_TIME("AMWT"),




    //modified by Liu Feige
    MIN_NUMBER_OF_SHIP("MNOT"), //the minimum number of tugboat that the ship needs
    MIN_HORSEPOWER_OF_SHIP("MHOT"),//the minimum horsepower of tugboat that the ship needs
    DISTANCE_STARTINGPOSITION_TO_BERTH("DSTB"),//the distance between berth and starting position of a task
    DISTANCE_STARTINGPOSITION_TO_END("DSTE"),//the distance between starting position and ending position of a task
    DISTANCE_STARTINGPOSITION_TO_START("DSTS"), //the distance between starting position and starting position of a task
    AVE_WORK_IN_QUEUE("AWIQ"),
    MAX_SPEED("MS"), // the max speed of the machine tugboat
    HORSE_POWER("HP"), //the horsepower of tugboat
    LACK_Of_HORSEPOWER("LOH"), // the lack of horsepower for ship
    ENERGY_TUG("EB"),
    ENERGY_IDE("ES"),

    PERCENTAGE_OF_TUG_TO_SHIP("PTTS"),
    PT_STARTINGPOSITION_TO_END("PTSTE"),
    PT_STARTINGPOSITION_TO_START("PTSTS"),

    SPEED("SP"),
    AVE_ENERGY_TUG("AET"),
    AVE_ENERGY_IDE("AEI"),
    AVE_STARTINGPOSITION_TO_START("ADSTS"),
    AVE_STARTINGPOSITION_TO_END("ADSTE"),
    BERTH_PROC_TIME("BPT"),
    SAILING_PROC_TIME_SS("SSPT"),
    SAILING_PROC_TIME_SE("SEPT"),
    MACHINE_BUSYTIME("MBT"),
    OPERATION_RT_INS1("ORTS1"),
    OPERATION_PT_INS2_OFWCS("BPTS"),
    OPERATION_PT_INS3_OFWCS("SEPTS"),
    OPERATION_NIQ_OFWCS("NIQOMS"),
    OPERATION_PT_INS1_OFWCS("SSPTS"),
    OP_WAITING_TIME_WS("OWTS"),
    TOTAL_ENERGY_TUG("TEB"),
    TOTAL_ENERGY_IDE("TES"),
    MAX_NUM_OPS_IN_QUEUE("MAXNIQ"),
    MIN_NUM_OPS_IN_QUEUE("MINNIQ"),
    AVG_NUM_OPS_IN_QUEUE("ANIQ"),
    MAX_WIQ("MAXWIQ"),
    MIN_WIQ("MINWIQ"),
    AVG_WIQ("AWIQ"),
    MAX_MWT("MAXMWT"),
    MIN_MWT("MINMWT"),
    AVG_MWT("AMWT"),
    MAX_MBT("MAXMBT"),
    MIN_MBT("MINMBT"),
    AVG_MBT("AMBT"),
    SPEED_OF_MS("MSS"); // the slack //    AVE_WORK_IN_QUEUE("AWIQ"), same definition but for different strategy

    private final String name;

    JobShopAttribute(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    // Reverse-lookup map
    private static final Map<String, JobShopAttribute> lookup = new HashMap<>();

    static {
        for (JobShopAttribute a : JobShopAttribute.values()) {
            lookup.put(a.getName(), a);
        }
    }

    public static JobShopAttribute get(String name) {
        return lookup.get(name);
    }

    //LIUFEIGE for tugboatset
    public double value(OperationOption op,
                        WorkCenter wk,
                        List<WorkCenter> workCenterset,
                        SystemState systemState
    ) {
        double value = -1;
        switch (this) {
            case MIN_NUMBER_OF_SHIP:
                value = op.getNumNeedTug();
                break;
            case MIN_HORSEPOWER_OF_SHIP:
                value = op.getUpperHorsepower();
                break;
            case AVE_WORK_IN_QUEUE:
                for (int i = 0; i < workCenterset.size(); i++) {
                    value += workCenterset.get(i).getWorkInQueue()/workCenterset.size();
                }
                break;
            case DISTANCE_STARTINGPOSITION_TO_BERTH:
                int s = op.getObjectPortArea()-1;
                int b = (int) op.getJob().getBerthArea()-1;
                value = op.getJob().getDisStoB();
                break;
            case PERCENTAGE_OF_TUG_TO_SHIP:
                for (int i = 0; i < workCenterset.size(); i++) {
                    value += workCenterset.get(i).getHorse_C0_C1_VTi()[0]/(op.getUpperHorsepower()*op.getNumNeedTug());
                }
                break;
            case AVG_NUM_OPS_IN_QUEUE:
                value=0;
                for (int i = 0; i < workCenterset.size(); i++) {
                   WorkCenter workCenter = workCenterset.get(i);
                    if (workCenter.getQueue()!=null) //LIUFEGE
                        value += workCenter.getQueue().size();
                    else value+=0;
                }
                if(wk!=null) {
                    value += wk.getQueue().size();
                    value /= (workCenterset.size()+1);
                }else value /= (workCenterset.size());
                break;
            case MAX_NUM_OPS_IN_QUEUE:
                value = 0;
                if(wk!=null) value=wk.getQueue().size();
                for (int i = 0; i < workCenterset.size(); i++) {
                    WorkCenter workCenter = workCenterset.get(i);
                    if (workCenter.getQueue()!=null&& workCenter.getQueue().size()>value) //LIUFEGE
                    {
                        value=workCenter.getQueue().size();
                    }
                }
                break;
            case MIN_NUM_OPS_IN_QUEUE:
                value=999999999;
                if(wk!=null) value=wk.getQueue().size();
                for (int i = 0; i < workCenterset.size(); i++) {
                    WorkCenter workCenter = workCenterset.get(i);
                    if (workCenter.getQueue()!=null&& workCenter.getQueue().size()<value) //LIUFEGE
                    {
                        value=workCenter.getQueue().size();
                    }
                }
                break;
            case SPEED:
                double totalv=0;
                for (int i = 0; i < workCenterset.size(); i++) {
                    double v= workCenterset.get(i).getHorse_C0_C1_VTi()[3];
                    totalv += Math.pow(v,2);
                }
                value = Math.sqrt(totalv);
                break;
            case AVE_ENERGY_TUG:
                for (int i = 0; i < workCenterset.size(); i++) {
                    value += workCenterset.get(i).getHorse_C0_C1_VTi()[1]/workCenterset.size()/workCenterset.size();
                }
                break;
            case AVE_ENERGY_IDE:
                for (int i = 0; i < workCenterset.size(); i++) {
                    value += workCenterset.get(i).getHorse_C0_C1_VTi()[2]/workCenterset.size()/workCenterset.size();
                }
                break;
            case AVE_STARTINGPOSITION_TO_START:
                int s31= op.getObjectPortArea()-1;
                for (int i = 0; i < workCenterset.size(); i++) {
                    int s21 = workCenterset.get(i).getMachinePortArea().get(0)-1;
                    value += systemState.getDSTS()[s21][s31]/workCenterset.size();
                }
                break;
            case AVE_STARTINGPOSITION_TO_END:
                int s11 = op.getObjectPortArea()-1;
                for (int i = 0; i < workCenterset.size(); i++) {
                    int e1 = workCenterset.get(i).getMachinePortArea().get(0)-1;
                    value += systemState.getDSTE()[s11][e1]/workCenterset.size();
                }
                break;

            case LACK_Of_HORSEPOWER:
                value = op.getJob().getUpperHorsepower();
                for (int i = 0; i < op.getWorkCenterSet().size(); i++) {
                    value-=op.getWorkCenterSet().get(i).getMachineHorsepower().get(0);
                }
                break;
            case AVG_MWT:
                for (int i = 0; i < workCenterset.size(); i++) {
                    value += (systemState.getClockTime() - workCenterset.get(i).getReadyTime());
                }
                if(wk!=null) {
                    value+=systemState.getClockTime() - wk.getReadyTime();
                    value=value/(workCenterset.size()+1);
                }else {
                    value=value/(workCenterset.size());
                }
                break;
            case MAX_MWT:
                double maxmwt=0;
                for (int i = 0; i < workCenterset.size(); i++) {
                    double mwt0 = (systemState.getClockTime() - workCenterset.get(i).getReadyTime());
                    if(mwt0>maxmwt) maxmwt=mwt0;
                }
                if(wk!=null&&systemState.getClockTime() - wk.getReadyTime()>maxmwt)
                    maxmwt=systemState.getClockTime() - wk.getReadyTime();
                value = maxmwt;
                break;
            case MIN_MWT:
                double minmwt=999999999;
                if(wk!=null) minmwt=systemState.getClockTime() - wk.getReadyTime();
                for (int i = 0; i < workCenterset.size(); i++) {
                    double mwt0 = (systemState.getClockTime() - workCenterset.get(i).getReadyTime());
                    if(mwt0<minmwt) minmwt=mwt0;
                }
                value = minmwt;
                break;
            case PROC_TIME:
                //value for tugboat, LIUFEIGE
               value=0;
                for (int i = 0; i < workCenterset.size(); i++) {
                    double v= workCenterset.get(i).getHorse_C0_C1_VTi()[3];
                    value += Math.pow(v,2);
                }
                totalv = Math.sqrt(value);
                double s2 = 2*op.getJob().getDisStoB()/totalv;
                value = s2;
                break;
            case NEXT_PROC_TIME:
                value = systemState.getClockTime();
                break;
            case OP_WAITING_TIME:
                value = systemState.getClockTime() - op.getReadyTime();
//                value = systemState.getClockTime() - op.getJob().getReleaseTime();
                break;
            case WORK_REMAINING:
                value = op.getWorkRemaining();
                break;
            case NUM_OPS_REMAINING:
                value = op.getNumOpsRemaining();
                break;
            case TIME_IN_SYSTEM:
                value = systemState.getClockTime() - op.getJob().getReleaseTime();
                break;
            case WEIGHT:
                value = op.getJob().getWeight();
                break;
            case BERTH_PROC_TIME:
                value = op.getJob().getDisStoB()/wk.getHorse_C0_C1_VTi()[3];
                break;
            case SAILING_PROC_TIME_SS:
                int s22 = wk.getMachinePortArea().get(0)-1;
                int s33= op.getObjectPortArea()-1;
                value = systemState.getDSTS()[s22][s33]/13;
                break;
            case SAILING_PROC_TIME_SE:
                int s1 = op.getObjectPortArea()-1;
                int e = wk.getMachinePortArea().get(0)-1;
                value = systemState.getDSTE()[s1][e]/13;
                break;
            case NUM_OPS_IN_QUEUE:
                if (wk.getQueue()!=null) //LIUFEGE
                    value = wk.getQueue ().size();   //wk
                else value=0;
                break;
            case WORK_IN_QUEUE:
                value = wk.getWorkInQueue();   //wk
                break;
            case MACHINE_WAITING_TIME:
                value = systemState.getClockTime() - wk.getReadyTime();  //wk
//                value = wk.getReadyTime();  //wk
                break;
            case DUE_DATE:
                value = op.getJob().getDueDate();
                break;
            case ENERGY_IDE:
//                value = wk.getHorse_C0_C1_VTi()[1];
                //energy Consumption of the tugboat for this task
                double timeInstage1andstage2=systemState.getDSTS()[wk.getMachinePortArea().get(0)-1]
                        [op.getObjectPortArea()-1]/13+systemState.getDSTE()
                        [op.getObjectPortArea()-1][wk.getMachinePortArea().get(0)-1]/13;
                value = timeInstage1andstage2*wk.getHorse_C0_C1_VTi()[1];
                break;
            case ENERGY_TUG:
//                value = wk.getHorse_C0_C1_VTi()[2];
                double v0= wk.getHorse_C0_C1_VTi()[3];
                value = 2*op.getJob().getDisStoB()/v0*wk.getHorse_C0_C1_VTi()[2];
                break;
            case SPEED_OF_MS:
                double vofset = 0;
                for (int i = 0; i <= workCenterset.size(); i++) {
                    if(i<workCenterset.size()){
                        double v= workCenterset.get(i).getHorse_C0_C1_VTi()[3];
                        vofset += Math.pow(v,2);
                    }else {
                        if (wk!=null){
                            double v= wk.getHorse_C0_C1_VTi()[3];
                            vofset += Math.pow(v,2);
                        }
                    }
                }
                value = Math.sqrt(vofset);
                break;
            case TOTAL_ENERGY_TUG:
                double totalv111=0;
                for (int i = 0; i <= workCenterset.size(); i++) {
                    if(i<workCenterset.size()){
                        double v= workCenterset.get(i).getHorse_C0_C1_VTi()[3];
                        totalv111 += Math.pow(v,2);
                    }else {
                        if (wk!=null){
                            double v= wk.getHorse_C0_C1_VTi()[3];
                            totalv111 += Math.pow(v,2);
                        }
                    }
                }
                totalv111 = Math.sqrt(totalv111);
                for (int i = 0; i < workCenterset.size(); i++) {
                    value += 2*op.getJob().getDisStoB()/
                            totalv111*workCenterset.get(i).getHorse_C0_C1_VTi()[2];
                }
                if(wk!=null){
                    value += 2*op.getJob().getDisStoB()/
                            totalv111*wk.getHorse_C0_C1_VTi()[2];
                }
                break;
            case TOTAL_ENERGY_IDE:
                for (int i = 0; i <workCenterset.size(); i++) {
                    WorkCenter wk0 =workCenterset.get(i);
                    double timeInstage1andstage22=systemState.getDSTS()[wk0.getMachinePortArea().get(0)-1]
                            [op.getObjectPortArea()-1]/13+systemState.getDSTE()
                            [op.getObjectPortArea()-1][wk0.getMachinePortArea().get(0)-1]/13;
                    value += timeInstage1andstage22*wk0.getHorse_C0_C1_VTi()[1];
                }
                if(wk!=null){
                    double timeInstage1andstage22=systemState.getDSTS()[wk.getMachinePortArea().get(0)-1]
                            [op.getObjectPortArea()-1]/13+systemState.getDSTE()
                            [op.getObjectPortArea()-1][wk.getMachinePortArea().get(0)-1]/13;
                    value += timeInstage1andstage22*wk.getHorse_C0_C1_VTi()[1];
                }
                break;
            case MAX_SPEED:
                value = wk.getMachineSpeed().get(0);
                break;
            case MACHINE_BUSYTIME:
                value = wk.getBusyTime();
                break;
            case OPERATION_RT_INS1:
                //max release time in stage 1
                // need to be calculated in both seq and rout
                double maxrt=0;
                for (int i = 0; i < op.getWorkCenterSet().size(); i++) {
                    double pts1 = systemState.getDSTS()[op.getWorkCenterSet().get(i).getMachinePortArea().get(0)-1] //s1
                            [op.getObjectPortArea()-1]/13;
                    double et1 = pts1 +op.getWorkCenterSet().get(i).getMachineReadyTime(0);
                    if(maxrt<et1)
                        maxrt=et1;
                }
                value=maxrt;
                break;
            case OPERATION_PT_INS1_OFWCS:
                double minst1=999999999;
                double maxet1=0;
                for (int i = 0; i <= workCenterset.size(); i++) {
                    if(i<workCenterset.size()){
                       if(minst1>workCenterset.get(i).getMachineReadyTime(0)) minst1 = workCenterset.get(i).getMachineReadyTime(0);
                       double finishingT1 = workCenterset.get(i).getMachineReadyTime(0)+ systemState.getDSTS()[op.getWorkCenterSet().get(i).getMachinePortArea().get(0)-1]
                               [op.getObjectPortArea()-1]/13;
                       if(maxet1<finishingT1) maxet1 = finishingT1;
                    }else {
                        if (wk!=null) {
                            if (minst1 > wk.getMachineReadyTime(0)) minst1 = wk.getMachineReadyTime(0);
                            double finishingT1 = wk.getMachineReadyTime(0) + systemState.getDSTS()[wk.getMachinePortArea().get(0) - 1]
                                    [op.getObjectPortArea() - 1] / 13;
                            if (maxet1 < finishingT1) maxet1 = finishingT1;
                        }
                    }
                }
                value = maxet1-minst1;
                break;
            case OPERATION_PT_INS2_OFWCS:
                double totalv1=0;
                for (int i = 0; i <= workCenterset.size(); i++) {
                   if(i<workCenterset.size()){
                       double v= workCenterset.get(i).getHorse_C0_C1_VTi()[3];
                       totalv1 += Math.pow(v,2);
                   }else {
                       if (wk!=null) {
                           double v = wk.getHorse_C0_C1_VTi()[3];
                           totalv1 += Math.pow(v, 2);
                       }
                   }
                }
                totalv1 = Math.sqrt(totalv1);
                value = 2*op.getJob().getDisStoB()/totalv1;
                break;
            case OPERATION_PT_INS3_OFWCS: //an average value , unlike in stage1, tugboats not need to waite others
                double avgPTS3=0;
                for (int i = 0; i <= workCenterset.size(); i++) {
                    if(i<workCenterset.size()) {
                        double machineTime = systemState.getDSTE()
                                [op.getObjectPortArea() - 1]
                                [workCenterset.get(i).getMachinePortArea().get(0) - 1] / 13;
                        avgPTS3 += machineTime;
                    }else {
                        if (wk!=null) {
                            double machineTime = systemState.getDSTE()
                                    [op.getObjectPortArea() - 1]
                                    [wk.getMachinePortArea().get(0) - 1] / 13;
                            avgPTS3 += machineTime;
                        }
                    }
                }
                if (wk!=null) avgPTS3/=(workCenterset.size()+1);
                else avgPTS3/=(workCenterset.size());
                value = avgPTS3;
                break;
            case OP_WAITING_TIME_WS:
                double maxtstIns2=0;
                for (int i = 0; i <= workCenterset.size(); i++) {
                    if(i<workCenterset.size()){
                        double finishingT1 = workCenterset.get(i).getMachineReadyTime(0)+ systemState.getDSTS()[op.getWorkCenterSet().get(i).getMachinePortArea().get(0)-1]
                                [op.getObjectPortArea()-1]/13;
                        if(maxtstIns2<finishingT1) maxtstIns2 = finishingT1;
                    }else {
                        if (wk!=null) {
                            double finishingT1 = wk.getMachineReadyTime(0) + systemState.getDSTS()[wk.getMachinePortArea().get(0) - 1]
                                    [op.getObjectPortArea() - 1] / 13;
                            if (maxtstIns2 < finishingT1) maxtstIns2 = finishingT1;
                        }
                    }
                }
                value = maxtstIns2-op.getReadyTime();
                break;
            case AVG_WIQ:
                double avge=0;
                for (int i = 0; i < workCenterset.size(); i++) {
                    avge+= workCenterset.get(i).getWorkInQueue();
                }
                if (wk!=null) {
                    avge+= wk.getWorkInQueue();
                    value = avge/(workCenterset.size()+1);
                }else
                    value = avge/workCenterset.size();
                break;
            case MAX_WIQ:
                double maxwiq=0;
                if (wk!=null) maxwiq=wk.getWorkInQueue();
                for (int i = 0; i < workCenterset.size(); i++) {
                    if(maxwiq<workCenterset.get(i).getWorkInQueue()){
                        maxwiq=workCenterset.get(i).getWorkInQueue();
                    }
                }
                value =maxwiq;
                break;
            case MIN_WIQ:
                double minwiq=999999999;
                if (wk!=null) minwiq=wk.getWorkInQueue();
                for (int i = 0; i < workCenterset.size(); i++) {
                    if(minwiq>workCenterset.get(i).getWorkInQueue()){
                        minwiq=workCenterset.get(i).getWorkInQueue();
                    }
                }
                value =minwiq;
                break;
            case OPERATION_NIQ_OFWCS:
                double totalNIQ_In_MachineSet = 0; //state
                for (int i = 0; i < workCenterset.size(); i++) {
                    totalNIQ_In_MachineSet+=workCenterset.get(i).getWorkInQueue();
                }
                value = totalNIQ_In_MachineSet;
                break;
            case MAX_MBT:
                double maxmbt = 0;
                if(wk!=null) maxmbt = wk.getBusyTime();
                for (int i = 0; i < workCenterset.size(); i++) {
                    double mbt0 = workCenterset.get(i).getBusyTime();
                    if(mbt0>maxmbt)
                        maxmbt=mbt0;
                }
                value=maxmbt;
                break;
            case MIN_MBT:
                double minmbt = 999999999;
                if(wk!=null) minmbt = wk.getBusyTime();
                for (int i = 0; i < workCenterset.size(); i++) {
                    double mbt0 = workCenterset.get(i).getBusyTime();
                    if(mbt0<minmbt)
                        minmbt=mbt0;
                }
                value=minmbt;
                break;
            case AVG_MBT:
                double avgmbt = 0;
                if(wk!=null) avgmbt = wk.getBusyTime();
                for (int i = 0; i < workCenterset.size(); i++) {
                    avgmbt += workCenterset.get(i).getBusyTime();

                }
                if(wk!=null) value=avgmbt/(workCenterset.size()+1);
                else  value=avgmbt/(workCenterset.size());
                break;
            default:
                System.err.println("Undefined attribute " + name);
                System.exit(1);
        }
        return value;
    }

    public double value(OperationOption op, WorkCenter workCenter, SystemState systemState
    		) {
        double value = -1;

        switch (this) {
            // Liu Feige
            case ENERGY_TUG:
                value = workCenter.getHorse_C0_C1_VTi()[1];
                break;
            case ENERGY_IDE:
                value = workCenter.getHorse_C0_C1_VTi()[2];
                break;
            case DISTANCE_STARTINGPOSITION_TO_BERTH:
                int s = op.getObjectPortArea()-1;
                int b = (int) op.getJob().getBerthArea()-1;
                value = op.getJob().getDisStoB();
                break;
            case DISTANCE_STARTINGPOSITION_TO_END:
                int s1 = op.getObjectPortArea()-1;
                int e = workCenter.getMachinePortArea().get(0)-1;
                value = systemState.getDSTE()[s1][e];
                break;
            case DISTANCE_STARTINGPOSITION_TO_START:
                int s2 = workCenter.getMachinePortArea().get(0)-1;
                int s3= op.getObjectPortArea()-1;
                value = systemState.getDSTS()[s2][s3];
                break;
            case PT_STARTINGPOSITION_TO_END:
                int s11 = op.getObjectPortArea()-1;
                int e1 = workCenter.getMachinePortArea().get(0)-1;
                value = systemState.getDSTE()[s11][e1]/13;
                break;
            case PT_STARTINGPOSITION_TO_START:
                int s21 = workCenter.getMachinePortArea().get(0)-1;
                int s31= op.getObjectPortArea()-1;
                value = systemState.getDSTS()[s21][s31]/13;
                break;
            case MIN_NUMBER_OF_SHIP:
                value = op.getNumNeedTug();
                break;
            case MIN_HORSEPOWER_OF_SHIP:
                value = op.getUpperHorsepower();
                break;
            case MAX_SPEED:
                value = workCenter.getMachineSpeed().get(0);
                break;
            case HORSE_POWER:
                value = workCenter.getMachineHorsepower().get(0);
                break;

            case PERCENTAGE_OF_TUG_TO_SHIP:
                value = workCenter.getHorse_C0_C1_VTi()[0]/(op.getUpperHorsepower()*op.getNumNeedTug());
                break;
            case PROC_TIME:
                //value for tugboat , LIUFEIGE
                double pt = op.getJob().getDisStoB()/workCenter.getHorse_C0_C1_VTi()[3];
                op.setProcTime(pt);
                value = op.getProcTime();
                break;
            case WORK_IN_QUEUE:
                value = workCenter.getWorkInQueue();
                break;
            case MACHINE_WAITING_TIME:
                value = systemState.getClockTime() - workCenter.getReadyTime();
                break;
            case OP_WAITING_TIME:
                value = systemState.getClockTime() - op.getReadyTime();
                //System.out.println("OWT" + value);
                break;
            case TIME_IN_SYSTEM:
                value = systemState.getClockTime() - op.getJob().getReleaseTime();
                break;
                //-------------------------------------
            case CURRENT_TIME:
                value = systemState.getClockTime();
                break;

            case NUM_OPS_IN_QUEUE:
                if (workCenter.getQueue()!=null) //LIUFEGE
                    value = workCenter.getQueue().size();
                else value=0;
                break;
            case MACHINE_READY_TIME:
                value = workCenter.getReadyTime();
                break;


            //modified by fzhang 31.5.2018  next processing time
            case NEXT_PROC_TIME:
                value = op.getNextProcTime();
                break;

            case LEAST_NEXT_PROC_TIME:
                value = systemState.getMinNextProcessTime(op.getOperation());
                break;
            case MAX_NEXT_PROC_TIME:
                value = systemState.getMaxNextProcessTime(op.getOperation());
                break;
            case MEDIAN_NEXT_PROC_TIME:
                value = systemState.getMedianNextProcessTime(op.getOperation());
                break;

            case OP_READY_TIME:
                value = systemState.getClockTime();
                break;

//            case NEXT_READY_TIME:
//                value = systemState.nextReadyTime(op);
//                break;
//            case NEXT_WAITING_TIME:
//                value = systemState.nextReadyTime(op) - systemState.getClockTime();
//                break;                                                                                                0
            case WORK_REMAINING:
                value = op.getWorkRemaining();
                break;
            case NUM_OPS_REMAINING:
                value = op.getNumOpsRemaining();
                break;
//            case WORK_IN_NEXT_QUEUE:
//                value = systemState.workInNextQueue(op);
//                break;
//            case NUM_OPS_IN_NEXT_QUEUE:
//                value = systemState.numOpsInNextQueue(op);
//                break;
//            case FLOW_DUE_DATE:
//                value = op.getFlowDueDate();
//                break;
            case RELATIVE_FLOW_DUE_DATE:
                value = op.getFlowDueDate() - systemState.getClockTime();
                break;
            case DUE_DATE:
                value = op.getJob().getDueDate();
                break;
            case RELATIVE_DUE_DATE:
                value = op.getJob().getDueDate() - systemState.getClockTime();
                break;
            case WEIGHT:
                value = op.getJob().getWeight();
                break;
            case ARRIVAL_TIME:
                value = op.getJob().getArrivalTime();
                break;

            case SLACK:
                value = op.getJob().getDueDate() - systemState.getClockTime() - op.getWorkRemaining();
                break;

            //==============================================================================
            case MACHINE_WORKLOAD_RATIO:
                value =  workCenter.getWorkInQueue()/systemState.getWorkInSystem();
                break;

             case MACHINE_NUM_OPERATION_RATIO:
                 value = workCenter.getNumOpsInQueue()/systemState.getNumOfOperationInSystem();
                 break;
             case NUM_CANDIATE_MACHINE:
                 value = op.getOperation().getOperationOptions().size();
                 break;
             case AVE_PROC_TIME_IN_QUEUE:
            	 if(workCenter.getWorkInQueue() == 0 ||workCenter.getNumOpsInQueue() == 0)
            		 value = 0;
            	 else
            		 value = workCenter.getWorkInQueue()/workCenter.getNumOpsInQueue();
                 break;

             //information of systemstate
             case AVE_WORKLOAD_IN_SYSTEME:
            	 value = systemState.getWorkInSystem()/systemState.getWorkCenters().size();
                 break;
             case AVE_NUM_OPERATION_IN_SYSTEME:
            	 value = systemState.getNumOfOperationInSystem()/systemState.getWorkCenters().size();
            	 break;

              //look-ahead, work in next queue (WINQ) and number of operations in queue (NOINQ)
             case LEAST_WORK_IN_NEXT_QUEUE:
                 value = systemState.getMinWorkInNextQueue(op.getOperation());
                 break;
             case MAX_WORK_IN_NEXT_QUEUE:
            	 value = systemState.getMaxWorkInNextQueue(op.getOperation());
                 break;
             case AVE_WORK_IN_NEXT_QUEUE:
            	 value = systemState.getAvgWorkInNextQueue(op.getOperation());
                 break;

             case LEAST_NUM_OPERATIOM_IN_NEXT_QUEUE:
                 value = systemState.getMinNumOperationInNextQueue(op.getOperation());
                 break;
             case MAX_NUM_OPERATIOM_IN_NEXT_QUEUE:
            	 value = systemState.getMaxNumOperationInNextQueue(op.getOperation());
                 break;
             case AVE_NUM_OPERATIOM_IN_NEXT_QUEUE:
            	 value = systemState.getAveNumOperationInNextQueue(op.getOperation());
                 break;

             case DEVIATION_OF_JOB_IN_QUEUE:
            	 value = workCenter.getMinProcessTimeInQueue()/workCenter.getMaxProcessTimeInQueue();
            	 break;

             //fzhang 19.7.2018  add information of current system
             case TOTAL_WORK_IN_SYSTEM:
            	 value = systemState.getWorkInSystem();
            	 break;
             case TOTAL_OPERATION_IN_SYSTEM:
            	 value = systemState.getNumOfOperationInSystem();
            	 break;
            //fzhang 19.7.2018  add history information
             case BUSY_TIME:
            	 value = workCenter.getBusyTime();
            	 break;
             case AVERAGE_BUSY_TIME:
            	 value = systemState.getTotalBusyTime()/systemState.getWorkCenters().size();
            	 break;
             case NUM_COMPLETED_JOB:
            	 value = systemState.getJobsCompleted().size();
            	 break;
            case WAITING_TIME:
                value = op.getWAITING_TIME();
                break;

           default:
                System.err.println("Undefined attribute " + name);
                System.exit(1);
        }

        return value;
    }

    public static double valueOfString(String attribute, OperationOption op, WorkCenter workCenter,
                                       SystemState systemState,
                                       List<JobShopAttribute> ignoredAttributes) {
        JobShopAttribute a = get(attribute);
        if (a == null) {
            if (NumberUtils.isNumber(attribute)) {
                return Double.valueOf(attribute);
            } else {
                System.err.println(attribute + " is neither a defined attribute nor a number.");
                System.exit(1);
            }
        }

        if (ignoredAttributes.contains(a)) {
            return 1.0;
        } else {
        	  return a.value(op, workCenter, systemState);
        }
    }

    //LIUFEIGE for A1
    public static JobShopAttribute[] relativeAttributesForTugboat() {
        return new JobShopAttribute[]{
                //relative
                JobShopAttribute.NUM_OPS_IN_QUEUE,//NIQ !
                JobShopAttribute.WORK_IN_QUEUE, //WIQ  !
                JobShopAttribute.MACHINE_WAITING_TIME, //MWT  !
                JobShopAttribute.TIME_IN_SYSTEM,  //relative TIS  !
                JobShopAttribute.DUE_DATE,      //due date;

                //PT
                JobShopAttribute.BERTH_PROC_TIME, //BPT
                JobShopAttribute.SAILING_PROC_TIME_SS, //SPTS
                JobShopAttribute.SAILING_PROC_TIME_SE, //SPTE
                JobShopAttribute.MAX_SPEED, //MS  @
                JobShopAttribute.ENERGY_TUG, //ET  ! EC in the machine(operation)?
                JobShopAttribute.ENERGY_IDE, //EI  @ EI in the machine(operation)?
                JobShopAttribute.MACHINE_BUSYTIME, //MBT  // for wk one machine

                //PT for machines : the state of current mahicne set
                JobShopAttribute.OPERATION_PT_INS1_OFWCS, //PTOMS //the processing time assumed of the machine set in stage 1
                JobShopAttribute.OPERATION_PT_INS2_OFWCS, //OPTS2OMS  //the processing time assumed of the machine set in stage 2
                JobShopAttribute.OPERATION_PT_INS3_OFWCS, //OPTS3OMS //the avg processing time of the machine set in stage 3
                JobShopAttribute.OP_WAITING_TIME_WS, //OWTOMS // ship waiting time, ship waiting for berthing, = OPERATION_RT_INS1 - arrival time
                JobShopAttribute.TOTAL_ENERGY_IDE,
                JobShopAttribute.TOTAL_ENERGY_TUG,
                JobShopAttribute.MAX_NUM_OPS_IN_QUEUE,
                JobShopAttribute.MIN_NUM_OPS_IN_QUEUE,
                JobShopAttribute.AVG_NUM_OPS_IN_QUEUE,
                JobShopAttribute.MAX_WIQ,
                JobShopAttribute.MIN_WIQ,
                JobShopAttribute.AVG_WIQ,
                JobShopAttribute.AVG_MWT,
                JobShopAttribute.MAX_MWT,
                JobShopAttribute.MIN_MWT,
                JobShopAttribute.MIN_MBT,
                JobShopAttribute.MAX_MBT,
                JobShopAttribute.AVG_MBT,
                JobShopAttribute.SPEED_OF_MS,

        };
    }

    //FOR tugboat,A2 for machine set
    public static JobShopAttribute[] relativeAttributesForTugboatSet() {
        return new JobShopAttribute[]{
//                JobShopAttribute.WEIGHT,
                JobShopAttribute.TIME_IN_SYSTEM,  //relative TIS  !
                JobShopAttribute.DUE_DATE,
                JobShopAttribute.OPERATION_PT_INS1_OFWCS, //PTOMS //the processing time assumed of the machine set in stage 1
                JobShopAttribute.OPERATION_PT_INS2_OFWCS, //OPTS2OMS  //the processing time assumed of the machine set in stage 2
                JobShopAttribute.OPERATION_PT_INS3_OFWCS, //OPTS3OMS //the avg processing time of the machine set in stage 3
                JobShopAttribute.OP_WAITING_TIME_WS, //OWTOMS // ship waiting time, ship waiting for berthing, = OPERATION_RT_INS1 - arrival time
                JobShopAttribute.TOTAL_ENERGY_IDE,
                JobShopAttribute.TOTAL_ENERGY_TUG,
                JobShopAttribute.MAX_NUM_OPS_IN_QUEUE,
                JobShopAttribute.MIN_NUM_OPS_IN_QUEUE,
                JobShopAttribute.AVG_NUM_OPS_IN_QUEUE,
                JobShopAttribute.MAX_WIQ,
                JobShopAttribute.MIN_WIQ,
                JobShopAttribute.AVG_WIQ,
                JobShopAttribute.AVG_MWT,
                JobShopAttribute.MAX_MWT,
                JobShopAttribute.MIN_MWT,
                JobShopAttribute.MIN_MBT,
                JobShopAttribute.MAX_MBT,
                JobShopAttribute.AVG_MBT,
                JobShopAttribute.SPEED_OF_MS,
        };
    }

    public static JobShopAttribute[] relativeAttributesForSingleTB() {
        return new JobShopAttribute[]{
//                JobShopAttribute.WEIGHT,
                //relative
                JobShopAttribute.NUM_OPS_IN_QUEUE,//NIQ !
                JobShopAttribute.WORK_IN_QUEUE, //WIQ  !
                JobShopAttribute.MACHINE_WAITING_TIME, //MWT  !
                JobShopAttribute.TIME_IN_SYSTEM,  //relative TIS  !
                JobShopAttribute.DUE_DATE,      //due date;

                //PT
                JobShopAttribute.BERTH_PROC_TIME, //BPT
                JobShopAttribute.SAILING_PROC_TIME_SS, //SPTS
                JobShopAttribute.SAILING_PROC_TIME_SE, //SPTE
                JobShopAttribute.MAX_SPEED, //MS  @
                JobShopAttribute.ENERGY_TUG, //ET  ! EC in the machine(operation)?
                JobShopAttribute.ENERGY_IDE, //EI  @ EI in the machine(operation)?
                JobShopAttribute.MACHINE_BUSYTIME, //MBT  // for wk one machine
        };
    }

    /**
     * Return the basic attributes.
     * @return the basic attributes.
     */
    public static JobShopAttribute[] basicAttributes() {
        return new JobShopAttribute[]{
                JobShopAttribute.CURRENT_TIME,
                JobShopAttribute.NUM_OPS_IN_QUEUE,
                JobShopAttribute.WORK_IN_QUEUE,
                JobShopAttribute.MACHINE_READY_TIME,
                JobShopAttribute.PROC_TIME,
                JobShopAttribute.NEXT_PROC_TIME,
                JobShopAttribute.OP_READY_TIME,
                //JobShopAttribute.NEXT_READY_TIME,
                JobShopAttribute.WORK_REMAINING,
                JobShopAttribute.NUM_OPS_REMAINING,
                //JobShopAttribute.WORK_IN_NEXT_QUEUE,
                //JobShopAttribute.NUM_OPS_IN_NEXT_QUEUE,
                //JobShopAttribute.FLOW_DUE_DATE,
                JobShopAttribute.DUE_DATE,
                JobShopAttribute.WEIGHT,

                JobShopAttribute.ARRIVAL_TIME,
                JobShopAttribute.SLACK
        };
    }

    /**
     * The attributes relative to the current time.
     * @return the relative attributes.
     */
    //for flexible JSSP
    //fzhang 19.7.2018 for flexible, the next processing time do not know, because we do not know the next operation will
    //be allocated in which machine:  baseline
    public static JobShopAttribute[] relativeAttributes() {
        return new JobShopAttribute[]{
                JobShopAttribute.NUM_OPS_IN_QUEUE,
                JobShopAttribute.WORK_IN_QUEUE,
                JobShopAttribute.MACHINE_WAITING_TIME,
                JobShopAttribute.PROC_TIME, //£¿
                JobShopAttribute.NEXT_PROC_TIME,
                JobShopAttribute.OP_WAITING_TIME,
                JobShopAttribute.WORK_REMAINING,
                JobShopAttribute.NUM_OPS_REMAINING,
                JobShopAttribute.WEIGHT,
                JobShopAttribute.TIME_IN_SYSTEM,
        };
    }


    //fzhang 19.7.2018 consider other current attributes: baseline
    public static JobShopAttribute[] relativeCurrentAttributes() {
        return new JobShopAttribute[]{
                JobShopAttribute.NUM_OPS_IN_QUEUE,
                JobShopAttribute.WORK_IN_QUEUE,
                JobShopAttribute.MACHINE_WAITING_TIME,
                JobShopAttribute.PROC_TIME,
                //JobShopAttribute.NEXT_PROC_TIME,
                JobShopAttribute.OP_WAITING_TIME,
                JobShopAttribute.WORK_REMAINING,
                JobShopAttribute.NUM_OPS_REMAINING,
                JobShopAttribute.WEIGHT,
                JobShopAttribute.TIME_IN_SYSTEM,
                
                //new attribute
                JobShopAttribute.NUM_CANDIATE_MACHINE,  //4
                JobShopAttribute.MACHINE_WORKLOAD_RATIO, //5
                JobShopAttribute.MACHINE_NUM_OPERATION_RATIO,//6

                JobShopAttribute.AVE_PROC_TIME_IN_QUEUE, //7

                JobShopAttribute.AVE_WORKLOAD_IN_SYSTEME, //8
                JobShopAttribute.AVE_NUM_OPERATION_IN_SYSTEME, //9
                JobShopAttribute.DEVIATION_OF_JOB_IN_QUEUE, //16
                
                JobShopAttribute.TOTAL_WORK_IN_SYSTEM,
                JobShopAttribute.TOTAL_OPERATION_IN_SYSTEM,             
        };
    }
    
    //fzhang 19.7.2018 consider other current attributes: baseline
    public static JobShopAttribute[] relativeFutureAttributes() {
        return new JobShopAttribute[]{
                JobShopAttribute.NUM_OPS_IN_QUEUE,
                JobShopAttribute.WORK_IN_QUEUE,
                JobShopAttribute.MACHINE_WAITING_TIME,
                JobShopAttribute.PROC_TIME,
                //JobShopAttribute.NEXT_PROC_TIME,
                JobShopAttribute.OP_WAITING_TIME,
                JobShopAttribute.WORK_REMAINING,
                JobShopAttribute.NUM_OPS_REMAINING,
                JobShopAttribute.WEIGHT,
                JobShopAttribute.TIME_IN_SYSTEM,
                
                //new terminals
                //next processing time   fzhang 31.5.2018 for flexible
                JobShopAttribute.LEAST_NEXT_PROC_TIME,  //1
                JobShopAttribute.MAX_NEXT_PROC_TIME,    //2
                JobShopAttribute.MEDIAN_NEXT_PROC_TIME, //3
                
                //Work in next queue
                JobShopAttribute.LEAST_WORK_IN_NEXT_QUEUE, //10
                JobShopAttribute.MAX_WORK_IN_NEXT_QUEUE, //11
                JobShopAttribute.AVE_WORK_IN_NEXT_QUEUE, //12

                //number of operations in next queue
                JobShopAttribute.LEAST_NUM_OPERATIOM_IN_NEXT_QUEUE, //13
                JobShopAttribute.MAX_NUM_OPERATIOM_IN_NEXT_QUEUE, //14
                JobShopAttribute.AVE_NUM_OPERATIOM_IN_NEXT_QUEUE, //15
        };
    }
    
    //fzhang 19.7.2018 consider other current attributes: baseline
    public static JobShopAttribute[] relativeHistoryAttributes() {
        return new JobShopAttribute[]{
                JobShopAttribute.NUM_OPS_IN_QUEUE,
                JobShopAttribute.WORK_IN_QUEUE,
                JobShopAttribute.MACHINE_WAITING_TIME,
                JobShopAttribute.PROC_TIME,
                //JobShopAttribute.NEXT_PROC_TIME,
                JobShopAttribute.OP_WAITING_TIME,
                JobShopAttribute.WORK_REMAINING,
                JobShopAttribute.NUM_OPS_REMAINING,
                JobShopAttribute.WEIGHT,
                JobShopAttribute.TIME_IN_SYSTEM,
                
                //new terminals
                JobShopAttribute.BUSY_TIME,
                JobShopAttribute.AVERAGE_BUSY_TIME,
                JobShopAttribute.NUM_COMPLETED_JOB,
               
        };
    }
    
    //fzhang  ignore weigth in non-weight objective, finally found that the result has no obvious difference.
    public static JobShopAttribute[] relativeWithoutWeightAttributes() {
        return new JobShopAttribute[]{
                JobShopAttribute.NUM_OPS_IN_QUEUE,
                JobShopAttribute.WORK_IN_QUEUE,
                JobShopAttribute.MACHINE_WAITING_TIME,
                JobShopAttribute.PROC_TIME,
                JobShopAttribute.NEXT_PROC_TIME,
                JobShopAttribute.OP_WAITING_TIME,
                JobShopAttribute.WORK_REMAINING,
                JobShopAttribute.NUM_OPS_REMAINING,
                JobShopAttribute.TIME_IN_SYSTEM,
        };
    }
    
    //modified by fzhang  24.5.2018  add some terminals for flexible job shop scheduling: especially terminals related to the system
    public static JobShopAttribute[] systemstateAttributes() {
        return new JobShopAttribute[]{
        		
                JobShopAttribute.NUM_OPS_IN_QUEUE,
                JobShopAttribute.WORK_IN_QUEUE,
                JobShopAttribute.MACHINE_WAITING_TIME,
                JobShopAttribute.PROC_TIME,
                //JobShopAttribute.NEXT_PROC_TIME,

                //next processing time   fzhang 31.5.2018 for flexible
                JobShopAttribute.LEAST_NEXT_PROC_TIME,  //1
                JobShopAttribute.MAX_NEXT_PROC_TIME,    //2
                JobShopAttribute.MEDIAN_NEXT_PROC_TIME, //3
                //-----------------------------------------------------

                JobShopAttribute.OP_WAITING_TIME,
                JobShopAttribute.WORK_REMAINING,
                JobShopAttribute.NUM_OPS_REMAINING,
                JobShopAttribute.WEIGHT,
                JobShopAttribute.TIME_IN_SYSTEM,
                //modified by fzhang 26.5.2018
                //new terminals
                JobShopAttribute.NUM_CANDIATE_MACHINE,  //4
                JobShopAttribute.MACHINE_WORKLOAD_RATIO, //5
                JobShopAttribute.MACHINE_NUM_OPERATION_RATIO,//6

                JobShopAttribute.AVE_PROC_TIME_IN_QUEUE, //7

                JobShopAttribute.AVE_WORKLOAD_IN_SYSTEME, //8
                JobShopAttribute.AVE_NUM_OPERATION_IN_SYSTEME, //9

                //Work in next queue
                JobShopAttribute.LEAST_WORK_IN_NEXT_QUEUE, //10
                JobShopAttribute.MAX_WORK_IN_NEXT_QUEUE, //11
                JobShopAttribute.AVE_WORK_IN_NEXT_QUEUE, //12

                //number of operations in next queue
                JobShopAttribute.LEAST_NUM_OPERATIOM_IN_NEXT_QUEUE, //13
                JobShopAttribute.MAX_NUM_OPERATIOM_IN_NEXT_QUEUE, //14
                JobShopAttribute.AVE_NUM_OPERATIOM_IN_NEXT_QUEUE, //15

                JobShopAttribute.DEVIATION_OF_JOB_IN_QUEUE, //16
        };
    }

    /**
     * The attributes for minimising mean weighted tardiness (Su's paper).
     * @return the attributes.
     */
    public static JobShopAttribute[] mwtAttributes() {
        return new JobShopAttribute[]{
                JobShopAttribute.TIME_IN_SYSTEM,
                JobShopAttribute.OP_WAITING_TIME,
                JobShopAttribute.NUM_OPS_REMAINING,
                JobShopAttribute.WORK_REMAINING,
                JobShopAttribute.PROC_TIME,
                JobShopAttribute.DUE_DATE,
                JobShopAttribute.SLACK,
                JobShopAttribute.WEIGHT,
                JobShopAttribute.NEXT_PROC_TIME,
                //JobShopAttribute.WORK_IN_NEXT_QUEUE
        };
    }

    public static JobShopAttribute[] countAttributes() {
        return new JobShopAttribute[] {
                JobShopAttribute.NUM_OPS_IN_QUEUE,
                JobShopAttribute.NUM_OPS_REMAINING,
                //JobShopAttribute.NUM_OPS_IN_NEXT_QUEUE
        };
    }

    public static JobShopAttribute[] weightAttributes() {
        return new JobShopAttribute[] {
                JobShopAttribute.WEIGHT
        };
    }

    public static JobShopAttribute[] timeAttributes() {
        return new JobShopAttribute[] {
                JobShopAttribute.MACHINE_WAITING_TIME,
                JobShopAttribute.OP_WAITING_TIME,
                //JobShopAttribute.NEXT_READY_TIME,
                //JobShopAttribute.FLOW_DUE_DATE,
                JobShopAttribute.DUE_DATE,

                JobShopAttribute.WORK_IN_QUEUE,
                JobShopAttribute.PROC_TIME,
                JobShopAttribute.NEXT_PROC_TIME,
                JobShopAttribute.WORK_REMAINING,
                //JobShopAttribute.WORK_IN_NEXT_QUEUE,

                JobShopAttribute.TIME_IN_SYSTEM,
                JobShopAttribute.SLACK
        };
    }
}
