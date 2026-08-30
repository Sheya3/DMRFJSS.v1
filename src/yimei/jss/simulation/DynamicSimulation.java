package yimei.jss.simulation;

import org.apache.commons.math3.random.RandomDataGenerator;
import yimei.jss.jobshop.*;
import yimei.jss.simulation.state.SystemState;
import yimei.util.random.*;
import yimei.jss.rule.AbstractRule;
import yimei.jss.simulation.event.AbstractEvent;
import yimei.jss.simulation.event.JobArrivalEvent;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * The dynamic simulation -- discrete event simulation
 *
 * Created by yimei on 22/09/16.
 */
public class DynamicSimulation extends Simulation {

    public final static int SEED_ROTATION = 10000;

    private long seed;
    private RandomDataGenerator randomDataGenerator;

    private final int minNumOperations;
    private final int maxNumOperations;
    private final double utilLevel;
    private final double dueDateFactor;
    private final boolean revisit;

    private AbstractIntegerSampler numOperationsSampler;
    //modified by fzhang, 17.04.2018  in order to set options from 2 to 10
    //private AbstractIntegerSampler numOptionsSampler;

    private AbstractRealSampler procTimeSampler;
    private AbstractRealSampler interArrivalTimeSampler;
    private AbstractRealSampler jobWeightSampler;

    //LIUFEIGE For tugboat
    private AbstractRealSampler interArrivalTimeForTimewindowSampler = new UniformSampler(1, 5);
    private AbstractIntegerSampler objectPortSampler;

    private AbstractIntegerSampler objectPortSampler1;

    private AbstractRealSampler shipLengthSampler;
    private AbstractIntegerSampler objectBerthSampler;

    private DynamicSimulation(long seed,
                              AbstractRule sequencingRule,
                              AbstractRule routingRule,
                              int numWorkCenters,
                              int numJobsRecorded,
                              int warmupJobs,
                              int minNumOperations,
                              int maxNumOperations,
                              double utilLevel,
                              double dueDateFactor,
                              boolean revisit,
                              AbstractIntegerSampler numOperationsSampler,
                              //modified by fzhang, 17.04.2018
                              //AbstractIntegerSampler numOptionsSampler,
                              AbstractRealSampler procTimeSampler,
                              AbstractRealSampler interArrivalTimeSampler,
                              AbstractRealSampler jobWeightSampler) {
        super(sequencingRule, routingRule, numWorkCenters, numJobsRecorded, warmupJobs);

        this.seed = seed;
        this.randomDataGenerator = new RandomDataGenerator();
        this.randomDataGenerator.reSeed(seed);

        this.minNumOperations = minNumOperations;
        this.maxNumOperations = maxNumOperations;
        this.utilLevel = utilLevel;
        this.dueDateFactor = dueDateFactor;
        this.revisit = revisit;

        this.numOperationsSampler = numOperationsSampler;
        //modified by fzhang 17.04.2018
        //this.numOptionsSampler = numOptionsSampler;

        this.procTimeSampler = procTimeSampler;
        this.interArrivalTimeSampler = interArrivalTimeSampler;
        this.jobWeightSampler = jobWeightSampler;

        setInterArrivalTimeSamplerMean();

        // Create the work centers, with empty queue and ready to go initially.
        for (int i = 0; i < numWorkCenters; i++) {
            systemState.addWorkCenter(new WorkCenter(i));
        }

        setup();
    }


    public DynamicSimulation(long seed,
                             AbstractRule sequencingRule,
                             AbstractRule routingRule,
                             int numWorkCenters,
                             int numJobsRecorded,
                             int warmupJobs,
                             int minNumOperations,
                             int maxNumOperations,
                             double utilLevel,
                             double dueDateFactor,
                             boolean revisit) {
        this(seed, sequencingRule, routingRule, numWorkCenters, numJobsRecorded, warmupJobs,
                minNumOperations, maxNumOperations, utilLevel, dueDateFactor, revisit,
                //here, specifiy the range of UniformIntegerSample to (1,10)
                new UniformIntegerSampler(minNumOperations, maxNumOperations), //these two values will be changed during the evolutionary process, because different models are called.
                //the surrogate model will set them to 1 and 5, but the original model will set them to 1 and 10
                //when calculate the phenotype, in this code, full simulation is used, they will be set to 10 and 10.
                //modified by fzhang 17.04.2018
                //new UniformIntegerSampler(1, numWorkCenters), //in this way, whether add this parameter or not is the same
                //new UniformIntegerSampler(1, 5), //one operation only can be processed at 5 machines

                new UniformSampler(1, 99),
                new ExponentialSampler(),
                new TwoSixTwoSampler());
    }

