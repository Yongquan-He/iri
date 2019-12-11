package br.ufal.statistics;

import br.ufal.model.Attribute;
import br.ufal.utils.Utils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.lazy.IBk;
import weka.core.Instances;
import weka.core.converters.ArffLoader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

public class KNNDemo {

    private  static List<String> especialista = Arrays.asList("Plataforma","Arquitetura","Domínio","Descritivo_da_US",
            "Modulo","Operacao","Tarefa_mapeada","Tarefa_original","Camada","Linguagem","Framework",
            "API","Persistencia","Outras_Tags");

    /**
     * This method is to load the data set.
     * @param fileName
     * @return
     * @throws IOException
     */
    public static Instances getDataSet(String fileName) throws IOException {
        /**
         * we can set the file i.e., loader.setFile("finename") to load the data
         */
        int classIdx = 1;
        /** the arffloader to load the arff file */
        ArffLoader loader = new ArffLoader();
        /** load the traing data */
        loader.setSource(KNNDemo.class.getResourceAsStream("/" + fileName));
        /**
         * we can also set the file like loader3.setFile(new
         * File("test-confused.arff"));
         */
        //loader.setFile(new File(fileName));
        Instances dataSet = loader.getDataSet();
        /** set the index based on the data given in the arff files */
        dataSet.setClassIndex(classIdx);

        return dataSet;
    }


    /**
     * @throws Exception
     */
    public static void process() throws Exception {
        Instances data = getDataSet("data.arff");
        data.setClassIndex(data.numAttributes() - 1);
        //k - the number of nearest neighbors to use for prediction
        Classifier ibk = new IBk(1);
        ibk.buildClassifier(data);

        System.out.println(ibk);

        Evaluation eval = new Evaluation(data);
        eval.evaluateModel(ibk, data);
        /** Print the algorithm summary */
        System.out.println("** KNN Demo  **");
        System.out.println(eval.toSummaryString());
        System.out.println(eval.toClassDetailsString());
        System.out.println(eval.toMatrixString());
    }

    public static Sheet filterProject(Sheet sheet, String projID){
        int qtd = sheet.getPhysicalNumberOfRows();
        int qtdRemoved = 0;
        for (int i=1; i < qtd; i++){
            Row row = sheet.getRow(i);
            Cell cell = row.getCell(0);
            if (cell.getCellType() == CellType.STRING){
                if (!cell.getStringCellValue().equalsIgnoreCase(projID)){
                    sheet.removeRow(row);
                    qtdRemoved++;
                }
            }
        }

        return sheet;
    }

