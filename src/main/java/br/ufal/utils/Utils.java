package br.ufal.utils;

import weka.core.Instances;
import weka.core.converters.ConverterUtils;

import java.io.InputStream;

public class Utils {

    private InputStream readFile(String fileNameExt) {
        return getClass().getClassLoader()
                .getResourceAsStream(fileNameExt);
    }

    public InputStream readFileXLSX(String fileName) {
        String fileNameExt = fileName.endsWith(".xlsx") ? fileName : fileName + ".xlsx";
        return readFile(fileNameExt);
    }

    private InputStream readFileARFF(String fileName) {
        String fileNameExt = fileName.endsWith(".arff") ? fileName : fileName + ".arff";
        return readFile(fileNameExt);
    }

    public Instances readInstances(String fileName) throws Exception {
        InputStream inputStreamArff = readFileARFF(fileName);

        ConverterUtils.DataSource dataSource = new ConverterUtils.DataSource(inputStreamArff);
        Instances instances = dataSource.getDataSet();

        return instances;
    }
}
