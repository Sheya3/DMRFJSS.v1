package yimei.jss.ruleanalysis;

import ec.gp.GPNode;
import ec.gp.GPTree;
import ec.multiobjective.MultiObjectiveFitness;
import yimei.jss.feature.ignore.Ignorer;
import yimei.jss.feature.ignore.SimpleIgnorer;
import yimei.jss.gp.GPRuleEvolutionState;
import yimei.jss.gp.terminal.AttributeGPNode;
import yimei.jss.gp.terminal.JobShopAttribute;
import yimei.jss.jobshop.Objective;
import yimei.jss.jobshop.SchedulingSet;
import yimei.jss.rule.operation.evolved.GPRule;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yimei on 12/10/16.
 */
public class RuleTestFeatureContribution extends RuleTest {

    private String featureSetName;
    private Ignorer ignorer = new SimpleIgnorer();

    public RuleTestFeatureContribution(String trainPath,
                                       RuleType ruleType,
                                       int numRuns,
                                       String testScenario,
                                       String testSetName,
                                       List<Objective> objectives,
                                       String featureSetName,
                                       int numPopulations) {
        super(trainPath, ruleType, numRuns, testScenario, testSetName, objectives, numPopulations);
        this.featureSetName = featureSetName;
    }

    public RuleTestFeatureContribution(String trainPath,
                                       RuleType ruleType,
                                       int numRuns,
                                       String testScenario,
                                       String testSetName,
                                       String featureSetName,
                                       int numPopulations) {
        this(trainPath, ruleType, numRuns, testScenario, testSetName,
                new ArrayList<>(), featureSetName, numPopulations);
    }

    public List<GPNode> featuresFromSetName() {
        List<GPNode> features = new ArrayList<>();

        switch (featureSetName) {
            case "basic-terminals":
                for (JobShopAttribute a : JobShopAttribute.basicAttributes()) {
                    features.add(new AttributeGPNode(a));
                }
                break;
            case "relative-terminals":
                for (JobShopAttribute a : JobShopAttribute.relativeAttributes()) {
                    features.add(new AttributeGPNode(a));
                }
                break;
                //LIUFEIGE
            case "relativeForTugboat":
                for (JobShopAttribute a : JobShopAttribute.relativeAttributesForTugboat()) {
                    features.add(new AttributeGPNode(a));
                }
            default:
                break;
        }

        return features;
    }

