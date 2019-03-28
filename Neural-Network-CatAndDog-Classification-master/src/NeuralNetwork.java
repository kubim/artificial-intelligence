import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.*;

public class NeuralNetwork {
    public static final double LEARNING_ACCURACY = 0.95;
    private static final double CLASSIFICATION_TARGET_DOG = 0.9;
    private static final double CLASSIFICATION_TARGET_CAT = 0.1;
    private static final int NUM_INPUT_NODES = 10000;
    private static final int NUM_HIDDEN_NODES = 200;
    private static final double LEARNING_FACTOR = 0.3;
    private static final double INTIAL_WEIGHT_VALUE_CLAMP = 0.5;

    private final ArrayList<Double> inputLayer = new ArrayList<>();
    private final ArrayList<HiddenNeuron> hiddenLayer = new ArrayList<>();
    private final ArrayList<Double> hiddenLayerOutputs = new ArrayList<>();
    private final OutputNeuron outputLayer = new OutputNeuron(NUM_HIDDEN_NODES, hiddenLayerOutputs, INTIAL_WEIGHT_VALUE_CLAMP);

    {
        for (int i = 0; i < NUM_HIDDEN_NODES; i++) {
            hiddenLayer.add(new HiddenNeuron(NUM_INPUT_NODES, inputLayer, INTIAL_WEIGHT_VALUE_CLAMP));
        }
    }

    public double trainNetwork(List<Data> data) {
        int matches = 0;
        for (int i = 0; i < data.size(); i++) {
            readInputs(data.get(i).file);
            computeOutput();
            double certainty = classify();
//           System.out.println(data.get(i) + " " + certainty);
            if (data.get(i).petType == (certainty > 0))
                matches++;
            if (data.get(i).petType)
                updateWeights(CLASSIFICATION_TARGET_DOG);
            else
                updateWeights(CLASSIFICATION_TARGET_CAT);
        }

        double accuracy = ((double) matches) / ((double) data.size());
        return accuracy;
    }

    public void testNetwork(ArrayList<File> files) {
        int match=0;
        for (File file : files) {
            readInputs(file);					//----> List inputLayer chứa giá trị xám của 8100 pixel  
            computeOutput();
            double certainty = classify();
            if (certainty > 0) {
                if(file.getName().toLowerCase().contains("dog")){
                    match++;
                }
                System.out.println(file.getName() + ": " + "DOG " + String.format("%.2f", certainty));
            } else {
                if(file.getName().toLowerCase().contains("cat")){
                    match++;
                }
                System.out.println(file.getName() + ": " + "CAT " + String.format("%.2f", -certainty));
            }
        }
        System.out.printf("Matched %d for %d files = %.2f",match,files.size(),(double)match/(double)files.size());
    }
    public void testNetwork(File file) {
            readInputs(file);					//----> List inputLayer chứa giá trị xám của 8100 pixel  
            computeOutput();
            double certainty = classify();
            if (certainty > 0) {
                System.out.println(file.getName() + ": " + "DOG " + String.format("%.2f", certainty));
            } else {
                System.out.println(file.getName() + ": " + "CAT " + String.format("%.2f", -certainty));
            }
    }

    public double testNetwork(ArrayList<File> files, ArrayList<Boolean> genders) {
        int matches = 0;
        for (int i = 0; i < files.size(); i++) {
            readInputs(files.get(i));
            computeOutput();
            if (genders.get(i) == (classify() > 0))
                matches++;
        }

        double accuracy = ((double) matches) / ((double) files.size());
        return accuracy;
    }

    public void saveWeights(FileOutputStream fout) {
        for (Neuron neuron : hiddenLayer) {
            neuron.saveWeights(fout);
        }
        outputLayer.saveWeights(fout);
    }

    public void loadWeights(FileInputStream fin) {
        for (Neuron neuron : hiddenLayer) {
            neuron.loadWeights(fin);
        }
        outputLayer.loadWeights(fin);
    }

    private void updateWeights(double targetOutput) {
        outputLayer.computeGradient(targetOutput);						//optimization
        for (int i = 0; i < NUM_HIDDEN_NODES; i++) {
            hiddenLayer.get(i).computeGradient(targetOutput, outputLayer.getWeight(i), outputLayer.getGradient());
            hiddenLayer.get(i).updateWeights(LEARNING_FACTOR);
        }
        outputLayer.updateWeights(LEARNING_FACTOR);
    }

    private void computeOutput() {
        hiddenLayerOutputs.clear();
        for (Neuron neuron : hiddenLayer) {
            neuron.computeOutput();
            hiddenLayerOutputs.add(neuron.getOutput());
        }
        outputLayer.computeOutput();
    }

    private double getOutput() {
        return outputLayer.getOutput();
    }

    private void readInputs(File file) {
        inputLayer.clear();
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNext()) {
                String[] line = scanner.nextLine().split(" ");
                for (String value : line)
                    inputLayer.add((double) Integer.parseInt(value));

            }

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    private double classify() {
        double output = getOutput() - 0.5;
        double maxDeviation = 0.5;
        double certainty;

        certainty = 100 * (output / maxDeviation);
        return certainty;

    }
}