    //LIUFEIGE for tugboat
    private DynamicSimulation(long seed,
                              AbstractRule sequencingRule,
                              AbstractRule routingRule,
                              int numWorkCenters,
                              int numJobsRecorded,
                              int warmupJobs,
                              int minNumOperations,
                              int maxNumOperations,
                              double utilLevel,
                              double dueDateFactor,
                              boolean revisit,
                              String typeOfQuestion,
                              AbstractIntegerSampler numOperationsSampler,

                              AbstractRealSampler procTimeSampler,
                              // For tugboat
                              AbstractRealSampler shipLengthSampler,
                              AbstractIntegerSampler objectPortSampler,
                              AbstractIntegerSampler objectPortSampler1,
                              AbstractIntegerSampler objectBerthSampler,

                              AbstractRealSampler interArrivalTimeSampler,
                              AbstractRealSampler jobWeightSampler
                            ) {
        super(sequencingRule, routingRule, numWorkCenters, numJobsRecorded, warmupJobs);

        this.seed=seed;
        this.randomDataGenerator = new RandomDataGenerator();
        this.randomDataGenerator.reSeed(seed);

        this.minNumOperations = minNumOperations;
        this.maxNumOperations = maxNumOperations;
        this.utilLevel = utilLevel;
        this.dueDateFactor = dueDateFactor;
        this.revisit = revisit;

        this.numOperationsSampler = numOperationsSampler;
        //modified by fzhang 17.04.2018
        //this.numOptionsSampler = numOptionsSampler;
        this.interArrivalTimeSampler = interArrivalTimeSampler;
        this.jobWeightSampler = jobWeightSampler;
        this.procTimeSampler = procTimeSampler;
        setInterArrivalTimeSamplerMean();
//        setInterArrivalTimeSamplerForTimewindow(6);
        //LUIFEIGE
        this.shipLengthSampler = shipLengthSampler;
        this.objectPortSampler = objectPortSampler;
        this.objectPortSampler1 = objectPortSampler1;
        this.objectBerthSampler= objectBerthSampler;

        //distance initial
        //---------------------Instance------------------LIUFEIGE: edit machines---------------------------
        // Create the work centers, with empty queue and ready to go initially.
        // Plan1: Instance 1 according to the machineNumber
//        for (int i = 0; i < numWorkCenters; i++) {
////            systemState.addWorkCenter(new WorkCenter(i));
//            //LIUFEIGE
//            //for tugboat to get workcenterSet
//            //initial the machine with its machineSpeed,machineHorsepower,machinePortArea
//            List<Double> machineHorsepower = new LinkedList<>();
//            int level = new UniformIntegerSampler(1,7).next(randomDataGenerator)-1;
//            machineHorsepower.add(Machine_Horse_C0_C1_VTi[level][0]);
//
//            List<Integer> machinePortArea = new LinkedList<>();
//            machinePortArea.add(new UniformIntegerSampler(1,4).next(randomDataGenerator));
//
//            LinkedList<Double> machineSpeed = new LinkedList<>(); machineSpeed.add(Machine_Horse_C0_C1_VTi[level][3]);
//            WorkCenter workCenter = new WorkCenter(i,1,
//                    machineSpeed,machineHorsepower,machinePortArea);
//
//            workCenter.setHorse_C0_C1_VTi(Machine_Horse_C0_C1_VTi[level]);
//            workCenter.setmachineTimeC0(0,0);
//            workCenter.setmachineTimeC1(0,0);
//            systemState.addWorkCenter(workCenter);
//        }

       /* // Plan 2: A Bi-objective green tugboat scheduling problem with the tidal port time windows
        for (int i = 1; i < 5; i++) {
            if(i==1){
                int[] level = {1,4,0,0,0,0,0};
                generateWorkcenter(level,i);
            }
            else if (i==2){
                int[] level = {0,3,1,2,0,0,0};
                generateWorkcenter(level,i);
            }
            else if (i==3){
                int[] level = {0,0,3,1,0,0,0};
                generateWorkcenter(level,i);
            }else if (i==4){
                int[] level = {0,0,5,2,1,1,1};
                generateWorkcenter(level,i);
            }else {
                System.out.println("error");
            }
        }*/

   /*     for (int i = 1; i < 5; i++) { //run 1,run 2
            if(i==1){
                int[] level = {1,4,0,0,2,1,2};
                generateWorkcenter(level,i);
            }
            else if (i==2){
                int[] level = {0,3,1,2,2,0,1};
                generateWorkcenter(level,i);
            }
            else if (i==3){
                int[] level = {0,0,5,1,2,2,1};
                generateWorkcenter(level,i);
            }else if (i==4){
                int[] level = {0,0,5,2,2,1,1};
                generateWorkcenter(level,i);
            }else {
                System.out.println("error");
            }
        }*/

        for (int i = 1; i < 5; i++) { //zhengtai,run6 in grid
            if(i==1){
                int[] level = {1,4,0,5,3,1,2};
                generateWorkcenter(level,i);
            }
            else if (i==2){
                int[] level = {0,3,1,2,2,0,1};
                generateWorkcenter(level,i);
            }
            else if (i==3){
                int[] level = {0,0,5,4,2,2,1};
                generateWorkcenter(level,i);
            }else if (i==4){
                int[] level = {0,0,6,3,5,1,2};
                generateWorkcenter(level,i);
            }else {
                System.out.println("error");
            }
        }
        setup();
    }