    @Override
    public void writeToCSV() {
        SchedulingSet testSet = generateTestSet();
        List<GPNode> features = featuresFromSetName();

        File targetPath = new File(trainPath + "test");
        if (!targetPath.exists()) {
            targetPath.mkdirs();
        }

        File csvFile = new File(targetPath + "/" + testSetName + "-feature-contribution.csv");

        double[][] featureContributionMtx = new double[numRuns][features.size()];

        for (int i = 0; i < numRuns; i++) {
//            File sourceFile = new File(trainPath + "job." + i + ".out.stat");
            //LIUFEIGE
            File sourceFile = new File(trainPath + "\\"+"job." + i + ".out.stat");
//            "E:\download\grid\MTGP-tugboat-dynamic\energyConsumption-0.75-1.5\job.0.out.stat"
            TestResult result = TestResult.readFromFile(sourceFile, ruleType, numPopulations);

            long start = System.currentTimeMillis();

            GPRule[] bestRules = (GPRule[]) result.getBestRules();

            MultiObjectiveFitness allFeaturesFit = new MultiObjectiveFitness();
            allFeaturesFit.objectives = new double[1];
            allFeaturesFit.maxObjective = new double[1];
            allFeaturesFit.minObjective = new double[1];
            allFeaturesFit.maximize = new boolean[1];
            bestRules[0].calcFitness(allFeaturesFit, null, testSet, bestRules[1], objectives);

            for (int j = 0; j < features.size(); j++) {
                GPNode feature = features.get(j);
                MultiObjectiveFitness fit = new MultiObjectiveFitness();
                fit.objectives = new double[1];
                fit.maxObjective = new double[1];
                fit.minObjective = new double[1];
                fit.maximize = new boolean[1];

                GPRule tempSeqRule;
                GPRule tempRoutingRule;
                if (bestRules[0].getType() == yimei.jss.rule.RuleType.SEQUENCING) {
                    tempSeqRule = new GPRule(yimei.jss.rule.RuleType.SEQUENCING, (GPTree)(bestRules[0].getGPTree().clone()));
                    tempRoutingRule = new GPRule(yimei.jss.rule.RuleType.ROUTING, (GPTree)(bestRules[1].getGPTree().clone()));
                } else {
                    tempSeqRule = new GPRule(yimei.jss.rule.RuleType.SEQUENCING, (GPTree)(bestRules[1].getGPTree().clone()));
                    tempRoutingRule = new GPRule(yimei.jss.rule.RuleType.ROUTING, (GPTree)(bestRules[0].getGPTree().clone()));
                }

                //TODO: What do we do here? Which rule do we ignore features from? One at a time?

                tempSeqRule.ignore(feature, ignorer);
                tempSeqRule.calcFitness(fit, null, testSet, tempRoutingRule, objectives);

                System.out.format("Run %d, %s: %.2f\n", i, feature.toString(),
                        fit.fitness() - allFeaturesFit.fitness());

                featureContributionMtx[i][j] = fit.fitness() - allFeaturesFit.fitness();
            }

            long finish = System.currentTimeMillis();
            long duration = finish - start;
            System.out.println("Run " + i + ": Duration = " + duration + " ms.");
        }

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(csvFile.getAbsoluteFile()));
            writer.write("Run,Feature,Contribution");
            writer.newLine();
            for (int i = 0; i < numRuns; i++) {
                for (int j = 0; j < features.size(); j++) {
                    writer.write(i + "," + features.get(j).toString() + "," +
                            featureContributionMtx[i][j]);
                    writer.newLine();
                }
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //LIUFEIGE for calculate features contribution of the best individuals of all runs
    public void featureContriOfRuns() {
        SchedulingSet testSet = generateTestSet();
        List<GPNode> features = featuresFromSetName();

        File targetPath = new File(trainPath + "test");
        if (!targetPath.exists()) {
            targetPath.mkdirs();
        }

        File csvFile = new File(targetPath + "/" + testSetName + "-feature-contribution.csv");

        double[][] featureContributionMtx = new double[numRuns][features.size()];
        double[][] featureContributionMtxIntree = new double[numRuns][features.size()];

        ArrayList<int[]> seqFeatureOccurrences = new ArrayList<>();
        ArrayList<int[]> rouFeatureOccurrences = new ArrayList<>();
        ArrayList<double[]> seqFeatureOccurrencesIntree = new ArrayList<>();
        ArrayList<double[]> rouFeatureOccurrencesIntree = new ArrayList<>();

        for (int i = 0; i < numRuns; i++) {
//            File sourceFile = new File(trainPath + "job." + i + ".out.stat");
            //LIUFEIGE
            File sourceFile = new File(trainPath + "\\"+"job." + i + ".out.stat");
//            "E:\download\grid\MTGP-tugboat-dynamic\energyConsumption-0.75-1.5\job.0.out.stat"
            TestResult result = TestResult.readFromFile(sourceFile, ruleType, numPopulations);

            //
            int[][] terminalOccurance = new int[result.getBestRulesOfRuns().length][];
            double[][] terminalOccuranceInTree = new double[result.getBestRulesOfRuns().length][];
            for (int t = 0; t < result.getBestRulesOfRuns().length; t++) {
                //LIU FEIGE
                terminalOccurance[t] = new int[features.size()];
                terminalOccuranceInTree[t] = new double[features.size()];
            }

            for (int f = 0; f < features.size(); f++) {
                for (int j = 0; j < terminalOccurance.length; j++) {
                    String rule = result.getBestRulesOfRuns()[j];
                    terminalOccurance[j][f] += GPRuleEvolutionState.countSubstring(rule, features.get(f).name());
                }
            }

            seqFeatureOccurrences.add(terminalOccurance[0]);
            if(terminalOccurance.length == 2){
                rouFeatureOccurrences.add(terminalOccurance[1]);
            }
            for (int j = 0; j < features.size(); j++) {
                featureContributionMtx[i][j]=terminalOccurance[0][j]+terminalOccurance[1][j];
            }

            //calculate
            double totalnumberOfTree0=0;
            double totalnumberOfTree1=0;
            for (int j = 0; j < terminalOccurance[0].length; j++) {
                totalnumberOfTree0+=terminalOccurance[0][j];
                totalnumberOfTree1+=terminalOccurance[1][j];
            }
            for (int j = 0; j < terminalOccurance[0].length; j++) {
                terminalOccuranceInTree[0][j]=terminalOccurance[0][j]/totalnumberOfTree0;
                terminalOccuranceInTree[1][j]=terminalOccurance[1][j]/totalnumberOfTree1;
                featureContributionMtxIntree[i][j]=terminalOccurance[0][j]+terminalOccurance[1][j];
            }

            seqFeatureOccurrencesIntree.add(terminalOccuranceInTree[0]);
            if(terminalOccurance.length == 2){
                rouFeatureOccurrencesIntree.add(terminalOccuranceInTree[1]);
            }



//            long start = System.currentTimeMillis();

//            GPRule[] bestRules = (GPRule[]) result.getBestRules();
//
//            MultiObjectiveFitness allFeaturesFit = new MultiObjectiveFitness();
//            allFeaturesFit.objectives = new double[1];
//            allFeaturesFit.maxObjective = new double[1];
//            allFeaturesFit.minObjective = new double[1];
//            allFeaturesFit.maximize = new boolean[1];
//            bestRules[0].calcFitness(allFeaturesFit, null, testSet, bestRules[1], objectives);
//
//            for (int j = 0; j < features.size(); j++) {
//                GPNode feature = features.get(j);
//                MultiObjectiveFitness fit = new MultiObjectiveFitness();
//                fit.objectives = new double[1];
//                fit.maxObjective = new double[1];
//                fit.minObjective = new double[1];
//                fit.maximize = new boolean[1];
//
//                GPRule tempSeqRule;
//                GPRule tempRoutingRule;
//                if (bestRules[0].getType() == yimei.jss.rule.RuleType.SEQUENCING) {
//                    tempSeqRule = new GPRule(yimei.jss.rule.RuleType.SEQUENCING, (GPTree)(bestRules[0].getGPTree().clone()));
//                    tempRoutingRule = new GPRule(yimei.jss.rule.RuleType.ROUTING, (GPTree)(bestRules[1].getGPTree().clone()));
//                } else {
//                    tempSeqRule = new GPRule(yimei.jss.rule.RuleType.SEQUENCING, (GPTree)(bestRules[1].getGPTree().clone()));
//                    tempRoutingRule = new GPRule(yimei.jss.rule.RuleType.ROUTING, (GPTree)(bestRules[0].getGPTree().clone()));
//                }
//
//                //TODO: What do we do here? Which rule do we ignore features from? One at a time?
//
//                tempSeqRule.ignore(feature, ignorer);
//                tempSeqRule.calcFitness(fit, null, testSet, tempRoutingRule, objectives);
//
//                System.out.format("Run %d, %s: %.2f\n", i, feature.toString(),
//                        fit.fitness() - allFeaturesFit.fitness());
//
//                featureContributionMtx[i][j] = fit.fitness() - allFeaturesFit.fitness();
//            }
//
//            long finish = System.currentTimeMillis();
//            long duration = finish - start;
//            System.out.println("Run " + i + ": Duration = " + duration + " ms.");
        }

        //feature contribution value calculate
        double[] avgSeqFeatureIntree = new double[features.size()];
        double[] avgRouFeatureIntree = new double[features.size()];
        double[] avgFeatureIntree = new double[features.size()];
        double[] allSeqFeature = new double[features.size()];
        double[] allRouFeature = new double[features.size()];
        double[] allFeature= new double[features.size()];
        for (int i = 0; i < features.size(); i++) {
            for (int j = 0; j < numRuns; j++) {
                avgSeqFeatureIntree[i]=avgSeqFeatureIntree[i]+seqFeatureOccurrencesIntree.get(j)[i]/30;
                avgRouFeatureIntree[i]=avgRouFeatureIntree[i]+rouFeatureOccurrencesIntree.get(j)[i]/30;
                avgFeatureIntree[i]=avgSeqFeatureIntree[i]+avgRouFeatureIntree[i];
                allSeqFeature[i]=allSeqFeature[i]+seqFeatureOccurrences.get(j)[i];
                allRouFeature[i]=allRouFeature[i]+rouFeatureOccurrences.get(j)[i];
                allFeature[i]= allSeqFeature[i]+ allRouFeature[i];
            }
        }

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(csvFile.getAbsoluteFile()));
            writer.write("Run,Feature,Contribution");
            writer.newLine();
            for (int i = 0; i < numRuns; i++) {
                for (int j = 0; j < features.size(); j++) {
                    writer.write(i + "," + features.get(j).toString() + "," +
                            featureContributionMtx[i][j]);
                    writer.newLine();
                }
            }
            writer.write("Run,Feature,ContributionInseq,ContributionInrout");
            writer.newLine();
            for (int i = 0; i < numRuns; i++) {
                for (int j = 0; j < features.size(); j++) {
                    writer.write(i + "," + features.get(j).toString() + "," +
                            seqFeatureOccurrences.get(i)[j] +","+
                            rouFeatureOccurrences.get(i)[j]);
                    writer.newLine();
                }
            }
            writer.write("Run,Feature,ContributionInTreeseq,ContributionInTreerout");
            writer.newLine();
            for (int i = 0; i < numRuns; i++) {
                for (int j = 0; j < features.size(); j++) {
                    writer.write(i + "," + features.get(j).toString() + "," +
                            seqFeatureOccurrencesIntree.get(i)[j] +","+
                            rouFeatureOccurrencesIntree.get(i)[j]);
                    writer.newLine();
                }
            }
            writer.write("Feature,ContributionIntreeSeq,ContributionIntreerout,contributionIntree," +
                    "seqFeatureall,routFeatureall,allfeature");
            writer.newLine();
            for (int j = 0; j < features.size(); j++) {
                writer.write( features.get(j).toString() + "," +
                        avgSeqFeatureIntree[j] + "," +
                                avgRouFeatureIntree[j]+ "," +
                                avgFeatureIntree[j]+ "," +
                        allSeqFeature[j]+ "," +
                        allRouFeature[j]+ "," +
                        allFeature[j]
                        );
                writer.newLine();
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
    	//should follow this sorting to set parameters for testing
        int idx = 0;
        String trainPath = args[idx];
        idx ++;
        RuleType ruleType = RuleType.get(args[idx]);
        idx ++;
        int numRuns = Integer.valueOf(args[idx]);
        idx ++;
        String testScenario = args[idx];
        idx ++;
        String testSetName = args[idx];
        idx ++;
        int numPopulations = Integer.valueOf(args[idx]);
        idx ++;
        int numObjectives = Integer.valueOf(args[idx]);
        idx ++;
        List<Objective> objectives = new ArrayList<>();
        for (int i = 0; i < numObjectives; i++) {
            objectives.add(Objective.get(args[idx]));
            idx ++;
        }
        String featureSetName = String.valueOf(args[idx]);
        idx ++;

        RuleTestFeatureContribution ruleTest = new RuleTestFeatureContribution(trainPath,
                ruleType, numRuns, testScenario, testSetName, objectives, featureSetName, numPopulations);

//        ruleTest.writeToCSV();
        ruleTest.featureContriOfRuns();
    }
}
