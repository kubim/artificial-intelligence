import java.io.File;

/**
 * Created by stasbar on 25.05.2017.
 */
public class Data{							//Lớp Data   ----> dữ liệu sẽ dùng ----> các thuộc tính: 
													
    public File file;						//+ File:    file
    public boolean petType;					//+ petType:  1 is Dog || 0 is Cat

    public Data(File file, boolean petType) {					//Hàm khởi tạo đối tượng Data
        this.file = file;
        this.petType = petType;				
    }

}