    public void generateWorkcenter(int[] level,int i){
        int id=0;
        for (int j = 0; j < level.length; j++) {
            if(level[j]!=0){
                for (int k = 0; k < level[j]; k++) {
                    List<Double> machineHorsepower = new LinkedList<>();
                    machineHorsepower.add(Machine_Horse_C0_C1_VTi[j][0]);

                    List<Integer> machinePortArea = new LinkedList<>();
                    machinePortArea.add(i);

                    LinkedList<Double> machineSpeed = new LinkedList<>(); machineSpeed.add(Machine_Horse_C0_C1_VTi[j][3]);
                    WorkCenter workCenter = new WorkCenter(id,1,machineSpeed,machineHorsepower,machinePortArea);

                    workCenter.setHorse_C0_C1_VTi(Machine_Horse_C0_C1_VTi[j]);
                    workCenter.setmachineTimeC0(0,0);
                    workCenter.setmachineTimeC1(0,0);

                    systemState.addWorkCenter(workCenter);
                    id++;
                }
            }
        }
    }

    //for tugboat deviation
    public DynamicSimulation(long seed,
                             AbstractRule sequencingRule,
                             AbstractRule routingRule,
                             int numWorkCenters,
                             int numJobsRecorded,
                             int warmupJobs,
                             int minNumOperations,
                             int maxNumOperations,
                             double utilLevel,
                             double dueDateFactor,
                             boolean revisit,
                             String typeOfQuestion,
                             double deviation) {
        this(seed,sequencingRule, routingRule, numWorkCenters, numJobsRecorded, warmupJobs,
                minNumOperations, maxNumOperations, utilLevel, dueDateFactor, revisit, typeOfQuestion,
                //here, specifiy the range of UniformIntegerSample to (1,10)
                new UniformIntegerSampler(minNumOperations, maxNumOperations), //these two values will be changed during the evolutionary process, because different models are called.
                //the surrogate model will set them to 1 and 5, but the original model will set them to 1 and 10
                //when calculate the phenotype, in this code, full simulation is used, they will be set to 10 and 10.
                //modified by fzhang 17.04.2018
                //new UniformIntegerSampler(1, numWorkCenters), //in this way, whether add this parameter or not is the same
                //new UniformIntegerSampler(1, 5), //one operation only can be processed at 5 machines

                new UniformSampler(0.40, 12.13), //pt
//                new UniformSampler(30,90), //mode2 : pt
                new NormalSampler(135,deviation),  //AbstractRealSampler shipLengthSampler,
//                new GammaSampler(2,0.85),  //mode2 AbstractRealSampler shipLengthSampler,
                new UniformIntegerSampler(1,4), //  AbstractIntegerSampler objectPortSampler,
                new UniformIntegerSampler(1,4),//  AbstractIntegerSampler objectPortSampler1,
                new UniformIntegerSampler(1,99), // AbstractIntegerSampler objectBerthSampler,

                new ExponentialSampler(), // AbstractRealSampler interArrivalTimeSampler,
                new TwoSixTwoSampler()); // AbstractRealSampler jobWeightSampler
//        System.out.println("seed: " + seed);
    }