    //gerar lista de atributos mais importantes
    //https://ikuz.eu/csv2arff/#
    public static void main(String[] args) {
        

        try {
            Utils utils = new Utils();

            String fileName = "Backlogs";

            List<String> allAttributes = new ArrayList<>();
            Map<String, List<Attribute>> map = new HashMap<>();

            for (int p=0; p<= 13; p++ ) {
                InputStream inputStreamXLSX = utils.readFileXLSX(fileName);

                Workbook workbook = new XSSFWorkbook(inputStreamXLSX);
                Sheet datatypeSheet = workbook.cloneSheet(0);

                String projectID;

                if (p == 0) {
                    projectID = "Especialista";
                }else if (p == 13) {
                    projectID = "Todos";
                } else {
                    projectID = String.format("P%02d", p);

                    datatypeSheet = filterProject(datatypeSheet, projectID);
                }

                System.out.println(projectID);

                Selection selection = new Selection(datatypeSheet);
                selection.run();
                allAttributes = selection.getAttributes().stream().map(Attribute::getName).collect(Collectors.toList());

                List<Attribute> selectedAttributes = new ArrayList<>();
                List<String> selectedAttributesStr;
                if (p == 0){
                    selectedAttributesStr = especialista;
                } else {
                    map.put(projectID, selection.getAttributes());

                    selectedAttributes = selection.getSelectedAttributes();
                    selectedAttributesStr = selectedAttributes.stream()
                            .map(Attribute::getName).collect(Collectors.toList());
                }
                System.out.println(selectedAttributes);

                int[] attrIndex = new int[selectedAttributesStr.size()];
                int i = 0;
                for (String attr : selectedAttributesStr) {
                    int index = allAttributes.indexOf(attr);
                    attrIndex[i++] = index;
                }
                Arrays.sort(attrIndex);

                /*Instances instances = utils.readInstances(fileName);
                instances.setClassIndex(instances.attribute(moreRelevantAttribute.getName()).index());

                Remove removeFilter = new Remove();
                removeFilter.setAttributeIndicesArray(attrIndex);
                removeFilter.setInvertSelection(true); //keep select columns
                removeFilter.setInputFormat(instances);
                instances = Filter.useFilter(instances, removeFilter);*/

                inference(datatypeSheet, attrIndex, selectedAttributes);

                inputStreamXLSX.close();
                workbook.close();
            }

            print(allAttributes, map);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void print(List<String> atributos, Map<String, List<Attribute>> map) {

        String result = "Atributos;Especialista;";
        for (int i = 1; i <= 13; i++) {
            result += (i == 13) ? "Todos" : String.format("P%02d;", i);
        }
        result += "\n";
        for (String atr : atributos) {
            result += String.format("%s;", atr);
            for (int i = 0; i <= 13; i++) {
                if (i==0) {
                    if (especialista.contains(atr)) {
                        result += String.format("%c", 'X');
                        //result += "1";
                    }
                } else {
                    String projectID = (i == 13) ? "Todos" : String.format("P%02d", i);
                    List<Attribute> lista = map.getOrDefault(projectID, new ArrayList<>());
                    Optional<Attribute> first = lista.stream().filter(a -> a.getName().equalsIgnoreCase(atr)).findFirst();
                    if (first.isPresent()) {
                        Attribute attribute = first.get();
                        if (attribute.getIsRelevant()) {
                            //result += String.format("%.2f", attribute.getGeneralValue()).replaceAll("[,]",".");
                            result += String.format("%c", 'X');
                        } else {
                            //result += String.format("%.2f", attribute.getGeneralValue()).replaceAll("[,]",".");
                        }
                    }
                }

                result += ";";
            }
            result += "\n";
        }

        System.out.println(result);
    }

    public static void inference(Sheet sheet, int[] attributesTarget, List<Attribute> selectedAttributes) {
        Set<Row> equals = new HashSet<>();
        List<Integer> linesReaded = new ArrayList<>();

        int qtdVP = 0;
        int qtdVN = 0;
        int qtdFP = 0;
        int qtdFN = 0;

        for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
            Row instance1 = sheet.getRow(i);

            if (instance1 == null || linesReaded.contains(i)) continue;

            String equalsLines = "linhas: " + (i+1);

            boolean addInstance1 = false;

            for (int j = 1; j < sheet.getPhysicalNumberOfRows(); j++) {
                Row instance2 = sheet.getRow(j);

                if (i==j || instance2 == null || linesReaded.contains(j)) continue;

                boolean isEqual = false;

                for (int attrIndex = 0; attrIndex < attributesTarget.length; attrIndex++) {
                    Attribute attribute;
                    if (selectedAttributes.isEmpty()) { // é especialista
                        attribute = new Attribute();
                        attribute.setEntropyValue(0d);
                    } else {
                        attribute = selectedAttributes.get(attrIndex);
                    }

                    int index = attributesTarget[attrIndex];
                    //if (index < 0) continue;
                    Cell atr1Obj = instance1.getCell(index);
                    Cell atr2Obj = instance2.getCell(index);
                    String value1 = atr1Obj.getStringCellValue();
                    String value2 = atr2Obj.getStringCellValue();

                    int compare = StringUtils.compare(value1, value2);

                    if (compare == 0) {
                        isEqual = true;
                    } else if (attribute.getEntropyValue() == 1f) {
                        continue;
                    } else {
                        isEqual = false;
                        break;
                    }
                }


                Double isSameAs = instance2.getCell(24).getNumericCellValue();
                //Double qtd = instance2.getCell(25).getNumericCellValue();

                if (isEqual) {
                    addInstance1 = true;
                    equals.add(instance2);
                    linesReaded.add(j);

                    equalsLines += "," + (j+1);
                    //System.out.printf("%d ",instance2.getRowNum());

                    if (isSameAs == 1f) {
                        qtdVP++;
                    } else {
                        qtdFP++;
                    }
                }
            }

            Double isSameAs = instance1.getCell(24).getNumericCellValue();
            if (addInstance1) {
                equals.add(instance1);
                //System.out.println(equalsLines);
                if (isSameAs == 1f) {
                    qtdVP++;
                } else {
                    qtdFP++;
                }

            } else {
                if (isSameAs == 1f) {
                    qtdVN++;
                } else {
                    qtdFN++;
                }
            }
        }
        System.out.println("SIZE EQUALS: " + equals.size());
        System.out.println("VP," + qtdVP );
        System.out.println("FP," + qtdFP );
        System.out.println("VN," + qtdVN );
        System.out.println("FN," + qtdFN );
        System.out.println();
        System.out.println();
    }
}