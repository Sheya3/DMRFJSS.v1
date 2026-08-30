package yimei.jss;

import org.apache.commons.csv.*;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.inference.WilcoxonSignedRankTest;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

public class AlgoAnalysis {

    static class Record {
        String scenario;
        String algo;
        int run;
        int generation;
        double value;

        Record(String scenario, String algo, int run, int generation, double value) {
            this.scenario = scenario;
            this.algo = algo;
            this.run = run;
            this.generation = generation;
            this.value = value;
        }
    }

    public static void main(String[] args) throws IOException {
        // -------------------- 配置 --------------------
        //E:\download\grid\MTGP-main\MTGP-tugboat-dynamic1\energyConsumption-0.75-1.5\test
        String[] algos = {"MTGP-tugboat-dynamic1","MTGP-tugboat-dynamic3"};
        String[] algosName = {"MTGP1","MTGP3"};
        String[] instances = {
                "mean-flowtime-0.75-1.5",
                "mean-flowtime-0.85-1.5",
                "mean-flowtime-0.95-1.5",
                "makespan-0.75-1.5",
                "makespan-0.85-1.5",
                "makespan-0.95-1.5",
                "energyConsumption-0.75-1.5",
                "energyConsumption-0.85-1.5",
                "energyConsumption-0.95-1.5"
        };
        String col = "test-fit"; // 可以换成其他列
//        String basePath = "E:\\download\\grid\\MTGP-main\\";
        String basePath = "E:\\download\\grid\\TabuGP25\\";

        List<Record> finalRecords = new ArrayList<>();

        // -------------------- 读取 CSV --------------------
        for (int s = 0; s < instances.length; s++) {
            String instance = instances[s];
            String testfile;

            if (s == 1 || s == 4 || s == 7) testfile = "missing-0.85-1.5.csv";
            else if (s == 2 || s == 5 || s == 8) testfile = "missing-0.95-1.5.csv";
            else testfile = "missing-0.75-1.5.csv";

            for (int a = 0; a < algos.length; a++) {
                String algo = algos[a];
                //E:\download\grid\MTGP-main\MTGP-tugboat-dynamic1\energyConsumption-0.75-1.5\test
                String csvPath = Paths.get(basePath, algo, instance, "test", testfile).toString();

                try (CSVParser parser = new CSVParser(new FileReader(csvPath), CSVFormat.DEFAULT.withHeader())) {
                    for (CSVRecord rec : parser) {
                        int run = Integer.parseInt(rec.get("Run"));
                        int generation = Integer.parseInt(rec.get("Generation"));
                        double value = Double.parseDouble(rec.get("TestFitness"));
//                        double value = Double.parseDouble(rec.get("TrainFitness"));
                        finalRecords.add(new Record(instance, algosName[a], run, generation, value));
                    }
                } catch (Exception e) {
                    System.err.println("Failed to read " + csvPath + ": " + e.getMessage());
                }
            }
        }

        // -------------------- 获取最后一代 --------------------
        int maxGeneration = finalRecords.stream().mapToInt(r -> r.generation).max().orElse(0);
        List<Record> lastGen = new ArrayList<>();
        for (Record r : finalRecords) {
            if (r.generation == maxGeneration) lastGen.add(r);
        }

        // -------------------- 计算均值、标准差、Wilcoxon --------------------
        WilcoxonSignedRankTest wilcox = new WilcoxonSignedRankTest();

        for (String instance : instances) {
            System.out.print(instance + " ");
            double[] lastAlgoValues = lastGen.stream()
                    .filter(r -> r.scenario.equals(instance) && r.algo.equals(algosName[algosName.length - 1]))
                    .mapToDouble(r -> r.value).toArray();

            DescriptiveStatistics lastStats = new DescriptiveStatistics(lastAlgoValues);

            for (int a = 0; a < algosName.length - 1; a++) {
                final String s = algosName[a];
                double[] values = lastGen.stream()
                        .filter(r -> r.scenario.equals(instance) && r.algo.equals(s))
                        .mapToDouble(r -> r.value).toArray();

                DescriptiveStatistics stats = new DescriptiveStatistics(values);
                double pValue = wilcox.wilcoxonSignedRankTest(lastAlgoValues, values, false);

                String significance;
                if (pValue < 0.05) {
                    significance = stats.getMean() < lastStats.getMean() ? "--" : "+";
                } else {
                    significance = "≈";
                }

                System.out.printf("& %.2f(%.2f){\\bf(%s)} (%.2f) ", stats.getMean(), stats.getStandardDeviation(), significance, pValue);
            }

            System.out.printf("& %.2f(%.2f) \\\\\n", lastStats.getMean(), lastStats.getStandardDeviation());
        }
    }
}
