import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private NeuralNetwork neuralNetwork = new NeuralNetwork();
    private File DogFolder = new File("res/data/Dog");
    private File CatFolder = new File("res/data/Cat");
    private File testFolder = new File("res/data/Test");

    private ArrayList<File> Testfiles = new ArrayList<>();

    private List<Data> Listdata;
    
    public static void main(String[] args) {
    	Main main = new Main();
    	main.train();
    	main.test();
//    	for (int i = 20; i <=50; i++) {
//        	main.test(new File("C:\\Users\\kubim\\Desktop\\dataset\\testnolabel\\"+i+".txt"));
//		}
	}

    //Đọc file từ folder ==> Lưu data vào Listdata và Train NN
    //In accuracy sau mỗi lần train
    public void train() {
        Listdata = new ArrayList<>();													
        File[] dogs = DogFolder.listFiles();								
        File[] cats = CatFolder.listFiles();
        int size = dogs.length > cats.length ? dogs.length : cats.length;
        for (int i = 0; i < size; i++) {

            if (i < dogs.length && !dogs[i].isHidden() && dogs[i].isFile())
                Listdata.add(new Data(dogs[i], true));
            if (i < cats.length && !cats[i].isHidden() && cats[i].isFile())
                Listdata.add(new Data(cats[i], false));
        }
        double accuracy;
        for (int i = 0; i < 500; i++) {									//iterations
            accuracy = neuralNetwork.trainNetwork(Listdata);			
            System.out.println("Accuracy: " + accuracy);				
            if(accuracy >= NeuralNetwork.LEARNING_ACCURACY)				
                break;
        }
        
        //ghi lại trọng số
        try (FileOutputStream fout = new FileOutputStream("weights")) {
            neuralNetwork.saveWeights(fout);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public void test() {
        Testfiles = new ArrayList<>();
        for (File file : testFolder.listFiles()) {
            if (!file.isFile() || file.isHidden())
                continue;
            Testfiles.add(file);
        }

        try (FileInputStream fin = new FileInputStream("weights")) {
            neuralNetwork.loadWeights(fin);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        neuralNetwork.testNetwork(Testfiles);
    }
    public void test(File file) {
        try (FileInputStream fin = new FileInputStream("weights")) {
            neuralNetwork.loadWeights(fin);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        neuralNetwork.testNetwork(file);
    }
}
