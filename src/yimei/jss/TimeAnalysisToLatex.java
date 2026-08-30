//package yimei.jss;
//
//import java.io.BufferedReader;
//import java.io.FileReader;
//import java.io.IOException;
//
//public class TimeAnalysisToLatex {
//
//    public static void main(String[] args) {
//        // 算法列表
//        String[] algos = {
//                "MTGP-tugboat-dynamic1",
//                "MTGP-tugboat-dynamic2",
//                "MTGP-tugboat-dynamic3"
//        };
//
//        // 场景列表
//        String[] scenarios = {
//                "mean-flowtime-0.75-1.3-60",   //0
//                "mean-flowtime-0.85-1.3-60",   //1
//                "mean-flowtime-0.95-1.3-60",   //2
//                "energyConsumption-0.75-1.3-60", //3
//                "energyConsumption-0.85-1.3-60", //4
//                "energyConsumption-0.95-1.3-60", //5
//                "mean-tardiness-0.75-1.3-60",   //6
//                "mean-tardiness-0.85-1.3-60",   //7
//                "mean-tardiness-0.95-1.3-60",   //8
//                "makespan-0.75-1.3-60",         //9
//                "makespan-0.85-1.3-60",         //10
//                "makespan-0.95-1.3-60",         //11
//                "mean-flowtime-0.75-1.3-80",    //12
//                "mean-flowtime-0.85-1.3-80",    //13
//                "mean-flowtime-0.95-1.3-80",    //14
//                "energyConsumption-0.75-1.3-80", //15
//                "energyConsumption-0.85-1.3-80", //16
//                "energyConsumption-0.95-1.3-80", //17
//                "mean-tardiness-0.75-1.3-80",   //18
//                "mean-tardiness-0.85-1.3-80",   //19
//                "mean-tardiness-0.95-1.3-80",   //20
//                "makespan-0.75-1.3-80",         //21
//                "makespan-0.85-1.3-80",         //22
//                "makespan-0.95-1.3-80"          //23
//        };
//
//        // 基础路径
//        String basePath = "E:\\download\\grid\\MTGP-main\\";
//
//        // 存储结果：每个算法在每个场景下的均值 [算法][场景]
//        double[][] results = new double[algos.length][scenarios.length];
//
//        // 遍历每个算法和每个场景
//        for (int a = 0; a < algos.length; a++) {
//            for (int s = 0; s < scenarios.length; s++) {
//                results[a][s] = calculateScenarioMean(basePath, algos[a], scenarios[s]);
//                System.out.printf("算法: %s, 场景: %s, 均值: %.2f%n",
//                        algos[a], scenarios[s], results[a][s]);
//            }
//        }
//
//        // 生成转置后的LaTeX表格（行名为场景，列名为算法）
//        generateTransposedLatexTable(results, algos, scenarios);
//    }
//
//    /**
//     * 计算一个场景下所有job的均值
//     * @param basePath 基础路径
//     * @param algo 算法名称
//     * @param scenario 场景名称
//     * @return 该场景下30个job的均值
//     */
//    private static double calculateScenarioMean(String basePath, String algo, String scenario) {
//        double sumOfJobMeans = 0.0;
//        int validJobCount = 0;
//
//        // 遍历30个job
//        for (int jobId = 0; jobId < 30; jobId++) {
//            // 构建文件路径
//            String filePath = basePath + algo + "\\" + scenario + "\\job." + jobId + ".time.csv";
//
//            // 计算当前job的总运行时间
//            double jobTotalTime = calculateJobTotalTime(filePath);
//
//            if (jobTotalTime >= 0) { // 文件读取成功
//                sumOfJobMeans += jobTotalTime;
//                validJobCount++;
//            } else {
//                System.err.println("警告: 无法读取文件 " + filePath);
//            }
//        }
//
//        // 计算均值
//        if (validJobCount > 0) {
//            return sumOfJobMeans / validJobCount;
//        } else {
//            return -1.0; // 表示无有效数据
//        }
//    }
//
//    /**
//     * 计算单个job的100个generation的运行时间总和
//     * @param filePath CSV文件路径
//     * @return 100个generation的运行时间总和
//     */
//    private static double calculateJobTotalTime(String filePath) {
//        double totalTime = 0.0;
//
//        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
//            String line;
//            boolean firstLine = true;
//
//            while ((line = br.readLine()) != null) {
//                // 跳过第一行（表头）
//                if (firstLine) {
//                    firstLine = false;
//                    continue;
//                }
//
//                // 解析CSV行，假设格式: generation,time
//                String[] parts = line.split(",");
//                if (parts.length >= 2) {
//                    try {
//                        double time = Double.parseDouble(parts[1].trim());
//                        totalTime += time;
//                    } catch (NumberFormatException e) {
//                        System.err.println("解析时间失败: " + line);
//                    }
//                }
//            }
//        } catch (IOException e) {
//            // 文件不存在或无法读取
//            return -1.0;
//        }
//
//        return totalTime;
//    }
//
//    /**
//     * 生成转置后的LaTeX表格（行名为场景，列名为算法）
//     * @param results 结果数据 [算法][场景]
//     * @param algos 算法名称数组
//     * @param scenarios 场景名称数组
//     */
//    private static void generateTransposedLatexTable(double[][] results, String[] algos, String[] scenarios) {
//        System.out.println("\n========== LaTeX表格代码（转置后） ==========\n");
//
//        // 表格开始
//        System.out.println("\\begin{table}[htbp]");
//        System.out.println("\\centering");
//        System.out.println("\\caption{不同场景下各算法的平均运行时间}");
//
//        // 构建列格式：第一列是场景，后面3列是算法
//        StringBuilder columnFormat = new StringBuilder("|l|");
//        for (int i = 0; i < algos.length; i++) {
//            columnFormat.append("c|");
//        }
//
//        System.out.println("\\begin{tabular}{" + columnFormat.toString() + "}");
//        System.out.println("\\hline");
//
//        // 打印列名（第一列是"场景"，后面是算法名称）
//        System.out.print("场景");
//        for (String algo : algos) {
//            // 简化算法名称
//            String algoShortName = algo.replace("MTGP-tugboat-", "");
//            System.out.print(" & " + algoShortName);
//        }
//        System.out.println(" \\\\");
//        System.out.println("\\hline");
//
//        // 打印每个场景的数据（转置后的行）
//        for (int s = 0; s < scenarios.length; s++) {
//            // 简化场景名称用于显示
////            String shortScenarioName = simplifyScenarioName(scenarios[s]);
//            System.out.print(scenarios[s]);
//
//            // 打印该场景下每个算法的结果
//            for (int a = 0; a < algos.length; a++) {
//                if (results[a][s] >= 0) {
//                    System.out.printf(" & %.2f", results[a][s]);
//                } else {
//                    System.out.print(" & -");
//                }
//            }
//            System.out.println(" \\\\");
////            System.out.println("\\hline");
//        }
//
//        // 表格结束
//        System.out.println("\\end{tabular}");
//        System.out.println("\\label{tab:transposed_results}");
//        System.out.println("\\end{table}");
//
//        System.out.println("\n==========================================\n");
//    }
//
//    /**
//     * 简化场景名称用于显示
//     * @param scenario 原始场景名称
//     * @return 简化后的名称
//     */
//    private static String simplifyScenarioName(String scenario) {
//        String[] parts = scenario.split("-");
//        if (parts.length >= 4) {
//            String objective = parts[0];
//            String weight = parts[1];
//            String deadline = parts[3];
//
//            // 简化目标函数名称
//            String shortObjective = "";
//            switch (objective) {
//                case "mean-flowtime":
//                    shortObjective = "Flow";
//                    break;
//                case "energyConsumption":
//                    shortObjective = "Energy";
//                    break;
//                case "mean-tardiness":
//                    shortObjective = "Tard";
//                    break;
//                case "makespan":
//                    shortObjective = "Make";
//                    break;
//                default:
//                    shortObjective = objective;
//            }
//
//            return shortObjective + "-" + weight + "-" + deadline;
//        }
//        return scenario;
//    }
//}