    //FEIGE LIU for tugboat
    public DynamicSimulation(long seed,
                             AbstractRule sequencingRule,
                             AbstractRule routingRule,
                             int numWorkCenters,
                             int numJobsRecorded,
                             int warmupJobs,
                             int minNumOperations,
                             int maxNumOperations,
                             double utilLevel,
                             double dueDateFactor,
                             boolean revisit,
                             String typeOfQuestion) {
        this(seed,sequencingRule, routingRule, numWorkCenters, numJobsRecorded, warmupJobs,
                minNumOperations, maxNumOperations, utilLevel, dueDateFactor, revisit, typeOfQuestion,
                //here, specifiy the range of UniformIntegerSample to (1,10)
                new UniformIntegerSampler(minNumOperations, maxNumOperations), //these two values will be changed during the evolutionary process, because different models are called.
                //the surrogate model will set them to 1 and 5, but the original model will set them to 1 and 10
                //when calculate the phenotype, in this code, full simulation is used, they will be set to 10 and 10.
                //modified by fzhang 17.04.2018
                //new UniformIntegerSampler(1, numWorkCenters), //in this way, whether add this parameter or not is the same
                //new UniformIntegerSampler(1, 5), //one operation only can be processed at 5 machines

                new UniformSampler(1, 10), //pt
//                new UniformSampler(1, 10), //mode2 pt
                new UniformSampler(80,350),  //AbstractRealSampler shipLengthSampler,
//                new NormalSampler(135,80),  //AbstractRealSampler shipLengthSampler,

                new UniformIntegerSampler(1,4), //  AbstractIntegerSampler objectPortSampler,
                new UniformIntegerSampler(1,4),//  AbstractIntegerSampler objectPortSampler1,
                new UniformIntegerSampler(1,99), // AbstractIntegerSampler objectBerthSampler,

                new ExponentialSampler(), // AbstractRealSampler interArrivalTimeSampler,
                new TwoSixTwoSampler()); // AbstractRealSampler jobWeightSampler
    }


    public int getNumWorkCenters() {
        return numWorkCenters;
    }

    public int getNumJobsRecorded() {
        return numJobsRecorded;
    }

    public int getWarmupJobs() {
        return warmupJobs;
    }

    public int getMinNumOperations() {
        return minNumOperations;
    }

    public int getMaxNumOperations() {
        return maxNumOperations;
    }

    public double getUtilLevel() {
        return utilLevel;
    }

    public double getDueDateFactor() {
        return dueDateFactor;
    }

    public boolean isRevisit() {
        return revisit;
    }

    public RandomDataGenerator getRandomDataGenerator() {
        return randomDataGenerator;
    }

    public AbstractIntegerSampler getNumOperationsSampler() {
        return numOperationsSampler;
    }

    public AbstractRealSampler getProcTimeSampler() {
        return procTimeSampler;
    }

    public AbstractRealSampler getInterArrivalTimeSampler() {
        return interArrivalTimeSampler;
    }

    public AbstractRealSampler getJobWeightSampler() {
        return jobWeightSampler;
    }

    @Override
    public void setup() {
        numJobsArrived = 0;
        throughput = 0;
        generateJob();
    }

    @Override
    public void resetState() {
        systemState.reset(); //
        eventQueue.clear();
        setup();
    }

    @Override
    public void reset() {
        reset(seed);
    }

    public void reset(long seed) {
        reseed(seed);
        resetState();
    }

    public void reseed(long seed) {
        this.seed = seed;
        randomDataGenerator.reSeed(seed);
    }

    @Override
    public void rotateSeed() {//this is use for changing seed value in next generation
    	//this only relates to generation
        System.out.println("seed: "+ seed);
        seed += SEED_ROTATION;
        reset();
//        System.out.println(seed);//when seed=0, after Gen0, the value is 10000, after Gen1, the value is 20000....
    }

