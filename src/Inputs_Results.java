import java.awt.*;
import java.awt.font.*;
import java.awt.geom.*;

import javax.swing.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.List;
import java.util.Scanner;

public class Inputs_Results  extends JPanel {
		
	public static ArrayList<ArrayList<RuleElement>> getDataset(String fileName){
		ArrayList<ArrayList<RuleElement>> dataset, temp;
		String line;
		int count;
		dataset = new ArrayList<ArrayList<RuleElement>>();
		
		
		//Open file for reading
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(fileName));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		//Open file for writing
		File file = new File("testing.sql");
		if (!file.exists())
			try {
				file.createNewFile();
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
		FileWriter fw = null;
		try {
			fw = new FileWriter(file.getAbsoluteFile());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		BufferedWriter bw = new BufferedWriter(fw);
				
		count = 0;
		try {
			while ((line = br.readLine()) != null){
				try {
					bw.write(line + '\n');
					count++;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				if(count == 50){
					try {
						bw.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					temp = getcrud.getDataset("testing.sql");
					for(int i = 0; i < temp.size(); i++)
						dataset.add(temp.get(i));
					try {
						fw = new FileWriter(file.getAbsoluteFile());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					bw = new BufferedWriter(fw);
					count = 0;
				}
			}
			if(count > 0){
				try {
					bw.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				temp = getcrud.getDataset("testing.sql");
				for(int i = 0; i < temp.size(); i++)
					dataset.add(temp.get(i));
				count = 0;
			}
			
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		//Close reading file
		try {
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//Close writing file
		try {
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return dataset;
	}
	
	//Graphs	
	private int width = 800;
    private int height = 400;
    private int padding = 25;
    private int labelPadding = 25;
    private Color lineColor = new Color(44, 102, 230, 180);
    private Color pointColor = new Color(100, 100, 100, 250);
    private Color gridColor = new Color(200, 200, 200, 200);
    private static final Stroke GRAPH_STROKE = new BasicStroke(2f);
    private int pointWidth = 6;
    private int numberYDivisions = 10;
    private ArrayList<Float> values;

    public Inputs_Results(ArrayList<Float> values) {
        this.values = values;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        double xScale = ((double) getWidth() - (2 * padding) - labelPadding) / (values.size());
        double yScale = ((double) getHeight() - 2 * padding - labelPadding) / (getMaxValue() - getMinValue());

        List<Point> graphPoints = new ArrayList<>();
        for (int i = 1; i <= values.size(); i++) {
            int x1 = (int) (i * xScale + padding + labelPadding);
            int y1 = (int) ((getMaxValue() - values.get(i-1)) * yScale + padding);
            graphPoints.add(new Point(x1, y1));
        }

        // draw white background
        g2.setColor(Color.WHITE);
        g2.fillRect(padding + labelPadding, padding, getWidth() - (2 * padding) - labelPadding, getHeight() - 2 * padding - labelPadding);
        g2.setColor(Color.BLACK);

        // create hatch marks and grid lines for y axis.
        for (int i = 0; i < numberYDivisions + 1; i++) {
            int x0 = padding + labelPadding;
            int x1 = pointWidth + padding + labelPadding;
            int y0 = getHeight() - ((i * (getHeight() - padding * 2 - labelPadding)) / numberYDivisions + padding + labelPadding);
            int y1 = y0;
            if (values.size() > 0) {
                g2.setColor(gridColor);
                g2.drawLine(padding + labelPadding + 1 + pointWidth, y0, getWidth() - padding, y1);
                g2.setColor(Color.BLACK);
                String yLabel = ((int) ((getMinValue() + (getMaxValue() - getMinValue()) * ((i * 1.0) / numberYDivisions)) * 100)) / 100.0 + "";
                FontMetrics metrics = g2.getFontMetrics();
                int labelWidth = metrics.stringWidth(yLabel);
                g2.drawString(yLabel, x0 - labelWidth - 5, y0 + (metrics.getHeight() / 2) - 3);
            }
            g2.drawLine(x0, y0, x1, y1);
        }

        // and for x axis
        for (int i = 0; i <= values.size(); i++) {
            if (values.size() > 1) {
                int x0 = i * (getWidth() - padding * 2 - labelPadding) / (values.size()) + padding + labelPadding;
                int x1 = x0;
                int y0 = getHeight() - padding - labelPadding;
                int y1 = y0 - pointWidth;
                if ((i % ((int) ((values.size() / 20.0)) + 1)) == 0) {
                    g2.setColor(gridColor);
                    g2.drawLine(x0, getHeight() - padding - labelPadding - 1 - pointWidth, x1, padding);
                    g2.setColor(Color.BLACK);
                    String xLabel = (5 * i) + "";
                    FontMetrics metrics = g2.getFontMetrics();
                    int labelWidth = metrics.stringWidth(xLabel);
                    g2.drawString(xLabel, x0 - labelWidth / 2, y0 + metrics.getHeight() + 3);
                }
                g2.drawLine(x0, y0, x1, y1);
            }
        }

        // create x and y axes 
        g2.drawLine(padding + labelPadding, getHeight() - padding - labelPadding, padding + labelPadding, padding);
        g2.drawLine(padding + labelPadding, getHeight() - padding - labelPadding, getWidth() - padding, getHeight() - padding - labelPadding);

        Stroke oldStroke = g2.getStroke();
        g2.setColor(lineColor);
        g2.setStroke(GRAPH_STROKE);
        for (int i = 0; i < graphPoints.size() - 1; i++) {
            int x1 = graphPoints.get(i).x;
            int y1 = graphPoints.get(i).y;
            int x2 = graphPoints.get(i + 1).x;
            int y2 = graphPoints.get(i + 1).y;
            g2.drawLine(x1, y1, x2, y2);
        }

        g2.setStroke(oldStroke);
        g2.setColor(pointColor);
        for (int i = 0; i < graphPoints.size(); i++) {
            int x = graphPoints.get(i).x - pointWidth / 2;
            int y = graphPoints.get(i).y - pointWidth / 2;
            int ovalW = pointWidth;
            int ovalH = pointWidth;
            g2.fillOval(x, y, ovalW, ovalH);
        }
    }

    private double getMinValue() {
        double minValue = Float.MAX_VALUE;
        for (Float score : values) {
            minValue = Math.min(minValue, score);
        }
        return minValue;
    }

    private double getMaxValue() {
        double maxValue = Float.MIN_VALUE;
        for (Float score : values) {
            maxValue = Math.max(maxValue, score);
        }
        return maxValue;
    }

    private static void createAndShowGui(ArrayList<Float> values, String title) {
        /*List<Double> scores = new ArrayList<>();
        Random random = new Random();
        int maxDataPoints = 40;
        int maxScore = 10;
        for (int i = 0; i < maxDataPoints; i++) {
            scores.add((double) random.nextDouble() * maxScore);
//            scores.add((double) i);
        }*/
        Inputs_Results mainPanel = new Inputs_Results(values);
        mainPanel.setPreferredSize(new Dimension(800, 600));
        JFrame frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(mainPanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    
    static int truePositives, falsePositives, falseNeagtives;
    
    private static float getPrecision(){
    	return (float) truePositives / (truePositives + falsePositives);
    }
    
    private static float getRecall(){
    	return (float) truePositives / (truePositives + falseNeagtives);
    }
    
    public static void main(String[] args) {
    	ArrayList<Float> precision = new ArrayList<Float>();
    	ArrayList<Float> recall = new ArrayList<Float>();
    	
    	//For 5 queries
    	truePositives = 1;
		falsePositives = 2;
		falseNeagtives = 3;
		precision.add(getPrecision());
    	recall.add(getRecall());
    	//For 10 queries
    	truePositives = 4;
		falsePositives = 5;
		falseNeagtives = 6;
		precision.add(getPrecision());
    	recall.add(getRecall());
    	//For 15 queries
    	truePositives = 7;
		falsePositives = 8;
		falseNeagtives = 9;
		precision.add(getPrecision());
    	recall.add(getRecall());
    	//For 20 queries
    	truePositives = 10;
		falsePositives = 11;
		falseNeagtives = 12;
		precision.add(getPrecision());
    	recall.add(getRecall());
    	
    	
    	/*     			//INPUT SYSTEM
    	Scanner s = new Scanner(System.in);
    	for(int i = 1; i <= 4; i++){
    		System.out.println("FOR " + (i * 5) + " QUERIES:-");
    		System.out.print("True Positives: ");
    		truePositives = s.nextInt();
    		System.out.print("False Positives: ");
    		falsePositives = s.nextInt();
    		System.out.print("False Negatives: ");
    		falseNeagtives = s.nextInt();
    		precision.add(getPrecision());
        	recall.add(getRecall());
    	}
    	s.close();
    	*/
      SwingUtilities.invokeLater(new Runnable() {
         public void run() {
            createAndShowGui(precision, "Precison Curve");
            createAndShowGui(recall, "Recall Curve");
         }
      });
   }
}
