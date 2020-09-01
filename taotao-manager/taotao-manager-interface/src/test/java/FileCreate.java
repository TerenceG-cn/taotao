import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileCreate {
    public static void main(String[] args) {//主程序，程序入口
        File file = new File("E:\\pic\\"+"20200831");
        if (!file.exists()) {//如果文件夹不存在
            file.mkdir();//创建文件夹
        }

    }
}