    @Override
    //LIUFEIGE for tugboat
    public void generateJob() {
        //runExperiments();
    	//modified by fzhang 15.5.2018  to avoid negative time  finallly decide to keep double type: to avoid same arrival time
//        interArrivalTimeSampler.setMean();
//        System.out.println(interArrivalTimeSampler.getMean());
        double gap=interArrivalTimeSampler.next(randomDataGenerator);
        double arrivalTime = getClockTime()
                 + gap;
        //LIUFEIGE For tugboat with timeWindow
        // change distribution----------------------------------------------
        int objectPortArea = objectPortSampler.next(randomDataGenerator);// change distribution???????
        //---------------------------------
//        double shipLength = shipLengthSampler.next(randomDataGenerator); //change distribution
        double shipLength = 80 +  shipLengthSampler.next(randomDataGenerator); //change distribution
//        double shipLength = 80 * shipLengthSampler.next(randomDataGenerator); //mode 2: change distribution
        double DistanceStoB = objectBerthSampler.next(randomDataGenerator)*0.1;
        double ShipBerthTime = procTimeSampler.next(randomDataGenerator); //mode 2; U(30,90)

        //duedate mode1
        //calculate average processing time
        double totalProcTime = 0; //it isn't true pT of the task
        //s1 min value =0
//        int s21 = objectPortSampler1.next(randomDataGenerator) -1; //randomly select tugboat base
//        int s31= objectPortArea-1;
//        totalProcTime += systemState.getDSTS()[s21][s31]/13;
        //s2 min choose max v set
        double v=0;
        int numNeedTug0 = 0;
        if(shipLength<134) {numNeedTug0=1;}
        else if (shipLength<188) {numNeedTug0=2;}
        else if (shipLength<242) {numNeedTug0=2;}
        else if (shipLength<296) {numNeedTug0=2;}
        else {numNeedTug0=2;}
        for (int i = 0; i < numNeedTug0; i++) {
            double v0 = systemState.getWorkCenters().get(systemState.getWorkCenters().size()-i-1).getHorse_C0_C1_VTi()[3];
            v+=v0;
        }
        v=Math.sqrt(v);
        double pt = DistanceStoB/v;  //4 is a middle value of all type of tugboat
        totalProcTime+=pt;
        //s3 min distance
//        int s11 = objectPortArea-1;
//        int e1 = objectPortSampler1.next(randomDataGenerator) -1;
//        totalProcTime += 5/13;
        double dueDate = arrivalTime + dueDateFactor * totalProcTime;

        // dudate mode2
//        double ptBer = procTimeSampler.next(randomDataGenerator);
//        double ptSailing1 = AssumePtSailing(objectPortArea, systemState);
//        double dueDate = ptBer + dueDateFactor * ptSailing1;

        double weight = jobWeightSampler.next(randomDataGenerator);
        Job job = new Job(numJobsArrived, new ArrayList<>(),
                arrivalTime, arrivalTime, 0, weight,
                objectPortArea,DistanceStoB,shipLength,DistanceStoB);

        int numOperations = numOperationsSampler.next(randomDataGenerator);

        for (int i = 0; i < numOperations; i++) {
            Operation o = new Operation(job, i);
            //modified by fzhang 17.04.2018
            int numOptions = numOperationsSampler.next(randomDataGenerator);
            //System.out.println("numOptions: "+numOptions);

            int[] route = randomDataGenerator.nextPermutation(numWorkCenters, numOptions);
            //nextPermutation(n,k)
            //Generates an integer array of length k whose entries are selected randomly, without repetition, from the integers 0, ..., n - 1 (inclusive).

            //modified by fzhang  14.5.2018  in order to avoid negative or positive time(equal = 0)  finallly decide to keep double type
            //double procTime = procTimeSampler.next(randomDataGenerator); //use same proc time for all options for now
            //================start==========
//            double procTime = procTimeSampler.next(randomDataGenerator); //need to be reset in tugboat schedule


            for (int j = 0; j < numOptions; j++) {//9
                //for jss
                double procTime = 0; //Feige: initial,will be calculated later, for tugboat schedule
//                o.addOperationOption(new OperationOption(o,j,procTime,systemState.getWorkCenter(route[j])));
                //LIUFEIGE for tugboat, assume processing time, mode1
                double upperHorsepower=job.getUpperHorsepower();
//                List<WorkCenter> availmachine = systemState.cloneWorkCenterss(upperHorsepower);
                List<WorkCenter> availmachine = systemState.cloneWorkCenterss(job);
                List<WorkCenter> workCenterSet = new LinkedList<>();
                int idx0 = new UniformIntegerSampler(0,availmachine.size()-1).next(randomDataGenerator);
                WorkCenter workCenter0 = availmachine.get(idx0);
                workCenterSet.add(workCenter0);
                availmachine.remove(idx0);
                while (o.getJob().numberHorseTug(workCenterSet)!=0){
                    int idx = new UniformIntegerSampler(0,availmachine.size()-1).next(randomDataGenerator);
                    WorkCenter workCenter = availmachine.get(idx);
                    workCenterSet.add(workCenter);
                    availmachine.remove(idx);
                }

                int numNeedTug = job.getNumNeedTug();
                //mode 2:
//                double ptInSailingS1=AssumePtSailing(objectPortArea, systemState);
//                double ptInSailingS2=(25+35)/2;
//                procTime = ptInSailingS1+ptInSailingS2+procTimeSampler.next(randomDataGenerator);
                //assume the process time of operation initially
                o.addOperationOption(new OperationOption(o,j,procTime,workCenterSet,
                        upperHorsepower,numNeedTug,objectPortArea));
            }
            //==========end===========

            //modified by fzhang  29.5.2018  set different processing time for different machines
           /* for (int j = 0; j < numOptions; j++) {
            	double procTime = procTimeSampler.next(randomDataGenerator);
                o.addOperationOption(new OperationOption(o,j,procTime,systemState.getWorkCenter(route[j])));
            }
*/

           //fzhang 2019.6.22 set different processtime to each machine
            //============================start========================================================
            /*double ptmean =  procTimeSampler.next(randomDataGenerator);// set processtime of each option
            AbstractRealSampler ptnsampler=new NormalSampler(ptmean, ptmean/10);

            for (int j = 0; j < numOptions; j++) {
                double procTime= ptnsampler.next(randomDataGenerator);
                o.addOperationOption(new OperationOption(o,j,procTime,systemState.getWorkCenter(route[j])));
            }*/
            //==============================end=================================================

            job.addOperation(o);
        }

        job.linkOperations();
        //just set totalProcTime to average value, as we don't know which option will be chosen
        //this is just used to define dueDate value
//        double totalProcTime = numOperations * procTimeSampler.getMean();
//        double dueDate = job.getReleaseTime() + dueDateFactor * totalProcTime;


        job.setDueDate(dueDate); //by feige
        systemState.addJobToSystem(job);
        numJobsArrived ++;

        eventQueue.add(new JobArrivalEvent(job));
    }

