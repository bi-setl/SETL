package practice;


import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;

import javax.swing.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class RDFGraphDemo extends JFrame {

    public RDFGraphDemo() {
        super("RDF Graph Demo");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        try {
            Map<Resource, Map<Property, RDFNode>> graph = createGraphFromRDF();
            displayGraph(graph);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Map<Resource, Map<Property, RDFNode>> createGraphFromRDF() {
        Map<Resource, Map<Property, RDFNode>> graph = new HashMap<>();

        String rdfFile = "C:\\Users\\Amrit\\Documents\\cities.rdf"; // Update with the actual path to your RDF file

        Model model = ModelFactory.createDefaultModel();
        model.read(rdfFile);

        StmtIterator iterator = model.listStatements();
        while (iterator.hasNext()) {
            Statement statement = iterator.next();
            Resource subject = statement.getSubject();
            Property predicate = statement.getPredicate();
            RDFNode object = statement.getObject();

            graph.computeIfAbsent(subject, k -> new HashMap<>()).put(predicate, object);
        }

        return graph;
    }

    private void displayGraph(Map<Resource, Map<Property, RDFNode>> graph) {
        // Create a JFreeChart CategoryDataset for the graph
        CategoryDataset categoryDataset = createCategoryDataset(graph);

        // Create a JFreeChart line chart
        JFreeChart chart = ChartFactory.createLineChart(
                "RDF Graph Demo",
                "City",
                "Value",
                categoryDataset
        );

        // Customize the plot to show lines between points
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        LineAndShapeRenderer renderer = new LineAndShapeRenderer();
        renderer.setBaseShapesVisible(true);
        plot.setRenderer(renderer);

        // Display the chart in a JPanel
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(800, 600));
        setContentPane(chartPanel);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private CategoryDataset createCategoryDataset(Map<Resource, Map<Property, RDFNode>> graph) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        // Add data to the dataset
        for (Resource city : graph.keySet()) {
            for (Map.Entry<Property, RDFNode> entry : graph.get(city).entrySet()) {
                Property property = entry.getKey();
                RDFNode object = entry.getValue();

                // Check if the object is a literal and represents a numeric value
                if (object.isLiteral() && ((Literal) object).getLexicalForm().matches("[+-]?\\d*\\.?\\d+")) {
                    dataset.addValue(((Literal) object).getDouble(), property.getLocalName(), city.toString());
                }
            }
        }

        return dataset;
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RDFGraphDemo());
    }
}
