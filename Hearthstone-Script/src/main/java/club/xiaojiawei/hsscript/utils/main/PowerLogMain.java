package club.xiaojiawei.hsscript.utils.main;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * 将power.log日志中的不相干信息去除，方便查看
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/8/28 15:45
 */
public class PowerLogMain {

    public static void main(String[] args) {
        decontamination("S:\\Hearthstone\\Logs\\Hearthstone_2024_11_04_23_33_25", true);
    }

    @SuppressWarnings("all")
    private static void decontamination(String path, boolean renew){
        if (renew){
            try(RandomAccessFile accessFile = new RandomAccessFile(new File(path + "\\Power.log"), "r");
                FileOutputStream outputStream = new FileOutputStream(path + "\\Power_renew.log")
            ){
                String line;
                while ((line = accessFile.readLine()) != null){
                    if (line.contains("PowerTaskList")){
                        outputStream.write(new String((line.replace("PowerTaskList.Debug", "") + "\n").getBytes(StandardCharsets.ISO_8859_1)).getBytes(StandardCharsets.UTF_8));
                    }
                }
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }else {
            try(RandomAccessFile accessFile = new RandomAccessFile(new File(path + "\\Power.log"), "rw")){
                String line;
                long leftPoint = accessFile.getFilePointer(), rightPoint;
                while ((line = accessFile.readLine()) != null){
                    rightPoint = accessFile.getFilePointer();
                    if (line.contains("PowerTaskList")){
                        accessFile.seek(leftPoint);
                        accessFile.write(new String((line.replace("PowerTaskList.Debug", "") + "\n").getBytes(StandardCharsets.ISO_8859_1)).getBytes(StandardCharsets.UTF_8));
                        leftPoint = accessFile.getFilePointer();
                        accessFile.seek(rightPoint);
                    }
                }
                if (leftPoint != 0){
                    accessFile.setLength(leftPoint);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }
}