package yimei.jss;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class TimeAnalysisToLatex {

    public static void main(String[] args) {
        // 算法列表
        String[] algos = {
                "MTGP-tugboat-dynamic1",
                "MTGP-tugboat-dynamic2",
                "MTGP-tugboat-dynamic3"
        };

        // 算法简称
        String[] algoShortNames = {
                "MTGP-SPS",
                "MTGP-CSPS",
                "MTGP-PGS"
        };

        // 场景列表
        String[] scenarios = {
                "mean-flowtime-0.75-1.3-60",   //0
                "mean-flowtime-0.85-1.3-60",   //1
                "mean-flowtime-0.95-1.3-60",   //2
                "energyConsumption-0.75-1.3-60", //3
                "energyConsumption-0.85-1.3-60", //4
                "energyConsumption-0.95-1.3-60", //5
                "mean-tardiness-0.75-1.3-60",   //6
                "mean-tardiness-0.85-1.3-60",   //7
                "mean-tardiness-0.95-1.3-60",   //8
                "makespan-0.75-1.3-60",         //9
                "makespan-0.85-1.3-60",         //10
                "makespan-0.95-1.3-60",         //11
                "mean-flowtime-0.75-1.3-80",    //12
                "mean-flowtime-0.85-1.3-80",    //13
                "mean-flowtime-0.95-1.3-80",    //14
                "energyConsumption-0.75-1.3-80", //15
                "energyConsumption-0.85-1.3-80", //16
                "energyConsumption-0.95-1.3-80", //17
                "mean-tardiness-0.75-1.3-80",   //18
                "mean-tardiness-0.85-1.3-80",   //19
                "mean-tardiness-0.95-1.3-80",   //20
                "makespan-0.75-1.3-80",         //21
                "makespan-0.85-1.3-80",         //22
                "makespan-0.95-1.3-80"          //23
        };

        // 基础路径
        String basePath = "E:\\download\\grid\\MTGP-main\\";

        // 存储结果：每个算法在每个场景下的均值 [算法][场景]
        double[][] results = new double[algos.length][scenarios.length];

        // 遍历每个算法和每个场景
        for (int a = 0; a < algos.length; a++) {
            for (int s = 0; s < scenarios.length; s++) {
                results[a][s] = calculateScenarioMean(basePath, algos[a], scenarios[s]);
                System.out.printf("算法: %s, 场景: %s, 均值: %.2f%n",
                        algoShortNames[a], simplifyScenarioName(scenarios[s]), results[a][s]);
            }
        }

        // 生成转置后的LaTeX表格（行名为场景，列名为算法）
        generateTransposedLatexTable(results, algoShortNames, scenarios);
    }

    /**
     * 计算一个场景下所有job的均值
     * @param basePath 基础路径
     * @param algo 算法名称
     * @param scenario 场景名称
     * @return 该场景下30个job的均值
     */
    private static double calculateScenarioMean(String basePath, String algo, String scenario) {
        double sumOfJobMeans = 0.0;
        int validJobCount = 0;

        // 遍历30个job
        for (int jobId = 0; jobId < 30; jobId++) {
            // 构建文件路径
            String filePath = basePath + algo + "\\" + scenario + "\\job." + jobId + ".time.csv";

            // 计算当前job的总运行时间
            double jobTotalTime = calculateJobTotalTime(filePath);

            if (jobTotalTime >= 0) { // 文件读取成功
                sumOfJobMeans += jobTotalTime;
                validJobCount++;
            } else {
                System.err.println("警告: 无法读取文件 " + filePath);
            }
        }

        // 计算均值
        if (validJobCount > 0) {
            return sumOfJobMeans / validJobCount;
        } else {
            return -1.0; // 表示无有效数据
        }
    }

    /**
     * 计算单个job的100个generation的运行时间总和
     * @param filePath CSV文件路径
     * @return 100个generation的运行时间总和
     */
    private static double calculateJobTotalTime(String filePath) {
        double totalTime = 0.0;

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean firstLine = true;

            while ((line = br.readLine()) != null) {
                // 跳过第一行（表头）
                if (firstLine) {
                    firstLine = false;
                    continue;
                }

                // 解析CSV行，假设格式: generation,time
                String[] parts = line.split(",");
                if (parts.length >= 2) {
                    try {
                        double time = Double.parseDouble(parts[1].trim());
                        totalTime += time;
                    } catch (NumberFormatException e) {
                        System.err.println("解析时间失败: " + line);
                    }
                }
            }
        } catch (IOException e) {
            // 文件不存在或无法读取
            return -1.0;
        }

        return totalTime;
    }

    /**
     * 生成转置后的LaTeX表格（行名为场景，列名为算法）
     * @param results 结果数据 [算法][场景]
     * @param algoShortNames 算法简称数组
     * @param scenarios 场景名称数组
     */
    private static void generateTransposedLatexTable(double[][] results, String[] algoShortNames, String[] scenarios) {
        System.out.println("\n========== LaTeX表格代码（转置后） ==========\n");

        // 表格开始
        System.out.println("\\begin{table}[htbp]");
        System.out.println("\\centering");
        System.out.println("\\caption{不同场景下各算法的平均运行时间（单位：秒）}");
        System.out.println("\\small"); // 使用小字体

        // 构建列格式：第一列是场景，后面3列是算法，最后一列是均值
        StringBuilder columnFormat = new StringBuilder("|l|");
        for (int i = 0; i < algoShortNames.length; i++) {
            columnFormat.append("c|");
        }
        columnFormat.append("c|"); // 均值列

        System.out.println("\\begin{tabular}{" + columnFormat.toString() + "}");
        System.out.println("\\hline");

        // 打印列名（第一列是"场景"，后面是算法名称，最后一列是"均值"）
        System.out.print("场景");
        for (String algoName : algoShortNames) {
            System.out.print(" & " + algoName);
        }
        System.out.println(" & 均值 \\\\");
        System.out.println("\\hline");

        // 存储每列的均值
        double[] columnMeans = new double[algoShortNames.length];
        for (int a = 0; a < algoShortNames.length; a++) {
            columnMeans[a] = 0.0;
        }

        // 打印每个场景的数据（转置后的行）
        for (int s = 0; s < scenarios.length; s++) {
            // 简化场景名称
            String shortScenarioName = simplifyScenarioName(scenarios[s]);
            System.out.print(shortScenarioName);

            // 打印该场景下每个算法的结果
            for (int a = 0; a < algoShortNames.length; a++) {
                if (results[a][s] >= 0) {
                    System.out.printf(" & %.2f", results[a][s]);
                    columnMeans[a] += results[a][s];
                } else {
                    System.out.print(" & -");
                }
            }

            // 计算当前行的均值
            double rowSum = 0.0;
            int validCount = 0;
            for (int a = 0; a < algoShortNames.length; a++) {
                if (results[a][s] >= 0) {
                    rowSum += results[a][s];
                    validCount++;
                }
            }
            double rowMean = validCount > 0 ? rowSum / validCount : 0;
            System.out.printf(" & %.2f", rowMean);

            System.out.println(" \\\\");
        }

        // 打印均值行
        System.out.println("\\hline");
        System.out.print("\\textbf{均值}");
        for (int a = 0; a < algoShortNames.length; a++) {
            double mean = columnMeans[a] / scenarios.length;
            System.out.printf(" & \\textbf{%.2f}", mean);
        }

        // 计算所有数据的总体均值
        double totalSum = 0.0;
        int totalCount = 0;
        for (int a = 0; a < algoShortNames.length; a++) {
            totalSum += columnMeans[a];
            totalCount += scenarios.length;
        }
        double overallMean = totalSum / totalCount;
        System.out.printf(" & \\textbf{%.2f}", overallMean);
        System.out.println(" \\\\");

        System.out.println("\\hline");

        // 表格结束
        System.out.println("\\end{tabular}");
        System.out.println("\\label{tab:time_analysis_results}");
        System.out.println("\\end{table}");

        System.out.println("\n==========================================\n");

        // 同时输出一个更紧凑的表格（带星号标记）
        printCompactTable(results, algoShortNames, scenarios);
    }

    /**
     * 打印紧凑格式的表格（用于比较不同场景）
     */
    private static void printCompactTable(double[][] results, String[] algoShortNames, String[] scenarios) {
        System.out.println("\n========== 紧凑格式表格 ==========\n");

        System.out.println("\\begin{table}[htbp]");
        System.out.println("\\centering");
        System.out.println("\\caption{不同场景下各算法的平均运行时间对比}");
        System.out.println("\\tiny");

        // 按目标函数分组显示
        String[] objectives = {"mean-flowtime", "energyConsumption", "mean-tardiness", "makespan"};
        String[] objectiveShort = {"Fmean", "TEC", "Tmean", "Cmax"};
        int[] sizes = {60, 80};

        for (int objIdx = 0; objIdx < objectives.length; objIdx++) {
            String objective = objectives[objIdx];
            String objShort = objectiveShort[objIdx];

            System.out.println("\n\\textbf{目标: " + objShort + "}\\\\");
            System.out.println("\\begin{tabular}{|l|c|c|c|c|}");
            System.out.println("\\hline");
            System.out.print("参数");
            for (String algoName : algoShortNames) {
                System.out.print(" & " + algoName);
            }
            System.out.println(" \\\\");
            System.out.println("\\hline");

            for (int size : sizes) {
                for (double weight : new double[]{0.75, 0.85, 0.95}) {
                    String scenarioKey = objective + "-" + weight + "-1.3-" + size;
                    // 找到对应的场景索引
                    int scenarioIdx = -1;
                    for (int s = 0; s < scenarios.length; s++) {
                        if (scenarios[s].equals(scenarioKey)) {
                            scenarioIdx = s;
                            break;
                        }
                    }

                    if (scenarioIdx >= 0) {
                        String paramLabel = String.format("%.0f%%", weight * 100) + ", " + size;
                        System.out.print(paramLabel);
                        for (int a = 0; a < algoShortNames.length; a++) {
                            if (results[a][scenarioIdx] >= 0) {
                                System.out.printf(" & %.2f", results[a][scenarioIdx]);
                            } else {
                                System.out.print(" & -");
                            }
                        }
                        System.out.println(" \\\\");
                    }
                }
            }
            System.out.println("\\hline");
            System.out.println("\\end{tabular}");
            System.out.println();
        }

        System.out.println("\\end{table}");
        System.out.println("\n==========================================\n");
    }

    /**
     * 简化场景名称用于显示
     * @param scenario 原始场景名称
     * @return 简化后的名称
     */
    private static String simplifyScenarioName(String scenario) {
        String[] parts = scenario.split("-");

        // 根据场景类型解析不同的部分
        String objective = parts[0];
        String weight;
        String size;

        // 判断目标函数类型
        if (objective.equals("mean-flowtime") || objective.equals("mean-tardiness")) {
            // 格式: mean-flowtime-0.75-1.3-60
            // 索引: 0: mean-flowtime, 1: 0.75, 2: 1.3, 3: 60
            weight = parts[1];
            size = parts[3];
        } else if (objective.equals("energyConsumption") || objective.equals("makespan")) {
            // 格式: energyConsumption-0.75-1.3-60
            // 索引: 0: energyConsumption, 1: 0.75, 2: 1.3, 3: 60
            weight = parts[1];
            size = parts[3];
        } else {
            // 默认处理
            weight = "0.75";
            size = "60";
        }

        // 简化目标函数名称
        String shortObjective = "";
        switch (objective) {
            case "mean-flowtime":
                shortObjective = "Fmean";
                break;
            case "energyConsumption":
                shortObjective = "TEC";
                break;
            case "mean-tardiness":
                shortObjective = "Tmean";
                break;
            case "makespan":
                shortObjective = "Cmax";
                break;
            default:
                shortObjective = objective;
        }

        // 格式化权重百分比
        double weightValue = Double.parseDouble(weight);
        String weightPercent = String.format("%.0f%%", weightValue * 100);

        return shortObjective + "-" + weight + "-" + size;
    }
}