    private double AssumePtSailing(int objectPortArea, SystemState systemState) {
        double avgPTSailing=0;
        for (int i = 0; i < 4; i++) {
             avgPTSailing += (systemState.SailingPT[objectPortArea][i][0] + systemState.SailingPT[objectPortArea][i][1]) / 2;
        }
        avgPTSailing/=4;
        return avgPTSailing;
    }

    private void runExperiments() {
        double interArrivalSum = 0.0;
        double numOperationsSum = 0.0;
        double numOptionsSum = 0.0;
        double procTimeSum = 0.0;
        int numRuns = 5000000;

        for (int i = 0; i < numRuns; ++i) {
            interArrivalSum += interArrivalTimeSampler.next(randomDataGenerator);
            numOperationsSum += numOperationsSampler.next(randomDataGenerator);
            numOptionsSum += numOperationsSampler.next(randomDataGenerator);
            procTimeSum += procTimeSampler.next(randomDataGenerator);
        }
        System.out.println("Average interarrival time: "+interArrivalSum/numRuns);
        System.out.println("Average num operations: "+numOperationsSum/numRuns);
        System.out.println("Average num options: "+numOptionsSum/numRuns);
        System.out.println("Average procedure time: "+procTimeSum/numRuns);
        System.out.println();
    }

    //control the inter time of job arrival
    public double interArrivalTimeMean(int numWorkCenters,
                                             int minNumOps,
                                             int maxNumOps,
                                             double utilLevel) {
        double meanNumOps = 0.5 * (minNumOps + maxNumOps); //(1+9)/2=5.5 average operations for a job is 5.5
        double meanProcTime = procTimeSampler.getMean(); //(1+99)/2=50   average processing time for a operation is 50
        //LIUFEIGE for tugboat
        double numberMachine = (20+33+38+30+22)/5/2;  //for tugboat???????????
        return (meanNumOps * meanProcTime) / (utilLevel * numberMachine); // the time to processing a job on each workcenter
        //for machines with same capacity, this return value is the same.
        //for machines with different capacities, this return value is different because utilLevel is dynamic
//        return (meanNumOps * meanProcTime) / (utilLevel * numWorkCenters); // the time to processing a job on each workcenter
    }

    public double interArrivalTimeforTimewindow(int numWorkCenters,
                                       int minNumOps,
                                       int maxNumOps,
                                       double utilLevel) {
        double meanNumOps = 0.5 * (minNumOps + maxNumOps); //(1+9)/2=5.5 average operations for a job is 5.5
        double meanProcTime = procTimeSampler.getMean(); //(1+99)/2=50   average processing time for a operation is 50

        //for machines with same capacity, this return value is the same.
        //for machines with different capacities, this return value is different because utilLevel is dynamic
        return (meanNumOps * meanProcTime) / (utilLevel * numWorkCenters); // the time to processing a job on each workcenter
    }

