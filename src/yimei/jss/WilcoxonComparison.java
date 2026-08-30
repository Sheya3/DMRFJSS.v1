////package yimei.jss;
////
////import com.opencsv.CSVReader;
////
////import java.io.FileReader;
////import java.io.IOException;
////import java.nio.file.Path;
////import java.nio.file.Paths;
////import java.util.ArrayList;
////import java.util.Comparator;
////import java.util.List;
////import java.util.Map;
////import java.util.stream.Collectors;
////
////public class WilcoxonComparison {
////
////    // Configuration - update these based on your needs
////    private static final String[] ALGOS = {"MTGP-tugboat-dynamic1","MTGP-tugboat-dynamic2", "MTGP-tugboat-dynamic3"};
////    private static final String[] ALGOS_NAME = {"MTGP1","MTGP2", "MTGP3"};
////
////    private static final String[] scenarios = {
////            "mean-flowtime-0.75-1.3-60", //0
////            "mean-flowtime-0.85-1.3-60", //1
////            "mean-flowtime-0.95-1.3-60", //2
////            "energyConsumption-0.75-1.3-60",
////            "energyConsumption-0.85-1.3-60",
////            "energyConsumption-0.95-1.3-60",
////            "mean-tardiness-0.75-1.3-60", //6
////            "mean-tardiness-0.85-1.3-60", //7
////            "mean-tardiness-0.95-1.3-60", //8
////            "makespan-0.75-1.3-60",
////            "makespan-0.85-1.3-60",
////            "makespan-0.95-1.3-60",
////            "mean-flowtime-0.75-1.3-80",//12
////            "mean-flowtime-0.85-1.3-80",//13
////            "mean-flowtime-0.95-1.3-80",//14
////            "energyConsumption-0.75-1.3-80",
////            "energyConsumption-0.85-1.3-80",
////            "energyConsumption-0.95-1.3-80",
////            "mean-tardiness-0.75-1.3-80",
////            "mean-tardiness-0.85-1.3-80",
////            "mean-tardiness-0.95-1.3-80",
////            "makespan-0.75-1.3-80",
////            "makespan-0.85-1.3-80",
////            "makespan-0.95-1.3-80"
////    };
////
////    private static final String COLUMN = "test-fit";
////    private static final String BASE_PATH = "E:\\download\\grid\\MTGP-main\\";
////    private static final int MAX_GENERATIONS = 51; // From the R script
////
////    // Data structure to hold results
////    static class ResultRow {
////        String scenario;
////        String algo;
////        int run;
////        int generation;
////        double testFitness;
////
////        ResultRow(String scenario, String algo, int run, int generation, double testFitness) {
////            this.scenario = scenario;
////            this.algo = algo;
////            this.run = run;
////            this.generation = generation;
////            this.testFitness = testFitness;
////        }
////    }
////
////    static class FinalData {
////        String scenario;
////        String algo;
////        int run;
////        double value;
////
////        FinalData(String scenario, String algo, int run, double value) {
////            this.scenario = scenario;
////            this.algo = algo;
////            this.run = run;
////            this.value = value;
////        }
////    }
////
////    public static void main(String[] args) {
////        try {
////            // Read all data
////            List<ResultRow> allResults = readAllData();
////
////            // Extract final generation data
////            List<FinalData> finalData = extractFinalData(allResults);
////
////            // Perform statistical comparison
////            performComparison(finalData);
////
////        } catch (Exception e) {
////            e.printStackTrace();
////        }
////    }
////
////    private static List<ResultRow> readAllData() throws IOException {
////        List<ResultRow> results = new ArrayList<>();
////
////        for (int s = 0; s < scenarios.length; s++) {
////            String scenario = scenarios[s];
////            String testFile = determineTestFile(s);
////
////            for (int a = 0; a < ALGOS.length; a++) {
////                String algo = ALGOS[a];
////                String algoName = ALGOS_NAME[a];
////
////                String filePath = BASE_PATH + algo + "/" +
////                        scenario + "/test/" + testFile;
////
////                Path path = Paths.get(filePath);
////
//////                String filePath = "E:\\download\\grid\\TabuGP25\\" + algo +
//////                        "/" + scenario + "/test/" + testfile;
////
////                System.out.println("Reading: " + filePath);
////
////                try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
////                    String[] headers = reader.readNext(); // 读取列名
////                    String[] line;
////                    while ((line = reader.readNext()) != null) {
////                        int run = Integer.parseInt(getColumnValue(line, headers, "Run"));
////                        int generation = Integer.parseInt(getColumnValue(line, headers, "Generation"));
////                        double trainFitness = Double.parseDouble(getColumnValue(line, headers, "TrainFitness"));
////                        double testFitness = Double.parseDouble(getColumnValue(line, headers, "TestFitness"));
////                        results.add(new ResultRow(scenario, algoName, run, generation, testFitness));
////                    }
////                } catch (Exception e) {
////                    System.err.println("Cannot read file: " + filePath);
////                    e.printStackTrace();
////                }
////                int aa=0;
//////                if (Files.exists(path)) {
//////                    List<String> lines = Files.readAllLines(path);
//////
//////                    // Skip header
//////                    for (int i = 1; i < lines.size(); i++) {
//////                        String line = lines.get(i);
//////                        if (line.trim().isEmpty()) continue;
//////
//////                        String[] parts = line.split(",");
//////                        if (parts.length >= 11) { // Based on the R structure
//////                            try {
//////                                int run = Integer.parseInt(parts[0].trim());
//////                                int generation = Integer.parseInt(parts[1].trim());
//////                                double testFitness = Double.parseDouble(parts[9].trim());
//////
//////                                results.add(new ResultRow(scenario, algoName, run,
//////                                        generation, testFitness));
//////                            } catch (NumberFormatException e) {
//////                                // Skip malformed lines
//////                            }
//////                        }
//////                    }
//////                } else {
//////                    System.err.println("File not found: " + filePath);
//////                }
////            }
////        }
////
////        return results;
////    }
////
////    private static String determineTestFile(int i) {
////        // Logic from R script to determine test file based on instance index
////        String testfile;
////        if(i%3==0&&i<12){
////            double sign1 = 0.75; int sign2 = 60;
////            testfile = "missing-"+sign1+"-1.3-"+sign2+".csv";
////        } else if (i%3==1&&i<12) {
////            double sign1 = 0.85; int sign2 = 60;
////            testfile = "missing-"+sign1+"-1.3-"+sign2+".csv";
////        } else if (i%3==2&&i<12) {
////            double sign1 = 0.95; int sign2 = 60;
////            testfile = "missing-"+sign1+"-1.3-"+sign2+".csv";
////        } else if (i%3==0&&i>=12) {
////            double sign1 = 0.75; int sign2 = 80;
////            testfile = "missing-"+sign1+"-1.3-"+sign2+".csv";
////        } else if (i%3==1&&i>=12) {
////            double sign1 = 0.85; int sign2 = 80;
////            testfile = "missing-"+sign1+"-1.3-"+sign2+".csv";
////        }else if (i%3==2&&i>=12) {
////            double sign1 = 0.95; int sign2 = 80;
////            testfile = "missing-"+sign1+"-1.3-"+sign2+".csv";
////        }else {
////            testfile ="error";
////            System.out.println("error");
////
////        }
////        return testfile;
////    }
////
/////*    private static List<FinalData> extractFinalData(List<ResultRow> allResults) {
////        List<FinalData> finalData = new ArrayList<>();
////
////        for (String instance : scenarios) {
////            for (String algo : ALGOS_NAME) {
////                // Get last generation data for this instance/algo combination
////                List<ResultRow> lastGenRows = allResults.stream()
////                        .filter(r -> r.scenario.equals(instance) &&
////                                r.algo.equals(algo) &&
////                                r.generation == MAX_GENERATIONS)
////                        .collect(Collectors.toList());
////
////                for (ResultRow row : lastGenRows) {
////                    finalData.add(new FinalData(instance, algo, row.run, row.testFitness));
////                }
////            }
////        }
////
////        return finalData;
////    }*/
////
////
////    private static List<FinalData> extractFinalData(List<ResultRow> allResults) {
////        List<FinalData> finalData = new ArrayList<>();
////
////        for (String instance : scenarios) {
////            for (String algo : ALGOS_NAME) {
////                // Group by run
////                Map<Integer, List<ResultRow>> runsMap = allResults.stream()
////                        .filter(r -> r.scenario.equals(instance) && r.algo.equals(algo))
////                        .collect(Collectors.groupingBy(r -> r.run));
////
////                // For each run, find the row with minimum TestFitness
////                for (Map.Entry<Integer, List<ResultRow>> entry : runsMap.entrySet()) {
////                    int run = entry.getKey();
////                    List<ResultRow> rowsForRun = entry.getValue();
////
////                    // Find the row with minimum TestFitness
////                    ResultRow bestRow = rowsForRun.stream()
////                            .min(Comparator.comparingDouble(r -> r.testFitness))
////                            .orElse(null);
////
////                    if (bestRow != null) {
////                        finalData.add(new FinalData(instance, algo, run, bestRow.testFitness));
////                    }
////                }
////            }
////        }
////
////        return finalData;
////    }
////
////    private static void performComparison(List<FinalData> finalData) {
////        int nInstances = scenarios.length;
////        int nAlgos = ALGOS_NAME.length;
////
////        double[][] meanMatrix = new double[nInstances][nAlgos];
////        double[][] sdMatrix = new double[nInstances][nAlgos];
////        double[][] pMatrix = new double[nInstances][nAlgos - 1];
////
////        String lastAlgo = ALGOS_NAME[nAlgos - 1];
////
////        for (int s = 0; s < nInstances; s++) {
////            String instance = scenarios[s];
////
////            // Get data for last algorithm
////            List<Double> lastAlgoValues = finalData.stream()
////                    .filter(f -> f.scenario.equals(instance) && f.algo.equals(lastAlgo))
////                    .map(f -> f.value)
////                    .collect(Collectors.toList());
////
////            // Calculate mean and SD for last algorithm
////            meanMatrix[s][nAlgos - 1] = calculateMean(lastAlgoValues);
////            sdMatrix[s][nAlgos - 1] = calculateSD(lastAlgoValues, meanMatrix[s][nAlgos - 1]);
////
////            // Compare with other algorithms
////            for (int a = 0; a < nAlgos - 1; a++) {
////                String algo = ALGOS_NAME[a];
////
////                List<Double> algoValues = finalData.stream()
////                        .filter(f -> f.scenario.equals(instance) && f.algo.equals(algo))
////                        .map(f -> f.value)
////                        .collect(Collectors.toList());
////
////                // Calculate mean and SD
////                meanMatrix[s][a] = calculateMean(algoValues);
////                sdMatrix[s][a] = calculateSD(algoValues, meanMatrix[s][a]);
////
////                // Perform Wilcoxon rank sum test
////                try {
////                    pMatrix[s][a] = performWilcoxonTest(lastAlgoValues, algoValues);
////                } catch (Exception e) {
////                    pMatrix[s][a] = 1.0; // Default if test fails
////                }
////            }
////        }
////
////        // Print results in LaTeX format (matching R output)
////        printResults(meanMatrix, sdMatrix, pMatrix);
////    }
////
////    private static double calculateMean(List<Double> values) {
////        return values.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
////    }
////
////    private static double calculateSD(List<Double> values, double mean) {
////        if (values.size() <= 1) return 0.0;
////        double sum = 0.0;
////        for (double v : values) {
////            sum += Math.pow(v - mean, 2);
////        }
////        return Math.sqrt(sum / (values.size() - 1));
////    }
////
////    private static double performWilcoxonTest(List<Double> group1, List<Double> group2) {
////        // Convert to primitive arrays
////        double[] array1 = group1.stream().mapToDouble(Double::doubleValue).toArray();
////        double[] array2 = group2.stream().mapToDouble(Double::doubleValue).toArray();
////
////        // For independent samples, we need to adapt the WilcoxonSignedRankTest
////        // This is a simplified approach - you might want to implement Mann-Whitney U test
////        // or use a library that provides it directly
////
////        // For now, we'll implement a basic approximation
////        return calculateMannWhitneyU(array1, array2);
////    }
////
////    private static double calculateMannWhitneyU(double[] group1, double[] group2) {
////        // Combine and rank all values
////        List<ValueWithGroup> combined = new ArrayList<>();
////        for (double v : group1) {
////            combined.add(new ValueWithGroup(v, 1));
////        }
////        for (double v : group2) {
////            combined.add(new ValueWithGroup(v, 2));
////        }
////
////        // Sort by value
////        combined.sort(Comparator.comparingDouble(v -> v.value));
////
////        // Assign ranks (handling ties)
////        double[] ranks = new double[combined.size()];
////        for (int i = 0; i < combined.size(); i++) {
////            int j = i;
////            while (j < combined.size() - 1 &&
////                    Math.abs(combined.get(j).value - combined.get(j + 1).value) < 1e-10) {
////                j++;
////            }
////
////            if (i == j) {
////                ranks[i] = i + 1;
////            } else {
////                double avgRank = (i + 1 + j + 1) / 2.0;
////                for (int k = i; k <= j; k++) {
////                    ranks[k] = avgRank;
////                }
////                i = j;
////            }
////        }
////
////        // Calculate U statistic
////        double rankSum1 = 0;
////        for (int i = 0; i < combined.size(); i++) {
////            if (combined.get(i).group == 1) {
////                rankSum1 += ranks[i];
////            }
////        }
////
////        double n1 = group1.length;
////        double n2 = group2.length;
////        double u1 = rankSum1 - (n1 * (n1 + 1) / 2.0);
////        double u2 = n1 * n2 - u1;
////        double u = Math.min(u1, u2);
////
////        // Normal approximation for p-value
////        double meanU = n1 * n2 / 2.0;
////        double sdU = Math.sqrt(n1 * n2 * (n1 + n2 + 1) / 12.0);
////        double z = (u - meanU) / sdU;
////
////        // Two-tailed p-value
////        return 2 * (1 - normalCDF(Math.abs(z)));
////    }
////
////    private static double normalCDF(double x) {
////        // Approximation of the standard normal CDF
////        return 0.5 * (1 + erf(x / Math.sqrt(2)));
////    }
////
////    private static double erf(double x) {
////        // Approximation of the error function
////        double t = 1.0 / (1.0 + 0.5 * Math.abs(x));
////        double tau = t * Math.exp(-x * x - 1.26551223 +
////                1.00002368 * t + 0.37409196 * Math.pow(t, 2) +
////                0.09678418 * Math.pow(t, 3) - 0.18628806 * Math.pow(t, 4) +
////                0.27886807 * Math.pow(t, 5) - 1.13520398 * Math.pow(t, 6) +
////                1.48851587 * Math.pow(t, 7) - 0.82215223 * Math.pow(t, 8) +
////                0.17087277 * Math.pow(t, 9));
////        return x >= 0 ? 1 - tau : tau - 1;
////    }
////
////    static class ValueWithGroup {
////        double value;
////        int group;
////
////        ValueWithGroup(double value, int group) {
////            this.value = value;
////            this.group = group;
////        }
////    }
////
////    private static void printResults(double[][] meanMatrix, double[][] sdMatrix,
////                                     double[][] pMatrix) {
////        int nInstances = scenarios.length;
////        int nAlgos = ALGOS_NAME.length;
////
////        for (int s = 0; s < nInstances; s++) {
////            System.out.printf("%s ", scenarios[s]);
////
////            for (int a = 0; a < nAlgos - 1; a++) {
////                double pValue = pMatrix[s][a];
////                double mean = meanMatrix[s][a];
////                double sd = sdMatrix[s][a];
////                double lastAlgoMean = meanMatrix[s][nAlgos - 1];
////
////                if (pValue < 0.05) {
////                    if (mean < lastAlgoMean) {
////                        System.out.printf("& %.2f(%.2f){\\bf(--)} (%.2f) ",
////                                mean, sd, pValue);
////                    } else {
////                        System.out.printf("& %.2f(%.2f){\\bf(+)} (%.2f) ",
////                                mean, sd, pValue);
////                    }
////                } else {
////                    System.out.printf("& %.2f(%.2f){($\\approx$)} (%.2f) ",
////                            mean, sd, pValue);
////                }
////            }
////
////            System.out.printf("& %.2f(%.2f) \\\\\n",
////                    meanMatrix[s][nAlgos - 1],
////                    sdMatrix[s][nAlgos - 1]);
////        }
////    }
////
////    private static String getColumnValue(String[] line, String[] headers, String colName) {
////        for (int i = 0; i < headers.length; i++) {
////            if (headers[i].equals(colName)) {
////                return line[i];
////            }
////        }
////        throw new RuntimeException("Column not found: " + colName);
////    }
////}
//
//package yimei.jss;
//
//import com.opencsv.CSVReader;
//
//import java.io.FileReader;
//import java.io.IOException;
//import java.util.*;
//import java.util.stream.Collectors;
//
//public class WilcoxonComparison {
//
//    // Configuration - update these based on your needs
//    private static final String[] ALGOS = {"MTGP-tugboat-dynamic1","MTGP-tugboat-dynamic2", "MTGP-tugboat-dynamic3"};
//    private static final String[] ALGOS_NAME = {"MTGP1","MTGP2", "MTGP3"};
//
//    private static final String[] scenarios = {
//            "mean-flowtime-0.75-1.3-60",   //0
//            "mean-flowtime-0.85-1.3-60",   //1
//            "mean-flowtime-0.95-1.3-60",   //2
//            "energyConsumption-0.75-1.3-60",
//            "energyConsumption-0.85-1.3-60",
//            "energyConsumption-0.95-1.3-60",
//            "mean-tardiness-0.75-1.3-60",  //6
//            "mean-tardiness-0.85-1.3-60",  //7
//            "mean-tardiness-0.95-1.3-60",  //8
//            "makespan-0.75-1.3-60",
//            "makespan-0.85-1.3-60",
//            "makespan-0.95-1.3-60",
//            "mean-flowtime-0.75-1.3-80",   //12
//            "mean-flowtime-0.85-1.3-80",   //13
//            "mean-flowtime-0.95-1.3-80",   //14
//            "energyConsumption-0.75-1.3-80",
//            "energyConsumption-0.85-1.3-80",
//            "energyConsumption-0.95-1.3-80",
//            "mean-tardiness-0.75-1.3-80",
//            "mean-tardiness-0.85-1.3-80",
//            "mean-tardiness-0.95-1.3-80",
//            "makespan-0.75-1.3-80",
//            "makespan-0.85-1.3-80",
//            "makespan-0.95-1.3-80"
//    };
//
//    private static final String BASE_PATH = "E:\\download\\grid\\MTGP-main\\";
//
//    // Data structure to hold results
//    static class ResultRow {
//        String scenario;
//        String algo;
//        int run;
//        int generation;
//        double testFitness;
//
//        ResultRow(String scenario, String algo, int run, int generation, double testFitness) {
//            this.scenario = scenario;
//            this.algo = algo;
//            this.run = run;
//            this.generation = generation;
//            this.testFitness = testFitness;
//        }
//    }
//
//    static class FinalData {
//        String scenario;
//        String algo;
//        int run;
//        double value;
//
//        FinalData(String scenario, String algo, int run, double value) {
//            this.scenario = scenario;
//            this.algo = algo;
//            this.run = run;
//            this.value = value;
//        }
//    }
//
//    public static void main(String[] args) {
//        try {
//            // Read all data
//            List<ResultRow> allResults = readAllData();
//
//            // Extract final generation data (best fitness per run)
//            List<FinalData> finalData = extractFinalData(allResults);
//
//            // Perform statistical comparison
//            performComparison(finalData);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private static List<ResultRow> readAllData() throws IOException {
//        List<ResultRow> results = new ArrayList<>();
//
//        for (int s = 0; s < scenarios.length; s++) {
//            String scenario = scenarios[s];
//            String testFile = determineTestFile(s);
//
//            for (int a = 0; a < ALGOS.length; a++) {
//                String algo = ALGOS[a];
//                String algoName = ALGOS_NAME[a];
//
//                String filePath = BASE_PATH + algo + "/" +
//                        scenario + "/test/" + testFile;
//
//                System.out.println("Reading: " + filePath);
//
//                try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
//                    String[] headers = reader.readNext();
//                    String[] line;
//                    while ((line = reader.readNext()) != null) {
//                        int run = Integer.parseInt(getColumnValue(line, headers, "Run"));
//                        int generation = Integer.parseInt(getColumnValue(line, headers, "Generation"));
//                        double testFitness = Double.parseDouble(getColumnValue(line, headers, "TestFitness"));
//                        results.add(new ResultRow(scenario, algoName, run, generation, testFitness));
//                    }
//                } catch (Exception e) {
//                    System.err.println("Cannot read file: " + filePath);
//                    e.printStackTrace();
//                }
//            }
//        }
//
//        return results;
//    }
//
//    private static String determineTestFile(int i) {
//        String testfile;
//        if(i%3==0&&i<12){
//            double sign1 = 0.75; int sign2 = 60;
//            testfile = "missing-"+sign1+"-1.3-"+sign2+".csv";
//        } else if (i%3==1&&i<12) {
//            double sign1 = 0.85; int sign2 = 60;
//            testfile = "missing-"+sign1+"-1.3-"+sign2+".csv";
//        } else if (i%3==2&&i<12) {
//            double sign1 = 0.95; int sign2 = 60;
//            testfile = "missing-"+sign1+"-1.3-"+sign2+".csv";
//        } else if (i%3==0&&i>=12) {
//            double sign1 = 0.75; int sign2 = 80;
//            testfile = "missing-"+sign1+"-1.3-"+sign2+".csv";
//        } else if (i%3==1&&i>=12) {
//            double sign1 = 0.85; int sign2 = 80;
//            testfile = "missing-"+sign1+"-1.3-"+sign2+".csv";
//        }else if (i%3==2&&i>=12) {
//            double sign1 = 0.95; int sign2 = 80;
//            testfile = "missing-"+sign1+"-1.3-"+sign2+".csv";
//        }else {
//            testfile ="error";
//            System.out.println("error");
//        }
//        return testfile;
//    }
//
//    private static List<FinalData> extractFinalData(List<ResultRow> allResults) {
//        List<FinalData> finalData = new ArrayList<>();
//
//        for (String instance : scenarios) {
//            for (String algo : ALGOS_NAME) {
//                // Group by run
//                Map<Integer, List<ResultRow>> runsMap = allResults.stream()
//                        .filter(r -> r.scenario.equals(instance) && r.algo.equals(algo))
//                        .collect(Collectors.groupingBy(r -> r.run));
//
//                // For each run, find the row with minimum TestFitness
//                for (Map.Entry<Integer, List<ResultRow>> entry : runsMap.entrySet()) {
//                    int run = entry.getKey();
//                    List<ResultRow> rowsForRun = entry.getValue();
//
//                    ResultRow bestRow = rowsForRun.stream()
//                            .min(Comparator.comparingDouble(r -> r.testFitness))
//                            .orElse(null);
//
//                    if (bestRow != null) {
//                        finalData.add(new FinalData(instance, algo, run, bestRow.testFitness));
//                    }
//                }
//            }
//        }
//
//        return finalData;
//    }
//
//    private static void performComparison(List<FinalData> finalData) {
//        int nInstances = scenarios.length;
//        int nAlgos = ALGOS_NAME.length;
//
//        double[][] meanMatrix = new double[nInstances][nAlgos];
//        double[][] sdMatrix = new double[nInstances][nAlgos];
//        double[][] pMatrix = new double[nInstances][nAlgos - 1];
//
//        String lastAlgo = ALGOS_NAME[nAlgos - 1];
//
//        // Store best values per instance for ranking
//        double[][] instanceBestValues = new double[nInstances][nAlgos];
//
//        for (int s = 0; s < nInstances; s++) {
//            String instance = scenarios[s];
//
//            // Get data for last algorithm
//            List<Double> lastAlgoValues = finalData.stream()
//                    .filter(f -> f.scenario.equals(instance) && f.algo.equals(lastAlgo))
//                    .map(f -> f.value)
//                    .collect(Collectors.toList());
//
//            // Calculate mean and SD for last algorithm
//            meanMatrix[s][nAlgos - 1] = calculateMean(lastAlgoValues);
//            sdMatrix[s][nAlgos - 1] = calculateSD(lastAlgoValues, meanMatrix[s][nAlgos - 1]);
//            instanceBestValues[s][nAlgos - 1] = meanMatrix[s][nAlgos - 1];
//
//            // Compare with other algorithms
//            for (int a = 0; a < nAlgos - 1; a++) {
//                String algo = ALGOS_NAME[a];
//
//                List<Double> algoValues = finalData.stream()
//                        .filter(f -> f.scenario.equals(instance) && f.algo.equals(algo))
//                        .map(f -> f.value)
//                        .collect(Collectors.toList());
//
//                meanMatrix[s][a] = calculateMean(algoValues);
//                sdMatrix[s][a] = calculateSD(algoValues, meanMatrix[s][a]);
//                instanceBestValues[s][a] = meanMatrix[s][a];
//
//                try {
//                    pMatrix[s][a] = performWilcoxonTest(lastAlgoValues, algoValues);
//                } catch (Exception e) {
//                    pMatrix[s][a] = 1.0;
//                }
//            }
//        }
//
//        // Calculate mean ranks and Friedman test
//        double[] meanRanks = calculateMeanRanks(instanceBestValues);
//        double friedmanPValue = performFriedmanTestManual(instanceBestValues);
//
//        // Print results with mean-rank row
//        printResults(meanMatrix, sdMatrix, pMatrix, meanRanks, friedmanPValue);
//    }
//
//
//    private static double[] calculateMeanRanks(double[][] instanceBestValues) {
//        int nInstances = instanceBestValues.length;
//        int nAlgos = instanceBestValues[0].length;
//        double[] meanRanks = new double[nAlgos];
//
//        // For each instance, assign ranks to algorithms (lower fitness = better rank)
//        for (int s = 0; s < nInstances; s++) {
//            // Create list of pairs (algoIndex, fitnessValue)
//            List<Map.Entry<Integer, Double>> fitnessList = new ArrayList<>();
//            for (int a = 0; a < nAlgos; a++) {
//                fitnessList.add(new AbstractMap.SimpleEntry<>(a, instanceBestValues[s][a]));
//            }
//
//            // Sort by fitness (ascending)
//            fitnessList.sort(Map.Entry.comparingByValue());
//
//            // Assign ranks (handling ties)
//            double[] ranks = new double[nAlgos];
//            for (int i = 0; i < nAlgos; i++) {
//                int j = i;
//                while (j < nAlgos - 1 &&
//                        Math.abs(fitnessList.get(j).getValue() - fitnessList.get(j + 1).getValue()) < 1e-10) {
//                    j++;
//                }
//
//                if (i == j) {
//                    ranks[i] = i + 1;
//                } else {
//                    double avgRank = (i + 1 + j + 1) / 2.0;
//                    for (int k = i; k <= j; k++) {
//                        ranks[k] = avgRank;
//                    }
//                    i = j;
//                }
//            }
//
//            // Add ranks to cumulative sum
//            for (int i = 0; i < nAlgos; i++) {
//                int algoIdx = fitnessList.get(i).getKey();
//                meanRanks[algoIdx] += ranks[i];
//            }
//        }
//
//        // Calculate mean ranks
//        for (int a = 0; a < nAlgos; a++) {
//            meanRanks[a] /= nInstances;
//        }
//
//        return meanRanks;
//    }
//
//    // Manual implementation of Friedman test
//    private static double performFriedmanTestManual(double[][] data) {
//        int nInstances = data.length;      // number of instances (k)
//        int nAlgos = data[0].length;       // number of algorithms (m)
//
//        System.out.println("\n=== Friedman Test Details ===");
//        System.out.println("Number of instances (k): " + nInstances);
//        System.out.println("Number of algorithms (m): " + nAlgos);
//
//        // Calculate ranks for each instance
//        double[][] ranks = new double[nInstances][nAlgos];
//
//        for (int i = 0; i < nInstances; i++) {
//            // Create list of (value, index) pairs
//            List<ValueIndex> list = new ArrayList<>();
//            for (int j = 0; j < nAlgos; j++) {
//                list.add(new ValueIndex(data[i][j], j));
//            }
//
//            // Sort by value (ascending)
//            list.sort((a, b) -> Double.compare(a.value, b.value));
//
//            // Assign ranks with tie handling
//            int pos = 0;
//            while (pos < nAlgos) {
//                int start = pos;
//                double currentValue = list.get(pos).value;
//                while (pos < nAlgos && Math.abs(list.get(pos).value - currentValue) < 1e-10) {
//                    pos++;
//                }
//                // Average rank for tied values
//                double avgRank = (start + 1 + pos) / 2.0;
//                for (int k = start; k < pos; k++) {
//                    ranks[i][list.get(k).index] = avgRank;
//                }
//            }
//        }
//
//        // Calculate mean rank for each algorithm
//        double[] meanRanks = new double[nAlgos];
//        for (int j = 0; j < nAlgos; j++) {
//            double sum = 0;
//            for (int i = 0; i < nInstances; i++) {
//                sum += ranks[i][j];
//            }
//            meanRanks[j] = sum / nInstances;
//        }
//
//        System.out.println("Mean ranks for each algorithm:");
//        for (int j = 0; j < nAlgos; j++) {
//            System.out.printf("  %s: %.4f\n", ALGOS_NAME[j], meanRanks[j]);
//        }
//
//        // Calculate Friedman statistic
//        // Formula: χ?_F = [12k / (m(m+1))] * [Σ(R_j?) - m(m+1)?/4]
//        double sumSqRanks = 0;
//        for (int j = 0; j < nAlgos; j++) {
//            sumSqRanks += meanRanks[j] * meanRanks[j];
//        }
//
//        double friedmanStat = (12.0 * nInstances / (nAlgos * (nAlgos + 1))) *
//                (sumSqRanks - nAlgos * Math.pow((nAlgos + 1), 2) / 4.0);
//
//        int df = nAlgos - 1;
//        double pValue = chiSquareCDF(friedmanStat, df);
//
//        System.out.printf("Friedman χ? statistic: %.4f\n", friedmanStat);
//        System.out.printf("Degrees of freedom: %d\n", df);
//        System.out.printf("P-value: %.6f\n", pValue);
//        System.out.println("===========================\n");
//
//        return pValue;
//    }
//
//    static class ValueIndex {
//        double value;
//        int index;
//        ValueIndex(double v, int i) { value = v; index = i; }
//    }
//
//    // Chi-square distribution CDF approximation using incomplete gamma function
//    private static double chiSquareCDF(double x, int df) {
//        if (x <= 0) return 1.0;  // For x <= 0, p-value = 1
//        double a = df / 2.0;
//        double b = x / 2.0;
//        return 1 - incompleteGamma(a, b);
//    }
//
//    // Incomplete gamma function P(a, x) approximation
//    private static double incompleteGamma(double a, double x) {
//        if (x < a + 1) {
//            // Use series expansion
//            double sum = 1.0 / a;
//            double term = sum;
//            for (int i = 1; i < 100; i++) {
//                term *= x / (a + i);
//                sum += term;
//                if (term < 1e-10) break;
//            }
//            double result = Math.pow(x, a) * Math.exp(-x) * sum;
//            return Math.min(result, 1.0);
//        } else {
//            // Use continued fraction for better accuracy
//            double a1 = 1 - a;
//            double b1 = x + 1 - a;
//            double c = 1 / 1e-30;
//            double d = 1 / b1;
//            double h = d;
//            for (int i = 1; i < 100; i++) {
//                double a2 = i * (a - i);
//                b1 += 2;
//                d = a2 * d + b1;
//                if (Math.abs(d) < 1e-30) d = 1e-30;
//                c = b1 + a2 / c;
//                if (Math.abs(c) < 1e-30) c = 1e-30;
//                d = 1 / d;
//                double delta = d * c;
//                h *= delta;
//                if (Math.abs(delta - 1) < 1e-10) break;
//            }
//            double result = Math.pow(x, a) * Math.exp(-x) * h;
//            return 1 - Math.min(result, 1.0);
//        }
//    }
//
//    private static double calculateMean(List<Double> values) {
//        return values.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
//    }
//
//    private static double calculateSD(List<Double> values, double mean) {
//        if (values.size() <= 1) return 0.0;
//        double sum = 0.0;
//        for (double v : values) {
//            sum += Math.pow(v - mean, 2);
//        }
//        return Math.sqrt(sum / (values.size() - 1));
//    }
//
//    private static double performWilcoxonTest(List<Double> group1, List<Double> group2) {
//        double[] array1 = group1.stream().mapToDouble(Double::doubleValue).toArray();
//        double[] array2 = group2.stream().mapToDouble(Double::doubleValue).toArray();
//        return calculateMannWhitneyU(array1, array2);
//    }
//
//    private static double calculateMannWhitneyU(double[] group1, double[] group2) {
//        List<ValueWithGroup> combined = new ArrayList<>();
//        for (double v : group1) {
//            combined.add(new ValueWithGroup(v, 1));
//        }
//        for (double v : group2) {
//            combined.add(new ValueWithGroup(v, 2));
//        }
//
//        combined.sort(Comparator.comparingDouble(v -> v.value));
//
//        double[] ranks = new double[combined.size()];
//        for (int i = 0; i < combined.size(); i++) {
//            int j = i;
//            while (j < combined.size() - 1 &&
//                    Math.abs(combined.get(j).value - combined.get(j + 1).value) < 1e-10) {
//                j++;
//            }
//
//            if (i == j) {
//                ranks[i] = i + 1;
//            } else {
//                double avgRank = (i + 1 + j + 1) / 2.0;
//                for (int k = i; k <= j; k++) {
//                    ranks[k] = avgRank;
//                }
//                i = j;
//            }
//        }
//
//        double rankSum1 = 0;
//        for (int i = 0; i < combined.size(); i++) {
//            if (combined.get(i).group == 1) {
//                rankSum1 += ranks[i];
//            }
//        }
//
//        double n1 = group1.length;
//        double n2 = group2.length;
//        double u1 = rankSum1 - (n1 * (n1 + 1) / 2.0);
//        double u2 = n1 * n2 - u1;
//        double u = Math.min(u1, u2);
//
//        double meanU = n1 * n2 / 2.0;
//        double sdU = Math.sqrt(n1 * n2 * (n1 + n2 + 1) / 12.0);
//        double z = (u - meanU) / sdU;
//
//        return 2 * (1 - normalCDF(Math.abs(z)));
//    }
//
//    private static double normalCDF(double x) {
//        return 0.5 * (1 + erf(x / Math.sqrt(2)));
//    }
//
//    private static double erf(double x) {
//        double t = 1.0 / (1.0 + 0.5 * Math.abs(x));
//        double tau = t * Math.exp(-x * x - 1.26551223 +
//                1.00002368 * t + 0.37409196 * Math.pow(t, 2) +
//                0.09678418 * Math.pow(t, 3) - 0.18628806 * Math.pow(t, 4) +
//                0.27886807 * Math.pow(t, 5) - 1.13520398 * Math.pow(t, 6) +
//                1.48851587 * Math.pow(t, 7) - 0.82215223 * Math.pow(t, 8) +
//                0.17087277 * Math.pow(t, 9));
//        return x >= 0 ? 1 - tau : tau - 1;
//    }
//
//    static class ValueWithGroup {
//        double value;
//        int group;
//
//        ValueWithGroup(double value, int group) {
//            this.value = value;
//            this.group = group;
//        }
//    }
//
////    private static void printResults(double[][] meanMatrix, double[][] sdMatrix,
////                                     double[][] pMatrix, double[] meanRanks, double friedmanPValue) {
////        int nInstances = scenarios.length;
////        int nAlgos = ALGOS_NAME.length;
////
////        // Print header
////        System.out.print("Instance");
////        for (int a = 0; a < nAlgos - 1; a++) {
////            System.out.printf(" & %s", ALGOS_NAME[a]);
////        }
////        System.out.printf(" & %s \\\\\n", ALGOS_NAME[nAlgos - 1]);
////        System.out.println("\\hline");
////
////        // Print each instance row with simplified names
////        for (int s = 0; s < nInstances; s++) {
////            String simplifiedName = simplifyInstanceName(scenarios[s]);
////            System.out.printf("%s", simplifiedName);
////
////            for (int a = 0; a < nAlgos - 1; a++) {
////                double pValue = pMatrix[s][a];
////                double mean = meanMatrix[s][a];
////                double sd = sdMatrix[s][a];
////                double lastAlgoMean = meanMatrix[s][nAlgos - 1];
////
////                if (pValue < 0.05) {
////                    if (mean < lastAlgoMean) {
////                        System.out.printf(" & %.2f(%.2f) {\\bf(--)} (%.4f)",
////                                mean, sd, pValue);
////                    } else {
////                        System.out.printf(" & %.2f(%.2f) {\\bf(+)} (%.4f)",
////                                mean, sd, pValue);
////                    }
////                } else {
////                    System.out.printf(" & %.2f(%.2f) {($\\approx$)} (%.4f)",
////                            mean, sd, pValue);
////                }
////            }
////
////            System.out.printf(" & %.2f(%.2f) \\\\\n",
////                    meanMatrix[s][nAlgos - 1],
////                    sdMatrix[s][nAlgos - 1]);
////        }
////
////        // Print mean-rank row
////        System.out.println("\\hline");
////        System.out.print("Mean-rank");
////        for (int a = 0; a < nAlgos; a++) {
////            System.out.printf(" & %.2f", meanRanks[a]);
////        }
////        System.out.println(" \\\\");
////
////        // Print Friedman p-value row
////        System.out.print("Friedman p-value");
////        for (int a = 0; a < nAlgos; a++) {
////            if (a == 0) {
////                System.out.printf(" & %.6f", friedmanPValue);
////            } else {
////                System.out.print(" & ");
////            }
////        }
////        System.out.println(" \\\\");
////    }
//
//    private static void printResults(double[][] meanMatrix, double[][] sdMatrix,
//                                     double[][] pMatrix, double[] meanRanks, double friedmanPValue) {
//        int nInstances = scenarios.length;
//        int nAlgos = ALGOS_NAME.length;
//
//        // Print header
//        System.out.print("Instance");
//        for (int a = 0; a < nAlgos - 1; a++) {
//            System.out.printf(" & %s", ALGOS_NAME[a]);
//        }
//        System.out.printf(" & %s \\\\\n", ALGOS_NAME[nAlgos - 1]);
//        System.out.println("\\hline");
//
//        // Print each instance row with simplified names
//        for (int s = 0; s < nInstances; s++) {
//            String simplifiedName = simplifyInstanceName(scenarios[s]);
//            System.out.printf("%s", simplifiedName);
//
//            for (int a = 0; a < nAlgos - 1; a++) {
//                double pValue = pMatrix[s][a];
//                double mean = meanMatrix[s][a];
//                double sd = sdMatrix[s][a];
//                double lastAlgoMean = meanMatrix[s][nAlgos - 1];
//
//                if (Double.isNaN(pValue)) {
//                    System.out.printf(" & %.2f(%.2f) {($\\approx$)} (---)",
//                            mean, sd);
//                } else if (pValue < 0.05) {
//                    if (mean < lastAlgoMean) {
//                        System.out.printf(" & %.2f(%.2f) {\\bf(--)} (%.4f)",
//                                mean, sd, pValue);
//                    } else {
//                        System.out.printf(" & %.2f(%.2f) {\\bf(+)} (%.4f)",
//                                mean, sd, pValue);
//                    }
//                } else {
//                    System.out.printf(" & %.2f(%.2f) {($\\approx$)} (%.4f)",
//                            mean, sd, pValue);
//                }
//            }
//
//            System.out.printf(" & %.2f(%.2f) \\\\\n",
//                    meanMatrix[s][nAlgos - 1],
//                    sdMatrix[s][nAlgos - 1]);
//        }
//
//        // Print mean-rank row
//        System.out.println("\\hline");
//        System.out.print("Mean-rank");
//        for (int a = 0; a < nAlgos; a++) {
//            System.out.printf(" & %.2f", meanRanks[a]);
//        }
//        System.out.println(" \\\\");
//
//        // Print Friedman p-value row - 修正：让p-value跨所有列居中显示
//        System.out.print("Friedman p-value");
//        System.out.printf(" & \\multicolumn{%d}{c}{%.6f} \\\\\n", nAlgos, friedmanPValue);
//    }
//
//    private static String simplifyInstanceName(String fullName) {
//        // 使用手动映射，避免字符串解析错误
//        switch (fullName) {
//            case "mean-flowtime-0.75-1.3-60":
//                return "Fmean-0.75-60";
//            case "mean-flowtime-0.85-1.3-60":
//                return "Fmean-0.85-60";
//            case "mean-flowtime-0.95-1.3-60":
//                return "Fmean-0.95-60";
//            case "energyConsumption-0.75-1.3-60":
//                return "TEC-0.75-60";
//            case "energyConsumption-0.85-1.3-60":
//                return "TEC-0.85-60";
//            case "energyConsumption-0.95-1.3-60":
//                return "TEC-0.95-60";
//            case "mean-tardiness-0.75-1.3-60":
//                return "Tmean-0.75-60";
//            case "mean-tardiness-0.85-1.3-60":
//                return "Tmean-0.85-60";
//            case "mean-tardiness-0.95-1.3-60":
//                return "Tmean-0.95-60";
//            case "makespan-0.75-1.3-60":
//                return "Cmax-0.75-60";
//            case "makespan-0.85-1.3-60":
//                return "Cmax-0.85-60";
//            case "makespan-0.95-1.3-60":
//                return "Cmax-0.95-60";
//            case "mean-flowtime-0.75-1.3-80":
//                return "Fmean-0.75-80";
//            case "mean-flowtime-0.85-1.3-80":
//                return "Fmean-0.85-80";
//            case "mean-flowtime-0.95-1.3-80":
//                return "Fmean-0.95-80";
//            case "energyConsumption-0.75-1.3-80":
//                return "TEC-0.75-80";
//            case "energyConsumption-0.85-1.3-80":
//                return "TEC-0.85-80";
//            case "energyConsumption-0.95-1.3-80":
//                return "TEC-0.95-80";
//            case "mean-tardiness-0.75-1.3-80":
//                return "Tmean-0.75-80";
//            case "mean-tardiness-0.85-1.3-80":
//                return "Tmean-0.85-80";
//            case "mean-tardiness-0.95-1.3-80":
//                return "Tmean-0.95-80";
//            case "makespan-0.75-1.3-80":
//                return "Cmax-0.75-80";
//            case "makespan-0.85-1.3-80":
//                return "Cmax-0.85-80";
//            case "makespan-0.95-1.3-80":
//                return "Cmax-0.95-80";
//            default:
//                return fullName;
//        }
//    }
//
//    private static String getColumnValue(String[] line, String[] headers, String colName) {
//        for (int i = 0; i < headers.length; i++) {
//            if (headers[i].equals(colName)) {
//                return line[i];
//            }
//        }
//        throw new RuntimeException("Column not found: " + colName);
//    }
//}


