package br.ufal.statistics;

import br.ufal.model.Attribute;
import br.ufal.utils.NumericUtils;
import lombok.Getter;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.*;
import java.util.stream.Collectors;

public class Selection {

    private final NumericUtils numericUtils = new NumericUtils();
    private final Entropy entropy = new ShannonEntropy();

    private Sheet sheet;
    private RealMatrix realMatrix;

    @Getter
    private List<Attribute> attributes = new ArrayList<>();

    public Selection(Sheet spreadsheet) {
        this.sheet = spreadsheet;
    }

    public void run() {
        parseHeader();
        parseData();
    }

    private void parseHeader() {
        Row header = sheet.getRow(0);

        header.cellIterator().forEachRemaining(c -> {
            Attribute attribute = new Attribute();
            attribute.setName(c.getStringCellValue());

            attributes.add(attribute);
        });
    }

    private void parseData() {
        int qtdColumns = attributes.size();
        int qtdRows = sheet.getPhysicalNumberOfRows();
        realMatrix = MatrixUtils.createRealMatrix(qtdRows, qtdColumns);

        for (int c=0; c < qtdColumns; c++) {

            int cuurentRow = 0;
            for (int r = 0; r < sheet.getLastRowNum(); r++) {

                Row row = sheet.getRow(r);

                if (row == null) continue;
                Cell currentCell = row.getCell(c);

                if (currentCell == null) {
                    System.out.println(r + " " + c);
                }

                double value;
                if (currentCell.getCellType() == CellType.STRING) {
                    value = numericUtils.textToNumeric(currentCell.getStringCellValue());
                } else if (currentCell.getCellType() == CellType.BOOLEAN) {
                    value = currentCell.getBooleanCellValue() == true ? 1f : 0f;
                } else if (currentCell.getCellType() == CellType.NUMERIC) {
                    value = currentCell.getNumericCellValue();
                } else if (currentCell.getCellType() == CellType.BLANK) {
                    value = numericUtils.textToNumeric(" ");
                    //continue;
                } else {
                    System.out.println(currentCell.getCellType());
                    throw new NullPointerException("Erro no tipo de dados");
                }

                realMatrix.addToEntry(cuurentRow++, c, value);
            }
        }

        //entropy
        for (int c=0; c < qtdColumns; c++) {
            double[] column = realMatrix.getColumn(c);
            List<Double> values = new ArrayList<>();
            for (double d : column) {
                //if (d ==0) continue;
                values.add(d);
            }

            Double entropyValue = entropy.calculate(values);
            Double variation = entropy.variation(values);
            double[] sample = values.stream().mapToDouble(Double::doubleValue).toArray();

            Attribute attribute = attributes.get(c);
            attribute.setEntropyValue(entropyValue);
            attribute.setVariation(variation);
            attribute.setSample(sample);
        }

        //correlation
        for (int i=0; i< attributes.size(); i++) {
            Attribute attribute1 = attributes.get(i);

            for (int j = i+1; j < attributes.size(); j++) {
                Attribute attribute2 = attributes.get(j);
                Correlation correlation = new Correlation(attribute1, attribute2);
                Double correlationValue = correlation.calculate();
                if (correlationValue > 0.5) {
                    attribute1.getCorrelation().add(correlation);
                    attribute2.getCorrelation().add(correlation);
                }
            }

            //Optional<Correlation> maxCorrelation = attribute1.getCorrelation().stream().max(Comparator.comparingDouble(Correlation::getCorrelation));
            //Double generalValue = (attribute1.getEntropyValue() + attribute1.getVariation()) / 2;
            Double generalValue = attribute1.getEntropyValue() * 0.5f + attribute1.getVariation() * 0.5f;
            attribute1.setGeneralValue(generalValue);

            Boolean isRelevant = attribute1.getGeneralValue() >= 0.5;
            attribute1.setIsRelevant(isRelevant);
        }
    }

    public List<Attribute> getSelectedAttributes() {
        return attributes.stream()
                .filter(a -> a.getIsRelevant())
                .collect(Collectors.toList());
    }

    public Optional<Attribute> getMoreRelevantAttribute() {
         return attributes.stream()
                 .filter(a -> a.getIsRelevant())
                .max(Comparator.comparingDouble(Attribute::getGeneralValue));
    }

}