    public void setInterArrivalTimeSamplerMean() {
        double mean = interArrivalTimeMean(numWorkCenters, minNumOperations, maxNumOperations, utilLevel);
        interArrivalTimeSampler.setMean(mean);
    }

    public void setInterArrivalTimeSamplerForTimewindow(double timewindow) {
        double mean = interArrivalTimeforTimewindow(numWorkCenters, minNumOperations, maxNumOperations, utilLevel);
        interArrivalTimeForTimewindowSampler.setMean(mean);
    }

    public List<SequencingDecisionSituation> sequencingDecisionSituations(int minQueueLength) {
        List<SequencingDecisionSituation> sequencingDecisionSituations = new ArrayList<>();

        
        while (!eventQueue.isEmpty() && throughput < numJobsRecorded) {
            AbstractEvent nextEvent = eventQueue.poll();
//            System.out.println("throughput "+throughput);
            systemState.setClockTime(nextEvent.getTime());
            nextEvent.addSequencingDecisionSituation(this, sequencingDecisionSituations, minQueueLength);
        }

        resetState();

        return sequencingDecisionSituations;
    }

    public List<RoutingDecisionSituation> routingDecisionSituations(int minQueueLength) {
        List<RoutingDecisionSituation> routingDecisionSituations = new ArrayList<>();

        while (!eventQueue.isEmpty() && throughput < numJobsRecorded) {
            AbstractEvent nextEvent = eventQueue.poll();

            systemState.setClockTime(nextEvent.getTime());
            nextEvent.addRoutingDecisionSituation(this, routingDecisionSituations, minQueueLength);
        }

        resetState();

        return routingDecisionSituations;
    }

    @Override
    public Simulation surrogate(int numWorkCenters, int numJobsRecorded,
                                       int warmupJobs) {
        int surrogateMaxNumOperations = maxNumOperations;

        AbstractIntegerSampler surrogateNumOperationsSampler = numOperationsSampler.clone();
        AbstractIntegerSampler surrogateNumOptionsSampler = numOperationsSampler.clone();
        AbstractRealSampler surrogateInterArrivalTimeSampler = interArrivalTimeSampler.clone();

        if (surrogateMaxNumOperations > numWorkCenters) {
            surrogateMaxNumOperations = numWorkCenters;
            surrogateNumOperationsSampler.setUpper(surrogateMaxNumOperations);

            surrogateInterArrivalTimeSampler.setMean(interArrivalTimeMean(numWorkCenters,
                    minNumOperations, surrogateMaxNumOperations, utilLevel));
        }

        Simulation surrogate = new DynamicSimulation(seed, sequencingRule, routingRule, numWorkCenters,
                numJobsRecorded, warmupJobs, minNumOperations, surrogateMaxNumOperations,
                utilLevel, dueDateFactor, revisit, surrogateNumOperationsSampler,
                procTimeSampler, surrogateInterArrivalTimeSampler, jobWeightSampler);

        //modified by fzhang 17.04.2018
       /* Simulation surrogate = new DynamicSimulation(seed, sequencingRule, routingRule, numWorkCenters,
                numJobsRecorded, warmupJobs, minNumOperations, surrogateMaxNumOperations,
                utilLevel, dueDateFactor, revisit, surrogateNumOperationsSampler,
                numOptionsSampler, procTimeSampler, surrogateInterArrivalTimeSampler, jobWeightSampler);*/

        return surrogate;
    }

