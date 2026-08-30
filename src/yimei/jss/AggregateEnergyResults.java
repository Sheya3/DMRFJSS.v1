package yimei.jss;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class AggregateEnergyResults {

    private static String BASE_DIR =
            "E:\\download\\grid\\MTGP-main1\\MTGP-tugboat-dynamic1\\mean-flowtime-0.75-1.3";

    private static final int NUM_JOBS = 30;

    public static void main(String[] args) throws Exception {
        String[] ob = {"energyConsumption","makespan","mean-flowtime","mean-tardiness"};
//        String[] ob = {"energyConsumption","mean-flowtime"};
        Double[] UL = {0.75,0.85,0.95};
        String[] DA = {"60", "80"}; // 可以根据实际有18个文件扩展

        for (int i = 0; i < ob.length; i++) {
            for (int j = 0; j < UL.length; j++) {
                for (int k = 0; k < DA.length; k++) {


                BASE_DIR =
//                        "E:\\download\\grid\\MTGP-main\\MTGP-tugboat-dynamic3\\"+ob[i]+"-"+UL[j]+"-1.5";
//                        "E:\\download\\grid\\TabuGP25\\MTGP-tugboat-dynamic1\\" + ob[i] + "-" + UL[j] + "-1.5";
                "E:\\download\\grid\\MTGP-main\\MTGP-tugboat-dynamic2\\"+ob[i]+"-"+UL[j]+"-1.3-"+DA[k];
//                "E:\\download\\grid\\MTGP-main\\MTGP-tugboat-dynamic1\\"+ob[i]+"-"+UL[j]+"-1.5-"+DA[k];
                aggregateFeatureCsv(
//                        "rouFeatureOccuranceFeq",
                        "rouFeatureOccurance",

//                        BASE_DIR + "/rouFeatureOccuranceFeq.csv"
                        BASE_DIR + "/rouFeatureOccurance.csv"

                );

//                aggregateFeatureCsv(
////                        "seqFeatureOccuranceFeq",
//                        "seqFeatureOccurance",
//
////                        BASE_DIR + "/seqFeatureOccuranceFeq.csv"
//                        BASE_DIR + "/seqFeatureOccurance.csv"
//
//                );

                aggregateTimeCsv(
                        BASE_DIR + "/time.txt"
                );
                }
            }
        }

        System.out.println("All aggregation finished successfully.");
    }

    /**
     * 汇总 30 个 job 的 rou/seq FeatureOccurance CSV
     * 按 generation + 每个 feature 求和
     */
    private static void aggregateFeatureCsv(String featureType, String outputPath) throws Exception {

        // generation -> sum of features
        Map<Integer, double[]> generationToSum = new TreeMap<>();
        String[] header = null;
        final int[] numFeaturesHolder = new int[1];


        for (int job = 0; job < NUM_JOBS; job++) {
            String filePath = BASE_DIR + "/job." + job + "." + featureType + ".csv";
            System.out.println("Reading: " + filePath);

            try (BufferedReader br = Files.newBufferedReader(Paths.get(filePath))) {

                String line = br.readLine(); // header
                if (line == null) continue;

                String[] cols = line.split(",");
                if (header == null) {
                    header = cols;
                    numFeaturesHolder[0] = cols.length - 1;
                }


                while ((line = br.readLine()) != null) {
                    if (line.trim().isEmpty()) continue;

                    String[] parts = line.split(",");
                    int generation = Integer.parseInt(parts[0].trim());

                    double[] sumArr = generationToSum.computeIfAbsent(
                            generation,
                            g -> new double[numFeaturesHolder[0]]
                    );


                    for (int i = 0; i < numFeaturesHolder[0]; i++) {
                        if (parts.length==14) {
                            int a=0;
                        }
                        double val = Double.parseDouble(parts[i + 1].trim());
                        sumArr[i] += val;
                    }
                }
            }
        }

        // 写汇总 CSV
        try (BufferedWriter bw = Files.newBufferedWriter(Paths.get(outputPath))) {

            // header
            bw.write(String.join(",", header));
            bw.newLine();

            for (Map.Entry<Integer, double[]> entry : generationToSum.entrySet()) {
                int generation = entry.getKey();
                double[] sums = entry.getValue();

                StringBuilder sb = new StringBuilder();
                sb.append(generation);
                for (double v : sums) {
                    sb.append(",").append(v);
                }
                bw.write(sb.toString());
                bw.newLine();
            }
        }

        System.out.println("Written aggregated feature file: " + outputPath);
    }

    /**
     * 处理 time.csv：
     * - 每个 job：所有代时间求和
     * - 30 个 job：求平均
     */
    private static void aggregateTimeCsv(String outputPath) throws Exception {

        double totalTimeAllJobs = 0.0;

        for (int job = 0; job < NUM_JOBS; job++) {
            String filePath = BASE_DIR + "/job." + job + ".time.csv";
            System.out.println("Reading: " + filePath);

            double jobTotal = 0.0;

            try (BufferedReader br = Files.newBufferedReader(Paths.get(filePath))) {

                String line = br.readLine(); // header
                if (line == null) continue;

                while ((line = br.readLine()) != null) {
                    if (line.trim().isEmpty()) continue;

                    String[] parts = line.split(",");

                    // 根据你上传的样例：第2列是 time
                    double timeVal = Double.parseDouble(parts[1].trim());
                    jobTotal += timeVal;
                }
            }

            totalTimeAllJobs += jobTotal;
            System.out.println("Job " + job + " total time = " + jobTotal);
        }

        double avgTime = totalTimeAllJobs / NUM_JOBS;

        try (BufferedWriter bw = Files.newBufferedWriter(Paths.get(outputPath))) {
            bw.write(String.valueOf(avgTime));
            bw.newLine();
        }

        System.out.println("Written average time to: " + outputPath);
        System.out.println("Average total time = " + avgTime);
    }
}
