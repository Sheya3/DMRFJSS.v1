package yimei.jss;

import com.opencsv.CSVReader;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;
import java.util.List;
import java.util.*;

public class ConvergenceCurvePlotter {

    // 定义数据类
    static class Result {
        String scenario;
        String algo;
        int run;
        int generation;
        double trainFitness;
        double testFitness;

        public Result(String scenario, String algo, int run, int generation,
                      double trainFitness, double testFitness) {
            this.scenario = scenario;
            this.algo = algo;
            this.run = run;
            this.generation = generation;
            this.trainFitness = trainFitness;
            this.testFitness = testFitness;
        }
    }

    // 存储每个算法在每个场景每个世代的平均值
    static class GenerationData {
        String scenario;
        String algo;
        int generation;
        double avgTestFitness;
        double avgTrainFitness;
        int count; // 用于计算平均值的计数

        public GenerationData(String scenario, String algo, int generation) {
            this.scenario = scenario;
            this.algo = algo;
            this.generation = generation;
            this.avgTestFitness = 0.0;
            this.avgTrainFitness = 0.0;
            this.count = 0;
        }

        public void addTestFitness(String scenario, double value) {
            if(scenario.contains("mean-flowtime")){
                if(value<=4) {  avgTestFitness = (avgTestFitness * count + value) / (count + 1);
                    count++;}
            }else if(scenario.contains("energyConsumption")){
            avgTestFitness = (avgTestFitness * count + value) / (count + 1);
            count++;
            }else if(scenario.contains("mean-tardiness")){
                if(value<=0.5) {  avgTestFitness = (avgTestFitness * count + value) / (count + 1);
                    count++;}
            }else if(scenario.contains("makespan")){
            avgTestFitness = (avgTestFitness * count + value) / (count + 1);
            count++;
            }
//            avgTestFitness = (avgTestFitness * count + value) / (count + 1);
//            count++;
        }

