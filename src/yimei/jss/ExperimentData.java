package yimei.jss;

import com.opencsv.CSVReader;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class ExperimentData {

    // 定义一个类来存储每行结果
    static class Result {
        String scenario;
        String algo;
        int run;
        int generation;
        double trainFitness;
        double testFitness;

        public Result(String scenario, String algo, int run, int generation, double trainFitness, double testFitness) {
            this.scenario = scenario;
            this.algo = algo;
            this.run = run;
            this.generation = generation;
            this.trainFitness = trainFitness;
            this.testFitness = testFitness;
        }
    }

    public static void main(String[] args) throws Exception {
        // 算法和场景列表
        String[] algos = {"MTGP-tugboat-dynamic1","MTGP-tugboat-dynamic2"};
        String[] algosName = {"MTGP1","MTGP3"};

        String[] scenarios = {
                "mean-flowtime-0.75-1.5",
                "mean-flowtime-0.85-1.5",
                "mean-flowtime-0.95-1.5",
                "mean-weighted-tardiness-0.75-1.5",
                "mean-weighted-tardiness-0.85-1.5",
                "mean-weighted-tardiness-0.95-1.5",
                "energyConsumption-0.75-1.5",
                "energyConsumption-0.85-1.5",
                "energyConsumption-0.95-1.5"
        };
        String[] scenariosName = {
                "<Fmean, 0.75>", "<Fmean, 0.85>", "<Fmean, 0.95>",
                "<WTmean, 0.75>", "<WTmean, 0.85>", "<WTmean, 0.95>",
                "<TEC, 0.75>", "<TEC, 0.85>", "<TEC, 0.95>"
        };

        String col = "test-fit"; // 可改为 "training-fit" 等
        List<Result> resultList = new ArrayList<>();

        for (int i = 0; i < scenarios.length; i++) {
            final String scenario = scenarios[i];
            final String scenarioName = scenariosName[i];

            // 确定 testfile
            String testfile;
            if (i == 1 || i == 4 || i == 7) {
                testfile = "missing-0.85-1.5.csv";
            } else if (i == 2 || i == 5 || i == 8) {
                testfile = "missing-0.95-1.5.csv";
            } else {
                testfile = "missing-0.75-1.5.csv";
            }

            for (int a = 0; a < algos.length; a++) {
                final String algo = algos[a];
                final String algoName = algosName[a];

                //E:\download\grid\MTGP-main\MTGP-tugboat-dynamic1\energyConsumption-0.75-1.5\test
                String filePath = "E:\\download\\grid\\MTGP-main\\" + algo +
                        "/" + scenario + "/test/" + testfile;

                // 读取 CSV
                try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
                    String[] headers = reader.readNext(); // 读取列名
                    String[] line;
                    while ((line = reader.readNext()) != null) {
                        int run = Integer.parseInt(getColumnValue(line, headers, "Run"));
                        int generation = Integer.parseInt(getColumnValue(line, headers, "Generation"));
                        double trainFitness = Double.parseDouble(getColumnValue(line, headers, "TrainFitness"));
                        double testFitness = Double.parseDouble(getColumnValue(line, headers, "TestFitness"));
                        resultList.add(new Result(scenario, algoName, run, generation, trainFitness, testFitness));
                    }
                } catch (Exception e) {
                    System.err.println("Cannot read file: " + filePath);
                    e.printStackTrace();
                }
            }
        }

        // 获取最后一代 generations
        int maxGeneration = resultList.stream().mapToInt(r -> r.generation).max().orElse(0);

        // 保存最后一代的 testFitness
        List<Result> finalBoxPlot = new ArrayList<>();
        for (String scenario : scenarios) {
            for (String algoName : algosName) {
                final String s = scenario;
                final String a = algoName;
                resultList.stream()
                        .filter(r -> r.scenario.equals(s) && r.algo.equals(a) && r.generation == maxGeneration)
                        .forEach(r -> finalBoxPlot.add(new Result(r.scenario, r.algo, r.run, r.generation, r.trainFitness,r.testFitness)));
            }
        }

        System.out.println("Total final records: " + finalBoxPlot.size());
        // 这里可以写入 CSV 或绘图


    }

    // 辅助方法获取列值
    private static String getColumnValue(String[] line, String[] headers, String colName) {
        for (int i = 0; i < headers.length; i++) {
            if (headers[i].equals(colName)) {
                return line[i];
            }
        }
        throw new RuntimeException("Column not found: " + colName);
    }
}
