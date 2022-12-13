package club.xiaojiawei.hearthstone.utils.extra;

import lombok.SneakyThrows;

import java.io.*;
import java.util.ArrayList;

/**
 * @author 肖嘉威
 * @date 2022/11/28 16:08
 */
public class FilterPowerLogExtraUtil {

    public static void main(String[] args) {
        filter("C:\\Users\\zerg\\Desktop\\wild.txt");
    }

    /**
     * 过滤官方的Power.log，只保留需要读取的信息
     * @param path
     */
    @SneakyThrows
    public static void filter(String path){
        File file = new File(path);
        ArrayList<String> lists = new ArrayList<>();
        try(BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        ){
            String s;
            while ((s = bufferedReader.readLine()) != null){
                if (s.contains("PowerTaskList.DebugPrintPower")){
                    lists.add(s.strip());
                }
            }
            System.out.println("读取完成");
            try(BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file))){
                for (String list : lists) {
                    bufferedWriter.write(list);
                    bufferedWriter.write("\r");
                }
            }
            System.out.println("写入完成");
        }
    }
}
