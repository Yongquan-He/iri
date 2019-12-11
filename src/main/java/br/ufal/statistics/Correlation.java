package br.ufal.statistics;

import br.ufal.model.Attribute;
import lombok.Getter;
import net.sourceforge.jdistlib.disttest.NormalityTest;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math3.stat.correlation.SpearmansCorrelation;

@Getter
public class Correlation {

    private Attribute x;
    private Attribute y;
    private Double correlation;

    public Correlation(Attribute x, Attribute y) {
        this.x = x;
        this.y = y;
    }

    public Double calculate() throws NullPointerException {
        double[] sampleX = x.getSample();
        double[] sampleY = y.getSample();

        double xTest = NormalityTest.anderson_darling_statistic(sampleX);
        double xTestPValue = NormalityTest.anderson_darling_pvalue(xTest, sampleX.length);

        double yTest = NormalityTest.anderson_darling_statistic(sampleY);
        double yTestPValue = NormalityTest.anderson_darling_pvalue(yTest, sampleY.length);

        if (xTestPValue > 0.05 && yTestPValue > 0.05) {
            PearsonsCorrelation correlationMap = new PearsonsCorrelation();
            correlation = correlationMap.correlation(sampleX, sampleY);
        } else {
            SpearmansCorrelation correlationMap = new SpearmansCorrelation();
            correlation = correlationMap.correlation(sampleX, sampleY);
        }

        return correlation;
    }

    @Override
    public boolean equals(Object obj) {
        return x.equals((Attribute) obj);
    }

    public void printArray(double[] array, String coluna) {
        System.out.println("=================");
        System.out.println(coluna);
        for (int j = 0; j < array.length; j++) {   //this equals to the column in each row.
            double value = array[j];
            System.out.printf("%.2f  ", value);
        }
        System.out.println(); //change line on console as row comes to end in the matrix.
        System.out.println("=================");
    }

    public void printMatrix(double[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {         //this equals to the row in our matrix.
            for (int j = 0; j < matrix[i].length; j++) {   //this equals to the column in each row.
                double value = matrix[i][j];
                System.out.printf("%.2f  ", value);
            }
            System.out.println(); //change line on console as row comes to end in the matrix.
        }
    }
}