        public void addTrainFitness(double value) {
            avgTrainFitness = (avgTrainFitness * count + value) / (count + 1);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new ConvergenceCurvePlotter().createAndShowGUI();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    private JPanel createBottomLegendPanel(String[] algosName) {
        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        legendPanel.setBackground(Color.WHITE);

        final Color[] colors = {
                new Color(233, 47, 48),
                new Color(92, 135, 153),
                new Color(110, 99, 156),
        };

        for (int i = 0; i < algosName.length; i++) {
            JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
            item.setBackground(Color.WHITE);

            JLabel colorBox = new JLabel();
            colorBox.setOpaque(true);
            colorBox.setBackground(colors[i]);
            colorBox.setPreferredSize(new Dimension(12, 12));

            JLabel label = new JLabel(algosName[i]);
            label.setFont(new Font("Arial", Font.PLAIN, 11));

            item.add(colorBox);
            item.add(label);
            legendPanel.add(item);
        }

        return legendPanel;
    }
    private void createAndShowGUI() throws Exception {
        // 算法和场景列表
        String[] algos = {"MTGP-tugboat-dynamic1","MTGP-tugboat-dynamic2", "MTGP-tugboat-dynamic3"};
        String[] algosName = {"MTGP-SPS","MTGP-CSPS", "MTGP-PGS"};
        //MTGP-SPS, MTGP-CSPS and MTGP-PGS.

//        String[] algos = {"MTGP-tugboat-dynamic1"};
//        String[] algosName = {"MTGP1"};

//        String[] scenarios = {
//                "mean-flowtime-0.75-1.3",
//                "mean-flowtime-0.85-1.3",
//                "mean-flowtime-0.95-1.3",
//                "makespan-0.75-1.3",
//                "makespan-0.85-1.3",
//                "makespan-0.95-1.3",
//                "energyConsumption-0.75-1.3",
//                "energyConsumption-0.85-1.3",
//                "energyConsumption-0.95-1.3"
//        };
//
//        String[] scenariosName = {
//                "<Fmean, 0.75>", "<Fmean, 0.85>", "<Fmean, 0.95>",
//                "<Cmax, 0.75>", "<Cmax, 0.85>", "<Cmax, 0.95>",
//                "<TEC, 0.75>", "<TEC, 0.85>", "<TEC, 0.95>"
//        };

//        deviation
        String[] scenarios = {
//                "mean-flowtime-0.75-1.3-60", //0
                "mean-flowtime-0.85-1.3-60", //1
                "mean-flowtime-0.95-1.3-60", //2
                //                "mean-flowtime-0.75-1.3-80",//12
                "mean-flowtime-0.85-1.3-80",//13
                "mean-flowtime-0.95-1.3-80",//14
//                "energyConsumption-0.75-1.3-60",
                "energyConsumption-0.85-1.3-60",
                "energyConsumption-0.95-1.3-60",
                //                "energyConsumption-0.75-1.3-80",
                "energyConsumption-0.85-1.3-80",
                "energyConsumption-0.95-1.3-80",
//                "mean-tardiness-0.75-1.3-60", //6
                "mean-tardiness-0.85-1.3-60", //7
                "mean-tardiness-0.95-1.3-60", //8
//                "mean-tardiness-0.75-1.3-80",
                "mean-tardiness-0.85-1.3-80",
                "mean-tardiness-0.95-1.3-80",
//                "makespan-0.75-1.3-60",
                "makespan-0.85-1.3-60",
                "makespan-0.95-1.3-60",
//                "makespan-0.75-1.3-80",
                "makespan-0.85-1.3-80",
                "makespan-0.95-1.3-80"
        };
//
        String[] scenariosName = {
//                "<Fmean, 0.75, 60>",
                "<Fmean, 0.85, 60>",
                "<Fmean, 0.95, 60>",
//                "<Fmean, 0.75, 80>",
                "<Fmean, 0.85, 80>",
                "<Fmean, 0.95, 80>",
//                "<TEC, 0.75, 60>",
                "<TEC, 0.85, 60>",
                "<TEC, 0.95, 60>",
//                "<TEC, 0.75, 80>",
                "<TEC, 0.85, 80>",
                "<TEC, 0.95, 80>",
//                "<Tmean, 0.75, 60>",
                "<Tmean, 0.85, 60>",
                "<Tmean, 0.95, 60>",
//                "<Tmean, 0.75, 80>",
                "<Tmean, 0.85, 80>",
                "<Tmean, 0.95, 80>",
//                "<Cmax, 0.75, 60>",
                "<Cmax, 0.85, 60>",
                "<Cmax, 0.95, 60>",
//                "<Cmax, 0.75, 80>",
                "<Cmax, 0.85, 80>",
                "<Cmax, 0.95, 80>"
        };

        // 读取所有数据
        List<Result> resultList = readAllData(algos, algosName, scenarios, scenariosName);

        // 计算每个算法在每个场景每个世代的平均值
        Map<String, Map<String, Map<Integer, GenerationData>>> dataMap = calculateAverages(resultList);

        // 创建图表面板
        JPanel mainPanel = createChartsPanel(dataMap, scenarios, scenariosName,algosName);

        // 创建主窗口
        JFrame frame = new JFrame("Convergence Curves for All Scenarios");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // 添加标题
        JLabel titleLabel = new JLabel("Convergence Curves Comparison", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        frame.add(titleLabel, BorderLayout.NORTH);



        // 添加图表面板
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        frame.add(scrollPane, BorderLayout.CENTER);

        JPanel container = new JPanel(new BorderLayout());

// 中间：图
        container.add(scrollPane, BorderLayout.CENTER);

// 底部：legend
        JPanel legendPanel = createBottomLegendPanel(algosName);
        container.add(legendPanel, BorderLayout.SOUTH);

// 加到 frame
        frame.add(container, BorderLayout.CENTER);
        frame.setSize(1600, 1200);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        // 在 frame.setVisible(true); 之前或之后都可以
        savePanelAsPNG(container, "E:\\download\\grid\\MTGP-main\\all_scenarios.png");
    }
    private void savePanelAsPNG(JPanel panel, String path) {
        try {
            int width = panel.getWidth();
            int height = panel.getHeight();

            // 如果还没渲染，手动设定尺寸
            if (width == 0 || height == 0) {
                width = 1600;
                height = 1200;
                panel.setSize(width, height);
                panel.doLayout();
            }

            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = image.createGraphics();

            panel.paint(g2);
            g2.dispose();

            javax.imageio.ImageIO.write(image, "png", new File(path));

            System.out.println("Saved PNG: " + path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private List<Result> readAllData(String[] algos, String[] algosName,
                                     String[] scenarios, String[] scenariosName) throws Exception {
        List<Result> resultList = new ArrayList<>();

        for (int i = 0; i < scenarios.length; i++) {
            final String scenario = scenarios[i];
            final String scenarioName = scenariosName[i];

            // 确定testfile
//            String testfile;
//            if (i == 1 || i == 4 || i == 7) {
//                testfile = "missing-0.85-1.3.csv";
//            } else if (i == 2 || i == 5 || i == 8) {
//                testfile = "missing-0.95-1.3.csv";
//            } else {
//                testfile = "missing-0.75-1.3.csv";
//            }

            String testfile;

            if (scenario.contains("0.75") && scenario.contains("-60")) {
                testfile = "missing-0.75-1.3-60.csv";
            } else if (scenario.contains("0.85") && scenario.contains("-60")) {
                testfile = "missing-0.85-1.3-60.csv";
            } else if (scenario.contains("0.95") && scenario.contains("-60")) {
                testfile = "missing-0.95-1.3-60.csv";
            } else if (scenario.contains("0.75") && scenario.contains("-80")) {
                testfile = "missing-0.75-1.3-80.csv";
            } else if (scenario.contains("0.85") && scenario.contains("-80")) {
                testfile = "missing-0.85-1.3-80.csv";
            } else if (scenario.contains("0.95") && scenario.contains("-80")) {
                testfile = "missing-0.95-1.3-80.csv";
            } else {
                testfile = "error";
                System.out.println("error: " + scenario);
            }
//            String testfile;
//            if(i%3==0&&i<12){
//                double sign1 = 0.75; int sign2 = 60;
//                testfile = "missing-"+sign1+"-1.3-"+sign2+".csv";
//            } else if (i%3==1&&i<12) {
//                double sign1 = 0.85; int sign2 = 60;
//                testfile = "missing-"+sign1+"-1.3-"+sign2+".csv";
//            } else if (i%3==2&&i<12) {
//                double sign1 = 0.95; int sign2 = 60;
//                testfile = "missing-"+sign1+"-1.3-"+sign2+".csv";
//            } else if (i%3==0&&i>=12) {
//                double sign1 = 0.75; int sign2 = 80;
//                testfile = "missing-"+sign1+"-1.3-"+sign2+".csv";
//            } else if (i%3==1&&i>=12) {
//                double sign1 = 0.85; int sign2 = 80;
//                testfile = "missing-"+sign1+"-1.3-"+sign2+".csv";
//            }else if (i%3==2&&i>=12) {
//                double sign1 = 0.95; int sign2 = 80;
//                testfile = "missing-"+sign1+"-1.3-"+sign2+".csv";
//            }else {
//                testfile ="error";
//                System.out.println("error");
//
//            }

            for (int a = 0; a < algos.length; a++) {
                final String algo = algos[a];
                final String algoName = algosName[a];

//                E:\download\grid\MTGP-main\MTGP-tugboat-dynamic1\energyConsumption-0.75-1.5-60\test
                String filePath = "E:\\download\\grid\\MTGP-main\\" + algo +
                        "/" + scenario + "/test/" + testfile;

//                String filePath = "E:\\download\\grid\\TabuGP25\\" + algo +
//                        "/" + scenario + "/test/" + testfile;

                System.out.println("Reading: " + filePath);

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
                int aa=0;
            }
        }

        System.out.println("Total records read: " + resultList.size());
        return resultList;
    }

    double max_MF = 0;
    double max_MT = 0;
    double max_TEC = 0;
    private void clearAllData (List<Result> resultList) {
        int num=0;
     /*   for (int i = 0; i < 9; i++) {
            for (int a = 0; a < 2; a++) { //change
                String object;
                if (i == 0 || i == 1 || i == 2) {
                    object = "MF";
                } else if (i == 3 || i == 4 || i == 5) {
                    object = "MS";
                } else {
                    object = "TEC";
                }
                for (int j = 0; j < 99 * 30; j++) {
                    if (object.equals("MF")){
                        if (resultList.get(num).testFitness<999999999&&max_MF<resultList.get(num).testFitness) {
                            max_MF = resultList.get(num).testFitness;
                        }
                    } else if (object.equals("MS")) {
                        if (resultList.get(num).testFitness<999999999&&max_MT<resultList.get(num).testFitness) {
                            max_MT = resultList.get(num).testFitness;
                        }
                    }else if (object.equals("TEC")){
                        if (resultList.get(num).testFitness<999999999&&max_TEC<resultList.get(num).testFitness) {
                            max_TEC = resultList.get(num).testFitness;
                        }
                    }
                    num++;
                }

            }
        }*/
  /*      for (int i = 0; i < 24; i++) {
            for (int a = 0; a < 3; a++) { //change
                String object;
                if (i == 0 || i == 1 || i == 2||i == 12 || i == 13 || i == 14) {
                    object = "MF";
                } else if (i == 3 || i == 4 || i == 5||i == 15 || i == 16 || i == 17) {
                    object = "TEC";
                } else if (i == 6 || i == 7 || i == 8||i == 18 || i == 19 || i == 20) {
                    object = "MT";
                } else if (i == 9 || i == 10 || i == 11||i == 21 || i == 22 || i == 23) {
                    object = "MS";
                } else {
                    object = "error";
                }
                for (int j = 0; j < 99 * 30; j++) {
                    if (object.equals("MF")){
                        if (resultList.get(num).testFitness<4&&max_MF<resultList.get(num).testFitness) {
                            max_MF = resultList.get(num).testFitness;
                        }
                    } else if (object.equals("MS")) {
                        if (resultList.get(num).testFitness<999999999&&max_MT<resultList.get(num).testFitness) {
                            max_MT = resultList.get(num).testFitness;
                        }
                    }else if (object.equals("TEC")){
                        if (resultList.get(num).testFitness<999999999&&max_TEC<resultList.get(num).testFitness) {
                            max_TEC = resultList.get(num).testFitness;
                        }
                    }
                    num++;
                }
//
            }
        }*/
        num=0;
//        for (int i = 0; i < 24; i++) {
//            for (int a = 0; a < 3; a++) { //change
//                String object;
//                if (i == 0 || i == 1 || i == 2||i == 12 || i == 13 || i == 14) {
//                    object = "MF";
//                } else if (i == 3 || i == 4 || i == 5||i == 15 || i == 16 || i == 17) {
//                    object = "TEC";
//                } else if (i == 6 || i == 7 || i == 8||i == 18 || i == 19 || i == 20) {
//                    object = "MT";
//                } else if (i == 9 || i == 10 || i == 11||i == 21 || i == 22 || i == 23) {
//                    object = "MS";
//                } else {
//                    object = "error";
//                }
//                for (int j = 0; j < 99 * 29; j++) {
//                    if (object.equals("MF")){
//                        if (resultList.get(num).testFitness>5||Double.isNaN(resultList.get(num).testFitness)) {
////                            resultList.remove(num);j++;
//                            resultList.get(num).testFitness=2;
//                        }
//                    } else if (object.equals("MS")) {
//                        if (resultList.get(num).testFitness>999999999||Double.isNaN(resultList.get(num).testFitness)) {
//                            resultList.get(num).testFitness=max_MT;
//                        }
//                    }else if (object.equals("TEC")){
//                        if (resultList.get(num).testFitness>999999999||Double.isNaN(resultList.get(num).testFitness)) {
//                            resultList.get(num).testFitness=max_TEC;
//                        }
//                    }else if (object.equals("MT")){
//                        if (resultList.get(num).testFitness>999999999||Double.isNaN(resultList.get(num).testFitness)) {
//                            resultList.get(num).testFitness=0.5;
//                        }
//                    }
//                    num++;
//                }
//            }
//        }

       /* for (int i = 0; i < 24; i++) {
            for (int a = 0; a < 2; a++) { //change
                String object;
                if (i == 0 || i == 1 || i == 2||i == 12 || i == 13 || i == 14) {
                    object = "MF";
                } else if (i == 3 || i == 4 || i == 5||i == 15 || i == 16 || i == 17) {
                    object = "TEC";
                } else if (i == 6 || i == 7 || i == 8||i == 18 || i == 19 || i == 20) {
                    object = "MT";
                } else if (i == 9 || i == 10 || i == 11||i == 21 || i == 22 || i == 23) {
                    object = "MS";
                } else {
                    object = "error";
                }
                for (int j = 0; j < 99 * 29; j++) {
                    if (object.equals("MF")){
                        if (resultList.get(num).testFitness>5||Double.isNaN(resultList.get(num).testFitness)) {
//                            resultList.remove(num);j++;
                            resultList.get(num).testFitness=2;
                        }
                    } else if (object.equals("MS")) {
                        if (resultList.get(num).testFitness>999999999||Double.isNaN(resultList.get(num).testFitness)) {
                            resultList.get(num).testFitness=max_MT;
                        }
                    }else if (object.equals("TEC")){
                        if (resultList.get(num).testFitness>999999999||Double.isNaN(resultList.get(num).testFitness)) {
                            resultList.get(num).testFitness=max_TEC;
                        }
                    }else if (object.equals("MT")){
                        if (resultList.get(num).testFitness>999999999||Double.isNaN(resultList.get(num).testFitness)) {
                            resultList.get(num).testFitness=0.5;
                        }
                    }
                    num++;
                }
            }
        }*/

        for (int i = 0; i < 24; i++) {
            for (int a = 0; a < 3; a++) { //change
                String object;
                if (i == 0 || i == 1 || i == 2||i == 12 || i == 13 || i == 14) {
                    object = "MF";
                } else if (i == 3 || i == 4 || i == 5||i == 15 || i == 16 || i == 17) {
                    object = "TEC";
                } else if (i == 6 || i == 7 || i == 8||i == 18 || i == 19 || i == 20) {
                    object = "MT";
                } else if (i == 9 || i == 10 || i == 11||i == 21 || i == 22 || i == 23) {
                    object = "MS";
                } else {
                    object = "error";
                }
                for (int j = 0; j < 99 * 29; j++) {
                    if (object.equals("MF")){
                        if (resultList.get(num).testFitness>5||Double.isNaN(resultList.get(num).testFitness)) {
//                            resultList.remove(num);j++;
                            resultList.get(num).testFitness=2;
                        }
                    } else if (object.equals("MS")) {
//                        if (resultList.get(num).testFitness>999999999||Double.isNaN(resultList.get(num).testFitness)) {
//                            resultList.get(num).testFitness=max_MT;
//                        }
                    }else if (object.equals("TEC")){
//                        if (resultList.get(num).testFitness>999999999||Double.isNaN(resultList.get(num).testFitness)) {
//                            resultList.get(num).testFitness=max_TEC;
//                        }
                    }else if (object.equals("MT")){
                        if (resultList.get(num).testFitness>999999999||Double.isNaN(resultList.get(num).testFitness)) {
                            resultList.get(num).testFitness=0.5;
                        }
                    }
                    num++;
                }
            }
        }
    }

    private Map<String, Map<String, Map<Integer, GenerationData>>> calculateAverages(List<Result> resultList) {
        // 数据结构: 场景 -> 算法 -> 世代 -> GenerationData
        Map<String, Map<String, Map<Integer, GenerationData>>> dataMap = new HashMap<>();

        for (Result result : resultList) {
            // 获取或创建场景的Map
            Map<String, Map<Integer, GenerationData>> algoMap = dataMap.computeIfAbsent(
                    result.scenario, k -> new HashMap<>());

            // 获取或创建算法的Map
            Map<Integer, GenerationData> generationMap = algoMap.computeIfAbsent(
                    result.algo, k -> new HashMap<>());

            // 获取或创建世代的数据
            GenerationData genData = generationMap.computeIfAbsent(
                    result.generation, k -> new GenerationData(result.scenario, result.algo, result.generation));

            // 添加测试适应度
            genData.addTestFitness(result.scenario,result.testFitness);
            genData.addTrainFitness(result.trainFitness);

        }

        return dataMap;
    }

    private JPanel createChartsPanel(Map<String, Map<String, Map<Integer, GenerationData>>> dataMap,
                                     String[] scenariosName, String[] scenariosName0, String[] algosName) {
        // 创建3x3网格的面板
        JPanel gridPanel = new JPanel(new GridLayout(4, 3, 0, 0));
        gridPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        // 为每个场景创建一个图表
        int i=0;
        for (String scenarioName : scenariosName) {
            // 找到对应的原始场景名称
            String originalScenario = null;
            for (Map.Entry<String, Map<String, Map<Integer, GenerationData>>> entry : dataMap.entrySet()) {
                if (entry.getKey().contains(scenarioName.replaceAll("[<>,\\s]", ""))) {
                    originalScenario = entry.getKey();
                    break;
                }
            }

            if (originalScenario == null) {
                System.err.println("Warning: No data found for scenario: " + scenarioName);
                continue;
            }
            String ScenarioName0=scenariosName0[i];i++;
            // 创建这个场景的图表
            ChartPanel chartPanel = createChartForScenario(dataMap.get(originalScenario), scenarioName, ScenarioName0,algosName);
            gridPanel.add(chartPanel);
        }

        return gridPanel;
    }

    private ChartPanel createChartForScenario(Map<String, Map<Integer, GenerationData>> algoData,
                                              String scenarioName, String scenarioName0,String[] algosName) {

        // 创建数据集
        XYSeriesCollection dataset = new XYSeriesCollection();

        // 定义颜色和线条样式
        Color[] colors = {
                new Color(233, 47, 48),      // 红色 - MTGP1
                new Color(92, 135, 153),      // 红色 - MTGP2
                new Color(110, 99, 156),      // 蓝色 - MTGP3
//                new Color(233, 47, 48),      // 红色 - MTGP1
//                new Color(92, 135, 153),      // 红色 - MTGP2
//                new Color(110, 99, 156),      // 蓝色 - MTGP3
        };

        BasicStroke[] strokes = {
                new BasicStroke(2.0f),      // 实线
                new BasicStroke(2.0f),      // 实线
                new BasicStroke(2.0f),      // 实线
//                new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
//                        1.0f, new float[]{10.0f, 6.0f}, 0.0f),  // 虚线
//                new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
//                        1.0f, new float[]{10.0f, 6.0f}, 0.0f),  // 虚线
//                new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
//                        1.0f, new float[]{10.0f, 6.0f}, 0.0f),  // 虚线
        };

        // 为每个算法添加数据系列
        for (int i = 0; i < algosName.length; i++) {
            String algoName = algosName[i];
            XYSeries series = new XYSeries(algoName);

            Map<Integer, GenerationData> generationData = algoData.get(algoName);
            if (generationData != null) {
                // 按世代排序
                List<Integer> generations = new ArrayList<>(generationData.keySet());
                Collections.sort(generations);

                for (Integer generation : generations) {
                    GenerationData data = generationData.get(generation);
                    series.add((double) generation, data.avgTestFitness);
//                    series.add(generation, data.avgTestFitness);
                }
            }

            dataset.addSeries(series);
        }

//        for (int i = 0; i < algosName.length; i++) {
//            String algoName = algosName[i];
//            XYSeries series = new XYSeries(algoName);
//
//            Map<Integer, GenerationData> generationData = algoData.get(algoName);
//            if (generationData != null) {
//                // 按世代排序
//                List<Integer> generations = new ArrayList<>(generationData.keySet());
//                Collections.sort(generations);
//
//                for (Integer generation : generations) {
//                    GenerationData data = generationData.get(generation);
//                    series.add((double) generation, data.avgTrainFitness);
////                    series.add(generation, data.avgTestFitness);
//                }
//            }
//
//            dataset.addSeries(series);
//        }

        // 先收集所有数据用于计算范围
        double minY = Double.MAX_VALUE;
        double maxY = Double.MIN_VALUE;

        for (int i = 0; i < algosName.length; i++) {
            String algoName = algosName[i];
            XYSeries series = new XYSeries(algoName);

            Map<Integer, GenerationData> generationData = algoData.get(algoName);
            if (generationData != null) {
                List<Integer> generations = new ArrayList<>(generationData.keySet());
                Collections.sort(generations);

                for (Integer generation : generations) {
                    GenerationData data = generationData.get(generation);
                    double value = data.avgTestFitness;
//                    double value1 = data.avgTrainFitness;
                    if (!Double.isNaN(value) && value < Double.MAX_VALUE) {
                        minY = Math.min(minY, value);
                        maxY = Math.max(maxY, value);
//                        minY = Math.min(minY, value1);
//                        maxY = Math.max(maxY, value1);
                        series.add((double)generation, value);
                    }
                }
            }
            dataset.addSeries(series);
        }

        // 创建图表
        JFreeChart chart = ChartFactory.createXYLineChart(
                scenarioName0,           // 图表标题
                "Generation",           // X轴标签
                "Average Test Fitness", // Y轴标签
                dataset,                // 数据集
                PlotOrientation.VERTICAL,
//                true,                   // 显示图例
                false,                   // 显示图例
                true,                   // 工具提示
                false                   // URL
        );

        // 移除图表边框
        chart.setBorderVisible(false);
        chart.setBackgroundPaint(Color.WHITE);

        // 自定义图表样式
        XYPlot plot = chart.getXYPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();

        // 根据场景类型设置不同的Y轴范围
        if (scenarioName.contains("Fmean")) {
            plot.getRangeAxis().setRange(0, 300); // Mean Flowtime
        } else if (scenarioName.contains("TEC")) {
            plot.getRangeAxis().setRange(0, 2500); // Energy Consumption
        } else if (scenarioName.contains("Cmax")) {
            plot.getRangeAxis().setRange(0, 800); // Makespan
        } else if (scenarioName.contains("Tmean")) {
            plot.getRangeAxis().setRange(0, 150); // Mean Tardiness
        } else {
            // 默认：根据数据动态设置，并添加10%的边距
            double padding = (maxY - minY) * 0.1;
            plot.getRangeAxis().setRange(minY - padding, maxY + padding);
        }

        // 设置每个系列的样式
        for (int i = 0; i < dataset.getSeriesCount(); i++) {
            renderer.setSeriesPaint(i, colors[i % colors.length]);
            renderer.setSeriesStroke(i, strokes[i % strokes.length]);
            renderer.setSeriesShapesVisible(i, false); // 显示数据点

            // 设置数据点形状
            if (i == 0) {
                // 圆形
                renderer.setSeriesShape(i, new java.awt.geom.Ellipse2D.Double(-3, -3, 6, 6));
            } else if (i == 1) {
                // 正方形
                renderer.setSeriesShape(i, new java.awt.geom.Rectangle2D.Double(-3, -3, 6, 6));
            }
        }

        plot.setRenderer(renderer);

        // 设置背景和网格线
        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinePaint(new Color(220, 220, 220));
        plot.setRangeGridlinePaint(new Color(220, 220, 220));
        plot.setDomainGridlinesVisible(true);
        plot.setRangeGridlinesVisible(true);

        // 设置标题字体
        chart.getTitle().setFont(new Font("Arial", Font.BOLD, 14));

        // 设置坐标轴标签字体
        plot.getDomainAxis().setLabelFont(new Font("Arial", Font.PLAIN, 11));
        plot.getRangeAxis().setLabelFont(new Font("Arial", Font.PLAIN, 11));

        // 设置图例字体
//        chart.getLegend().setItemFont(new Font("Arial", Font.PLAIN, 10));

        // 创建ChartPanel
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(250, 175));
        chartPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

        return chartPanel;
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

