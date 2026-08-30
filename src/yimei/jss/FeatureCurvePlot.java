package yimei.jss;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleEdge;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class FeatureCurvePlot {

    // CSV文件读取方法
    private static Map<String, List<Double>> readCSV(String filePath) throws IOException {
        Map<String, List<Double>> featureMap = new LinkedHashMap<>();
        try (BufferedReader br = Files.newBufferedReader(Paths.get(filePath))) {
            String line = br.readLine(); // 读取表头
            if (line == null) return featureMap;
            String[] features = line.split(",");

            // 初始化每个 feature 对应的 list
            for (String feature : features) {
                featureMap.put(feature, new ArrayList<>());
            }

            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                for (int i = 0; i < values.length; i++) {
                    featureMap.get(features[i]).add(Double.parseDouble(values[i]));
                }
            }
        }
        return featureMap;
    }
    /**
     * 简化场景名称
     * @param objective 目标函数名称
     * @param weight 权重值
     * @param size 规模值
     * @return 简化后的名称，如 "Fmean-0.75-60"
     */
    private static String simplifyScenarioName(String objective, String weight, String size) {
        // 简化目标函数名称
        String shortObjective = "";
        switch (objective) {
            case "mean-flowtime-":
                shortObjective = "Fmean";
                break;
            case "energyConsumption-":
                shortObjective = "TEC";
                break;
            case "mean-tardiness-":
                shortObjective = "Tmean";
                break;
            case "makespan-":
                shortObjective = "Cmax";
                break;
            default:
                shortObjective = objective;
        }

        return shortObjective + "-" + weight + "-" + size;
    }
    // 创建图表
    private static JFreeChart createChart(Map<String, List<Double>> featureMap, String chartTitle) {
        XYSeriesCollection dataset = new XYSeriesCollection();
        for (Map.Entry<String, List<Double>> entry : featureMap.entrySet()) {
            if (entry.getKey().equalsIgnoreCase("Gen")) {
                continue; // 忽略 generation 列
            }
            XYSeries series = new XYSeries(entry.getKey());
            List<Double> values = entry.getValue();
            for (int i = 0; i < values.size(); i++) {
                series.add(i + 1, values.get(i)); // generation 从1开始
            }
            dataset.addSeries(series);
        }

        JFreeChart chart = ChartFactory.createXYLineChart(
                chartTitle,
                "Generation",
                "Occurrence",
                dataset,
                PlotOrientation.VERTICAL,
                false, true, false
        );
        // 移除图表边框
        chart.setBorderVisible(false);
        chart.setBackgroundPaint(Color.WHITE);

        XYPlot plot = chart.getXYPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        // 给每条线不同颜色
        for (int i = 0; i < dataset.getSeriesCount(); i++) {
            renderer.setSeriesPaint(i, getColor(i));
            renderer.setSeriesStroke(i, new BasicStroke(2.0f));
            renderer.setSeriesShapesVisible(i, false);  // 不显示点
        }
        plot.setRenderer(renderer);
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(Color.GRAY);
        plot.setDomainGridlinePaint(Color.GRAY);

        return chart;
    }

    // 获取颜色
    private static Color getColor(int index) {
        Color[] colors = new Color[]{
                Color.RED, Color.BLUE, Color.GREEN, Color.MAGENTA, Color.ORANGE,
                Color.CYAN, Color.PINK, Color.YELLOW, Color.GRAY, Color.DARK_GRAY,
                new Color(128,0,128), new Color(0,128,128), new Color(128,128,0),
                new Color(255,105,180), new Color(75,0,130), new Color(210,105,30),
                new Color(34,139,34), new Color(0,191,255)
        };
        return colors[index % colors.length];
    }

    // 创建一个只包含图例的图表
    // 创建一个只包含图例的图表
    // 创建一个只包含图例的图表 - 通过裁剪方式隐藏红线
    private static BufferedImage createLegendImage(Map<String, List<Double>> featureMap, int width, int height) {
        XYSeriesCollection dataset = new XYSeriesCollection();

        for (Map.Entry<String, List<Double>> entry : featureMap.entrySet()) {
            if (entry.getKey().equalsIgnoreCase("Gen")) {
                continue;
            }
            XYSeries series = new XYSeries(entry.getKey());
            // 需要添加数据点，否则图例不会显示颜色
            series.add(0, 0);
            series.add(1, 0);
            dataset.addSeries(series);
        }

        JFreeChart chart = ChartFactory.createXYLineChart(
                "",  // 无标题
                "", "",  // 无轴标签
                dataset,
                PlotOrientation.VERTICAL,
                true, false, false  // 显示图例
        );

        // 设置图例样式
        LegendTitle legend = chart.getLegend();
        legend.setBorder(BlockBorder.NONE);
        legend.setBackgroundPaint(Color.WHITE);
        legend.setPosition(RectangleEdge.BOTTOM);
        legend.setItemFont(new Font("SansSerif", Font.PLAIN, 12));

        // 获取绘图区域并完全隐藏
        XYPlot plot = chart.getXYPlot();

        // 设置所有颜色为白色
        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinePaint(Color.WHITE);
        plot.setRangeGridlinePaint(Color.WHITE);
        plot.setOutlinePaint(Color.WHITE);

        // 隐藏坐标轴
        plot.getDomainAxis().setVisible(false);
        plot.getRangeAxis().setVisible(false);

        // 设置渲染器 - 线条颜色设为白色
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        for (int i = 0; i < dataset.getSeriesCount(); i++) {
            renderer.setSeriesPaint(i, getColor(i));  // 这个颜色用于图例
            renderer.setSeriesStroke(i, new BasicStroke(2.0f));  // 极细的线条
            renderer.setSeriesShapesVisible(i, false);
            renderer.setSeriesLinesVisible(i, true);
        }
        plot.setRenderer(renderer);

        // 移除图表边框
        chart.setBorderVisible(false);
        chart.setBackgroundPaint(Color.WHITE);

        // 生成完整图表，然后裁剪掉绘图区域（只保留图例部分）
        BufferedImage fullImage = chart.createBufferedImage(width, height);

        // 计算图例的实际高度（通过查看图例的位置）
        // 简单方法：从底部向上裁剪，只保留图例区域
        // 假设图例高度约为60-80像素，从底部向上取80像素
        int legendOnlyHeight = 80;
        BufferedImage legendOnly = fullImage.getSubimage(0, height - legendOnlyHeight, width, legendOnlyHeight);

        return legendOnly;
    }
    public static void main(String[] args) throws Exception {
        // CSV 文件路径
//        String basePath = "E:\\download\\grid\\MTGP-main\\MTGP-tugboat-dynamic3\\";
        String basePath = "E:\\download\\grid\\MTGP-main\\MTGP-tugboat-dynamic2\\";
//        String basePath = "E:\\download\\grid\\MTGP-main\\MTGP-tugboat-dynamic1\\";
//        String[] ob = {"energyConsumption-","mean-flowtime-"};
        String[] ob = {"energyConsumption-","makespan-","mean-flowtime-","mean-tardiness-"};
        String[] UL = {
                "0.75",
                "0.85",
                "0.95"
        }; // 可以根据实际有18个文件扩展
        String[] DA = {
                "60",
                "80"
        }; // 可以根据实际有18个文件扩展
        List<JFreeChart> charts = new ArrayList<>();

//        // 读取每个 CSV 文件并生成图表
//        for (int i = 0; i < ob.length; i++) {
//            for (int j = 0; j < UL.length; j++) {
////                String csvPath = basePath + ob[i] + UL[j] + "-1.5\\rouFeatureOccuranceFeq.csv";
////                String csvPath = basePath + ob[i] + UL[j] + "-1.5\\rouFeatureOccurance.csv";
//                String csvPath = basePath + ob[i] + UL[j] + "-1.3\\rouFeatureOccurance.csv";
//
//                Map<String, List<Double>> featureMap = readCSV(csvPath);
//                JFreeChart chart = createChart(featureMap, "Energy Level: " +  ob[i] + UL[j]);
//                charts.add(chart);
//            }
//        }
        // 用于存储第一个有效的 featureMap，以便生成统一的图例
        Map<String, List<Double>> sampleFeatureMap = null;

        // 读取每个 CSV 文件并生成图表

            for (int j = 0; j < UL.length; j++) {
                for (int k = 0; k < DA.length; k++) {
                    for (int i = 0; i < ob.length; i++) {
                    String csvPath = basePath + ob[i] + UL[j] + "-1.3"+"-"+DA[k]+"\\rouFeatureOccurance.csv";
//                    String csvPath = basePath + ob[i] + UL[j] + "-1.3"+"-"+DA[k]+"\\seqFeatureOccurance.csv";
                    Map<String, List<Double>> featureMap = readCSV(csvPath);
                    // 保存第一个有效的 featureMap 用于生成图例
                    if (sampleFeatureMap == null && featureMap != null && !featureMap.isEmpty()) {
                        sampleFeatureMap = featureMap;
                    }
                        // 生成简化的场景名称
                        String shortName = simplifyScenarioName(ob[i], UL[j], DA[k]);
                    JFreeChart chart = createChart(featureMap, shortName);
                    if(UL[j].contains("0.75")&&DA[k].contains("60")||UL[j].contains("0.95")&&DA[k].contains("80"))
                    charts.add(chart);
//                    charts.add(chart);
                }
            }
        }


        // 将图表组合成一个 PNG 文件
        int chartWidth = 600;
        int chartHeight = 400;
        int legendHeight = 80;  // 图例区域高度
        int columns = 4;
        int rows = (int) Math.ceil(charts.size() / (double) columns);

        // 总高度 = 子图高度 + 图例高度
        int totalHeight = chartHeight * rows + legendHeight;

        BufferedImage combined = new BufferedImage(
                chartWidth * columns,
                totalHeight,
                BufferedImage.TYPE_INT_ARGB
        );

        Graphics2D g = combined.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, combined.getWidth(), combined.getHeight());

        for (int i = 0; i < charts.size(); i++) {
            int row = i / columns;
            int col = i % columns;
            BufferedImage chartImage = charts.get(i).createBufferedImage(chartWidth, chartHeight);
            g.drawImage(chartImage, col * chartWidth, row * chartHeight, null);
        }
        // 绘制图例（只包含图例，没有红线）
        if (sampleFeatureMap != null) {
            BufferedImage legendImage = createLegendImage(sampleFeatureMap, chartWidth * columns, chartHeight);
            // 只取图例部分（底部）
            g.drawImage(legendImage, 0, chartHeight * rows, null);
        }

        g.dispose();

        // 输出 PNG 文件
//        File outFile = new File(basePath + "seqfeature.png");
        File outFile = new File(basePath + "roufeature.png");

        ImageIO.write(combined, "png", outFile);
        System.out.println("Feature plot saved to: " + outFile.getAbsolutePath());
    }
}