    @Override
    public Simulation surrogateBusy(int numWorkCenters, int numJobsRecorded,
                                int warmupJobs) {
        double utilLevel = 1;
        int surrogateMaxNumOperations = maxNumOperations;

        AbstractIntegerSampler surrogateNumOperationsSampler = numOperationsSampler.clone();
        AbstractRealSampler surrogateInterArrivalTimeSampler = interArrivalTimeSampler.clone();

        if (surrogateMaxNumOperations > numWorkCenters) {
            surrogateMaxNumOperations = numWorkCenters;
            surrogateNumOperationsSampler.setUpper(surrogateMaxNumOperations);

            surrogateInterArrivalTimeSampler.setMean(interArrivalTimeMean(numWorkCenters,
                    minNumOperations, surrogateMaxNumOperations, utilLevel));
        }

        Simulation surrogate = new DynamicSimulation(seed, sequencingRule, routingRule, numWorkCenters,
                numJobsRecorded, warmupJobs, minNumOperations, surrogateMaxNumOperations, utilLevel,
                dueDateFactor, revisit, surrogateNumOperationsSampler, procTimeSampler,
                surrogateInterArrivalTimeSampler, jobWeightSampler);

        //modified by fzhang 17.04.2018
     /*   Simulation surrogate = new DynamicSimulation(seed, sequencingRule, routingRule, numWorkCenters,
                numJobsRecorded, warmupJobs, minNumOperations, surrogateMaxNumOperations, utilLevel,
                dueDateFactor, revisit, surrogateNumOperationsSampler, numOptionsSampler, procTimeSampler,
                surrogateInterArrivalTimeSampler, jobWeightSampler);*/

        return surrogate;
    }

    public static DynamicSimulation standardFull(
            long seed,
            AbstractRule sequencingRule,
            AbstractRule routingRule,
            int numWorkCenters,
            int numJobsRecorded,
            int warmupJobs,
            double utilLevel,
            double dueDateFactor) {
        return new DynamicSimulation(seed, sequencingRule, routingRule, numWorkCenters, numJobsRecorded,
                warmupJobs, numWorkCenters, numWorkCenters, utilLevel,
                dueDateFactor, false);
    }

    // LIUFEIGE
    public static DynamicSimulation standardFull(
            long seed,
            AbstractRule sequencingRule,
            AbstractRule routingRule,
            int numWorkCenters,
            int numJobsRecorded,
            int warmupJobs,
            double utilLevel,
            double dueDateFactor,
            int minNumOperations,
            int maxNumOperations,
            String typeOfQuestion,
            double deviation) {
        return new DynamicSimulation(seed, sequencingRule, routingRule, numWorkCenters, numJobsRecorded, warmupJobs,
                minNumOperations, maxNumOperations,
                utilLevel, dueDateFactor, false,typeOfQuestion,deviation);
    }

    public static DynamicSimulation standardFull(
            long seed,
            AbstractRule sequencingRule,
            AbstractRule routingRule,
            int numWorkCenters,
            int numJobsRecorded,
            int warmupJobs,
            double utilLevel,
            double dueDateFactor,
            int minNumOperations,
            int maxNumOperations,
            String typeOfQuestion) {
        return new DynamicSimulation(seed, sequencingRule, routingRule, numWorkCenters, numJobsRecorded, warmupJobs,
                minNumOperations, maxNumOperations,
                utilLevel, dueDateFactor, false,typeOfQuestion);
    }

    public static DynamicSimulation standardMissing(
            long seed,
            AbstractRule sequencingRule,
            AbstractRule routingRule,
            int numWorkCenters,
            int numJobsRecorded,
            int warmupJobs,
            double utilLevel,
            double dueDateFactor) {
    	 return new DynamicSimulation(seed, sequencingRule, routingRule, numWorkCenters, numJobsRecorded,
                 warmupJobs,1, numWorkCenters, utilLevel, dueDateFactor, false);
    }

    // LIUFEIGE
    public static DynamicSimulation standardMissing(
            long seed,
            AbstractRule sequencingRule,
            AbstractRule routingRule,
            int numWorkCenters,
            int numJobsRecorded,
            int warmupJobs,
            double utilLevel,
            double dueDateFactor,
            int minNumOperations,
            int maxNumOperations,
            String typeOfQuestion,
            double deviation) {
        return new DynamicSimulation(seed, sequencingRule, routingRule, numWorkCenters, numJobsRecorded, warmupJobs,
                minNumOperations, maxNumOperations,
                utilLevel, dueDateFactor, false,typeOfQuestion,deviation);
    }

    public static DynamicSimulation standardMissing(
            long seed,
            AbstractRule sequencingRule,
            AbstractRule routingRule,
            int numWorkCenters,
            int numJobsRecorded,
            int warmupJobs,
            double utilLevel,
            double dueDateFactor,
            int minNumOperations,
            int maxNumOperations,
            String typeOfQuestion
            ) {
        return new DynamicSimulation(seed, sequencingRule, routingRule, numWorkCenters, numJobsRecorded, warmupJobs,
                minNumOperations, maxNumOperations,
                utilLevel, dueDateFactor, false,typeOfQuestion);
    }

}