package yimei.jss;

import com.opencsv.CSVReader;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class WilcoxonComparison {

    // Configuration - update these based on your needs
    private static final String[] ALGOS = {"MTGP-tugboat-dynamic1","MTGP-tugboat-dynamic2", "MTGP-tugboat-dynamic3"};
    private static final String[] ALGOS_NAME = {"MTGP-CAIS", "MTGP-GS", "MTGP-IIS"};

    private static final String[] scenarios = {
            "mean-flowtime-0.75-1.3-60",   //0
            "mean-flowtime-0.85-1.3-60",   //1
            "mean-flowtime-0.95-1.3-60",   //2
            "energyConsumption-0.75-1.3-60",
            "energyConsumption-0.85-1.3-60",
            "energyConsumption-0.95-1.3-60",
            "mean-tardiness-0.75-1.3-60",  //6
            "mean-tardiness-0.85-1.3-60",  //7
            "mean-tardiness-0.95-1.3-60",  //8
            "makespan-0.75-1.3-60",
            "makespan-0.85-1.3-60",
            "makespan-0.95-1.3-60",
            "mean-flowtime-0.75-1.3-80",   //12
            "mean-flowtime-0.85-1.3-80",   //13
            "mean-flowtime-0.95-1.3-80",   //14
            "energyConsumption-0.75-1.3-80",
            "energyConsumption-0.85-1.3-80",
            "energyConsumption-0.95-1.3-80",
            "mean-tardiness-0.75-1.3-80",
            "mean-tardiness-0.85-1.3-80",
            "mean-tardiness-0.95-1.3-80",
            "makespan-0.75-1.3-80",
            "makespan-0.85-1.3-80",
            "makespan-0.95-1.3-80"
    };

    private static final String BASE_PATH = "/home/feige/Downloads/grid/MTGP-main/";

    // Helper method to format numbers with 5 significant digits in scientific notation
    private static String formatScientific(double value) {
        if (Double.isNaN(value)) return "NaN";
        if (value == 0) return "0.0000e+00";

        // Use String.format with scientific notation
        return String.format("%.4e", value);
    }

    // Helper method to format mean and SD together
    private static String formatMeanSD(double mean, double sd) {
        return String.format("%.4e(%.4e)", mean, sd);
    }

    // Data structure to hold results
    static class ResultRow {
        String scenario;
        String algo;
        int run;
        int generation;
        double testFitness;

        ResultRow(String scenario, String algo, int run, int generation, double testFitness) {
            this.scenario = scenario;
            this.algo = algo;
            this.run = run;
            this.generation = generation;
            this.testFitness = testFitness;
        }
    }

    static class FinalData {
        String scenario;
        String algo;
        int run;
        double value;

        FinalData(String scenario, String algo, int run, double value) {
            this.scenario = scenario;
            this.algo = algo;
            this.run = run;
            this.value = value;
        }
    }

    public static void main(String[] args) {
        try {
            // Read all data
            List<ResultRow> allResults = readAllData();

            // Extract final generation data (best fitness per run)
            List<FinalData> finalData = extractFinalData(allResults);

            // Perform statistical comparison
            performComparison(finalData);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static List<ResultRow> readAllData() throws IOException {
        List<ResultRow> results = new ArrayList<>();

        for (int s = 0; s < scenarios.length; s++) {
            String scenario = scenarios[s];
            String testFile = determineTestFile(s);

            for (int a = 0; a < ALGOS.length; a++) {
                String algo = ALGOS[a];
                String algoName = ALGOS_NAME[a];

                String filePath = BASE_PATH + algo + "/" +
                        scenario + "/test/" + testFile;

                System.out.println("Reading: " + filePath);

                try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
                    String[] headers = reader.readNext();
                    String[] line;
                    while ((line = reader.readNext()) != null) {
                        int run = Integer.parseInt(getColumnValue(line, headers, "Run"));
                        int generation = Integer.parseInt(getColumnValue(line, headers, "Generation"));
                        double testFitness = Double.parseDouble(getColumnValue(line, headers, "TestFitness"));
                        results.add(new ResultRow(scenario, algoName, run, generation, testFitness));
                    }
                } catch (Exception e) {
                    System.err.println("Cannot read file: " + filePath);
                    e.printStackTrace();
                }
            }
        }

        return results;
    }

    private static String determineTestFile(int i) {
        String testfile;
        if(i%3==0&&i<12){
            double sign1 = 0.75; int sign2 = 60;
            testfile = "missing-"+sign1+"-1.3-"+sign2+".csv";
        } else if (i%3==1&&i<12) {
            double sign1 = 0.85; int sign2 = 60;
            testfile = "missing-"+sign1+"-1.3-"+sign2+".csv";
        } else if (i%3==2&&i<12) {
            double sign1 = 0.95; int sign2 = 60;
            testfile = "missing-"+sign1+"-1.3-"+sign2+".csv";
        } else if (i%3==0&&i>=12) {
            double sign1 = 0.75; int sign2 = 80;
            testfile = "missing-"+sign1+"-1.3-"+sign2+".csv";
        } else if (i%3==1&&i>=12) {
            double sign1 = 0.85; int sign2 = 80;
            testfile = "missing-"+sign1+"-1.3-"+sign2+".csv";
        }else if (i%3==2&&i>=12) {
            double sign1 = 0.95; int sign2 = 80;
            testfile = "missing-"+sign1+"-1.3-"+sign2+".csv";
        }else {
            testfile ="error";
            System.out.println("error");
        }
        return testfile;
    }

    private static List<FinalData> extractFinalData(List<ResultRow> allResults) {
        List<FinalData> finalData = new ArrayList<>();

        for (String instance : scenarios) {
            for (String algo : ALGOS_NAME) {
                // Group by run
                Map<Integer, List<ResultRow>> runsMap = allResults.stream()
                        .filter(r -> r.scenario.equals(instance) && r.algo.equals(algo))
                        .collect(Collectors.groupingBy(r -> r.run));

                // For each run, find the row with minimum TestFitness
                for (Map.Entry<Integer, List<ResultRow>> entry : runsMap.entrySet()) {
                    int run = entry.getKey();
                    List<ResultRow> rowsForRun = entry.getValue();

                    ResultRow bestRow = rowsForRun.stream()
                            .min(Comparator.comparingDouble(r -> r.testFitness))
                            .orElse(null);

                    if (bestRow != null) {
                        finalData.add(new FinalData(instance, algo, run, bestRow.testFitness));
                    }
                }
            }
        }

        return finalData;
    }

    private static void performComparison(List<FinalData> finalData) {
        int nInstances = scenarios.length;
        int nAlgos = ALGOS_NAME.length;

        double[][] meanMatrix = new double[nInstances][nAlgos];
        double[][] sdMatrix = new double[nInstances][nAlgos];
        double[][] pMatrix = new double[nInstances][nAlgos - 1];

        String lastAlgo = ALGOS_NAME[nAlgos - 1];

        // Store best values per instance for ranking
        double[][] instanceBestValues = new double[nInstances][nAlgos];

        for (int s = 0; s < nInstances; s++) {
            String instance = scenarios[s];

            // Get data for last algorithm
            List<Double> lastAlgoValues = finalData.stream()
                    .filter(f -> f.scenario.equals(instance) && f.algo.equals(lastAlgo))
                    .map(f -> f.value)
                    .collect(Collectors.toList());

            // Calculate mean and SD for last algorithm
            meanMatrix[s][nAlgos - 1] = calculateMean(lastAlgoValues);
            sdMatrix[s][nAlgos - 1] = calculateSD(lastAlgoValues, meanMatrix[s][nAlgos - 1]);
            instanceBestValues[s][nAlgos - 1] = meanMatrix[s][nAlgos - 1];

            // Compare with other algorithms
            for (int a = 0; a < nAlgos - 1; a++) {
                String algo = ALGOS_NAME[a];

                List<Double> algoValues = finalData.stream()
                        .filter(f -> f.scenario.equals(instance) && f.algo.equals(algo))
                        .map(f -> f.value)
                        .collect(Collectors.toList());

                meanMatrix[s][a] = calculateMean(algoValues);
                sdMatrix[s][a] = calculateSD(algoValues, meanMatrix[s][a]);
                instanceBestValues[s][a] = meanMatrix[s][a];

                try {
                    pMatrix[s][a] = performWilcoxonTest(lastAlgoValues, algoValues);
                } catch (Exception e) {
                    pMatrix[s][a] = 1.0;
                }
            }
        }

        // Calculate mean ranks and Friedman test
        double[] meanRanks = calculateMeanRanks(instanceBestValues);
        double friedmanPValue = performFriedmanTestManual(instanceBestValues);

        // Print results with mean-rank row
        printResults(meanMatrix, sdMatrix, pMatrix, meanRanks, friedmanPValue);
    }


    private static double[] calculateMeanRanks(double[][] instanceBestValues) {
        int nInstances = instanceBestValues.length;
        int nAlgos = instanceBestValues[0].length;
        double[] meanRanks = new double[nAlgos];

        // For each instance, assign ranks to algorithms (lower fitness = better rank)
        for (int s = 0; s < nInstances; s++) {
            // Create list of pairs (algoIndex, fitnessValue)
            List<Map.Entry<Integer, Double>> fitnessList = new ArrayList<>();
            for (int a = 0; a < nAlgos; a++) {
                fitnessList.add(new AbstractMap.SimpleEntry<>(a, instanceBestValues[s][a]));
            }

            // Sort by fitness (ascending)
            fitnessList.sort(Map.Entry.comparingByValue());

            // Assign ranks (handling ties)
            double[] ranks = new double[nAlgos];
            for (int i = 0; i < nAlgos; i++) {
                int j = i;
                while (j < nAlgos - 1 &&
                        Math.abs(fitnessList.get(j).getValue() - fitnessList.get(j + 1).getValue()) < 1e-10) {
                    j++;
                }

                if (i == j) {
                    ranks[i] = i + 1;
                } else {
                    double avgRank = (i + 1 + j + 1) / 2.0;
                    for (int k = i; k <= j; k++) {
                        ranks[k] = avgRank;
                    }
                    i = j;
                }
            }

            // Add ranks to cumulative sum
            for (int i = 0; i < nAlgos; i++) {
                int algoIdx = fitnessList.get(i).getKey();
                meanRanks[algoIdx] += ranks[i];
            }
        }

        // Calculate mean ranks
        for (int a = 0; a < nAlgos; a++) {
            meanRanks[a] /= nInstances;
        }

        return meanRanks;
    }

    // Manual implementation of Friedman test
    private static double performFriedmanTestManual(double[][] data) {
        int nInstances = data.length;      // number of instances (k)
        int nAlgos = data[0].length;       // number of algorithms (m)

        System.out.println("\n=== Friedman Test Details ===");
        System.out.println("Number of instances (k): " + nInstances);
        System.out.println("Number of algorithms (m): " + nAlgos);

        // Calculate ranks for each instance
        double[][] ranks = new double[nInstances][nAlgos];

        for (int i = 0; i < nInstances; i++) {
            // Create list of (value, index) pairs
            List<ValueIndex> list = new ArrayList<>();
            for (int j = 0; j < nAlgos; j++) {
                list.add(new ValueIndex(data[i][j], j));
            }

            // Sort by value (ascending)
            list.sort((a, b) -> Double.compare(a.value, b.value));

            // Assign ranks with tie handling
            int pos = 0;
            while (pos < nAlgos) {
                int start = pos;
                double currentValue = list.get(pos).value;
                while (pos < nAlgos && Math.abs(list.get(pos).value - currentValue) < 1e-10) {
                    pos++;
                }
                // Average rank for tied values
                double avgRank = (start + 1 + pos) / 2.0;
                for (int k = start; k < pos; k++) {
                    ranks[i][list.get(k).index] = avgRank;
                }
            }
        }

        // Calculate mean rank for each algorithm
        double[] meanRanks = new double[nAlgos];
        for (int j = 0; j < nAlgos; j++) {
            double sum = 0;
            for (int i = 0; i < nInstances; i++) {
                sum += ranks[i][j];
            }
            meanRanks[j] = sum / nInstances;
        }

        System.out.println("Mean ranks for each algorithm:");
        for (int j = 0; j < nAlgos; j++) {
            System.out.printf("  %s: %.4f\n", ALGOS_NAME[j], meanRanks[j]);
        }

        // Calculate Friedman statistic
        // Formula: χ?_F = [12k / (m(m+1))] * [Σ(R_j?) - m(m+1)?/4]
        double sumSqRanks = 0;
        for (int j = 0; j < nAlgos; j++) {
            sumSqRanks += meanRanks[j] * meanRanks[j];
        }

        double friedmanStat = (12.0 * nInstances / (nAlgos * (nAlgos + 1))) *
                (sumSqRanks - nAlgos * Math.pow((nAlgos + 1), 2) / 4.0);

        int df = nAlgos - 1;
        double pValue = chiSquareCDF(friedmanStat, df);

        System.out.printf("Friedman χ? statistic: %.4f\n", friedmanStat);
        System.out.printf("Degrees of freedom: %d\n", df);
        System.out.printf("P-value: %.6e\n", pValue);
        System.out.println("===========================\n");

        return pValue;
    }

    static class ValueIndex {
        double value;
        int index;
        ValueIndex(double v, int i) { value = v; index = i; }
    }

    // Chi-square distribution CDF approximation using incomplete gamma function
    private static double chiSquareCDF(double x, int df) {
        if (x <= 0) return 1.0;  // For x <= 0, p-value = 1
        double a = df / 2.0;
        double b = x / 2.0;
        return 1 - incompleteGamma(a, b);
    }

    // Incomplete gamma function P(a, x) approximation
    private static double incompleteGamma(double a, double x) {
        if (x < a + 1) {
            // Use series expansion
            double sum = 1.0 / a;
            double term = sum;
            for (int i = 1; i < 100; i++) {
                term *= x / (a + i);
                sum += term;
                if (term < 1e-10) break;
            }
            double result = Math.pow(x, a) * Math.exp(-x) * sum;
            return Math.min(result, 1.0);
        } else {
            // Use continued fraction for better accuracy
            double a1 = 1 - a;
            double b1 = x + 1 - a;
            double c = 1 / 1e-30;
            double d = 1 / b1;
            double h = d;
            for (int i = 1; i < 100; i++) {
                double a2 = i * (a - i);
                b1 += 2;
                d = a2 * d + b1;
                if (Math.abs(d) < 1e-30) d = 1e-30;
                c = b1 + a2 / c;
                if (Math.abs(c) < 1e-30) c = 1e-30;
                d = 1 / d;
                double delta = d * c;
                h *= delta;
                if (Math.abs(delta - 1) < 1e-10) break;
            }
            double result = Math.pow(x, a) * Math.exp(-x) * h;
            return 1 - Math.min(result, 1.0);
        }
    }

    private static double calculateMean(List<Double> values) {
        return values.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
    }

    private static double calculateSD(List<Double> values, double mean) {
        if (values.size() <= 1) return 0.0;
        double sum = 0.0;
        for (double v : values) {
            sum += Math.pow(v - mean, 2);
        }
        return Math.sqrt(sum / (values.size() - 1));
    }

    private static double performWilcoxonTest(List<Double> group1, List<Double> group2) {
        double[] array1 = group1.stream().mapToDouble(Double::doubleValue).toArray();
        double[] array2 = group2.stream().mapToDouble(Double::doubleValue).toArray();
        return calculateMannWhitneyU(array1, array2);
    }

    private static double calculateMannWhitneyU(double[] group1, double[] group2) {
        List<ValueWithGroup> combined = new ArrayList<>();
        for (double v : group1) {
            combined.add(new ValueWithGroup(v, 1));
        }
        for (double v : group2) {
            combined.add(new ValueWithGroup(v, 2));
        }

        combined.sort(Comparator.comparingDouble(v -> v.value));

        double[] ranks = new double[combined.size()];
        for (int i = 0; i < combined.size(); i++) {
            int j = i;
            while (j < combined.size() - 1 &&
                    Math.abs(combined.get(j).value - combined.get(j + 1).value) < 1e-10) {
                j++;
            }

            if (i == j) {
                ranks[i] = i + 1;
            } else {
                double avgRank = (i + 1 + j + 1) / 2.0;
                for (int k = i; k <= j; k++) {
                    ranks[k] = avgRank;
                }
                i = j;
            }
        }

        double rankSum1 = 0;
        for (int i = 0; i < combined.size(); i++) {
            if (combined.get(i).group == 1) {
                rankSum1 += ranks[i];
            }
        }

        double n1 = group1.length;
        double n2 = group2.length;
        double u1 = rankSum1 - (n1 * (n1 + 1) / 2.0);
        double u2 = n1 * n2 - u1;
        double u = Math.min(u1, u2);

        double meanU = n1 * n2 / 2.0;
        double sdU = Math.sqrt(n1 * n2 * (n1 + n2 + 1) / 12.0);
        double z = (u - meanU) / sdU;

        return 2 * (1 - normalCDF(Math.abs(z)));
    }

    private static double normalCDF(double x) {
        return 0.5 * (1 + erf(x / Math.sqrt(2)));
    }

    private static double erf(double x) {
        double t = 1.0 / (1.0 + 0.5 * Math.abs(x));
        double tau = t * Math.exp(-x * x - 1.26551223 +
                1.00002368 * t + 0.37409196 * Math.pow(t, 2) +
                0.09678418 * Math.pow(t, 3) - 0.18628806 * Math.pow(t, 4) +
                0.27886807 * Math.pow(t, 5) - 1.13520398 * Math.pow(t, 6) +
                1.48851587 * Math.pow(t, 7) - 0.82215223 * Math.pow(t, 8) +
                0.17087277 * Math.pow(t, 9));
        return x >= 0 ? 1 - tau : tau - 1;
    }

    static class ValueWithGroup {
        double value;
        int group;

        ValueWithGroup(double value, int group) {
            this.value = value;
            this.group = group;
        }
    }

    private static void printResults(double[][] meanMatrix, double[][] sdMatrix,
                                     double[][] pMatrix, double[] meanRanks, double friedmanPValue) {
        int nInstances = scenarios.length;
        int nAlgos = ALGOS_NAME.length;

        // Print header
        System.out.print("Instance");
        for (int a = 0; a < nAlgos - 1; a++) {
            System.out.printf(" & %s", ALGOS_NAME[a]);
        }
        System.out.printf(" & %s \\\\\n", ALGOS_NAME[nAlgos - 1]);
        System.out.println("\\hline");

        // Print each instance row with simplified names
        for (int s = 0; s < nInstances; s++) {
            String simplifiedName = simplifyInstanceName(scenarios[s]);
            System.out.printf("%s", simplifiedName);

            for (int a = 0; a < nAlgos - 1; a++) {
                double pValue = pMatrix[s][a];
                double mean = meanMatrix[s][a];
                double sd = sdMatrix[s][a];
                double lastAlgoMean = meanMatrix[s][nAlgos - 1];

                if (Double.isNaN(pValue)) {
                    System.out.printf(" & %.4e(%.4e) {($\\approx$)} (---)",
                            mean, sd);
                } else if (pValue < 0.05) {
                    if (mean < lastAlgoMean) {
                        System.out.printf(" & %.4e(%.4e) {\\bf(--)} (%.4e)",
                                mean, sd, pValue);
                    } else {
                        System.out.printf(" & %.4e(%.4e) {\\bf(+)} (%.4e)",
                                mean, sd, pValue);
                    }
                } else {
                    System.out.printf(" & %.4e(%.4e) {($\\approx$)} (%.4e)",
                            mean, sd, pValue);
                }
            }

            System.out.printf(" & %.4e(%.4e) \\\\\n",
                    meanMatrix[s][nAlgos - 1],
                    sdMatrix[s][nAlgos - 1]);
        }

        // Print mean-rank row
        System.out.println("\\hline");
        System.out.print("Mean-rank");
        for (int a = 0; a < nAlgos; a++) {
            System.out.printf(" & %.2f", meanRanks[a]);
        }
        System.out.println(" \\\\");

        // Print Friedman p-value row - spanning across all algorithm columns
        System.out.print("Friedman p-value");
        System.out.printf(" & \\multicolumn{%d}{c}{%.4e} \\\\\n", nAlgos, friedmanPValue);
    }

    private static String simplifyInstanceName(String fullName) {
        // Use manual mapping to avoid string parsing errors
        switch (fullName) {
            case "mean-flowtime-0.75-1.3-60":
                return "Fmean-0.75-60";
            case "mean-flowtime-0.85-1.3-60":
                return "Fmean-0.85-60";
            case "mean-flowtime-0.95-1.3-60":
                return "Fmean-0.95-60";
            case "energyConsumption-0.75-1.3-60":
                return "TEC-0.75-60";
            case "energyConsumption-0.85-1.3-60":
                return "TEC-0.85-60";
            case "energyConsumption-0.95-1.3-60":
                return "TEC-0.95-60";
            case "mean-tardiness-0.75-1.3-60":
                return "Tmean-0.75-60";
            case "mean-tardiness-0.85-1.3-60":
                return "Tmean-0.85-60";
            case "mean-tardiness-0.95-1.3-60":
                return "Tmean-0.95-60";
            case "makespan-0.75-1.3-60":
                return "Cmax-0.75-60";
            case "makespan-0.85-1.3-60":
                return "Cmax-0.85-60";
            case "makespan-0.95-1.3-60":
                return "Cmax-0.95-60";
            case "mean-flowtime-0.75-1.3-80":
                return "Fmean-0.75-80";
            case "mean-flowtime-0.85-1.3-80":
                return "Fmean-0.85-80";
            case "mean-flowtime-0.95-1.3-80":
                return "Fmean-0.95-80";
            case "energyConsumption-0.75-1.3-80":
                return "TEC-0.75-80";
            case "energyConsumption-0.85-1.3-80":
                return "TEC-0.85-80";
            case "energyConsumption-0.95-1.3-80":
                return "TEC-0.95-80";
            case "mean-tardiness-0.75-1.3-80":
                return "Tmean-0.75-80";
            case "mean-tardiness-0.85-1.3-80":
                return "Tmean-0.85-80";
            case "mean-tardiness-0.95-1.3-80":
                return "Tmean-0.95-80";
            case "makespan-0.75-1.3-80":
                return "Cmax-0.75-80";
            case "makespan-0.85-1.3-80":
                return "Cmax-0.85-80";
            case "makespan-0.95-1.3-80":
                return "Cmax-0.95-80";
            default:
                return fullName;
        }
    }

    private static String getColumnValue(String[] line, String[] headers, String colName) {
        for (int i = 0; i < headers.length; i++) {
            if (headers[i].equals(colName)) {
                return line[i];
            }
        }
        throw new RuntimeException("Column not found: " + colName);
    }
}