package tm.clustering;

import javafx.scene.layout.Border;
import org.apache.commons.math3.ml.clustering.Cluster;
import smile.clustering.SpectralClustering;
import smile.data.AttributeDataset;
import smile.data.parser.DelimitedTextParser;
import smile.plot.Palette;
import smile.plot.PlotCanvas;
import smile.plot.ScatterPlot;
import tm.Main;
import tm.cvs.CSVUtils;

import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.stream.IntStream;

/**
 * Created by Anthony Tjuatja on 6/29/2017.
 */
public class Clustering extends JPanel {

    public Clustering() throws URISyntaxException, IOException, ParseException {

        setLayout(new BorderLayout());

        DelimitedTextParser parser = new DelimitedTextParser();
        parser.setDelimiter(",");
        AttributeDataset data = parser.parse("Euclidian Dataset", new File(Main.class.getClassLoader().getResource("eucSim.csv").toURI()));
        double[][] dataset = data.toArray(new double[data.size()][]);
        double[][] coor = IntStream.range(0 , dataset.length).
                mapToObj(x -> new double[] {x / 5, x % 5}).toArray(double[][]::new);
        SpectralClustering spectral = new SpectralClustering(dataset, 10, 0.2);
        PlotCanvas plot = ScatterPlot.plot(coor, 'o');

        for (int k = 0; k < spectral.getNumClusters(); k++) {
            double[][] cluster = new double[spectral.getClusterSize()[k]][];
            for (int i = 0, j = 0; i < dataset.length; i++) {
                if (spectral.getClusterLabel()[i] == k) {
                    cluster[j++] = new double[] {i / 5, i % 5};
                }
            }
            plot.points(cluster, 'o', Palette.COLORS[k % Palette.COLORS.length]);
        }

       // plotCanvas = ScatterPlot.plot(coor, spectral.getClusterLabel(), 'o', Palette.COLORS);
        add(plot, BorderLayout.CENTER);

//        try {
//            plotCanvas.save();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    public static void main(String[] args) throws IOException, URISyntaxException, ParseException {

        Clustering clustering = new Clustering();
        JFrame f = new JFrame("Spectral Clustering");
        f.setSize(new Dimension(1000, 1000));
        f.setLocationRelativeTo(null);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.getContentPane().add(clustering);
        f.setVisible(true);
    }
}
