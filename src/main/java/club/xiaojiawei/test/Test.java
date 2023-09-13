package club.xiaojiawei.test;

import lombok.Getter;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author 肖嘉威
 * @date 2023/9/13 12:51
 * @msg
 */
public class Test {
    private static MyTable myTable=new MyTable();

    public static void main(String[] args) throws IOException {
        Runtime runtime = Runtime.getRuntime();
        Process process = null;
        process = runtime
                .exec("cmd /c reg query HKEY_LOCAL_MACHINE\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\");
        BufferedReader in = new BufferedReader(new InputStreamReader(process
                .getInputStream(),"GBK"));

        String string = null;
        while ((string = in.readLine()) != null) {
            System.out.println(string);
            process = runtime.exec("cmd /c reg query " + string
                    + " /v DisplayName");
            System.out.println(process);

            BufferedReader name = new BufferedReader(new InputStreamReader(
                    process.getInputStream(),"GBK"));
            String[] message = queryValue(string);
            if(message!=null) myTable.addRow(message);
        }
        in.close();
        process.destroy();
        System.out.println(Arrays.toString(myTable.getColNames()));

    }
    private static String[] queryValue(String string) throws IOException {
        String nameString = "";
        String versionString = "";

        String publisherString="";
        String uninstallPathString = "";

        Runtime runtime = Runtime.getRuntime();
        Process process = null;
        BufferedReader br = null;

        process = runtime.exec("cmd /c reg query " + string + " /v DisplayName");
        br = new BufferedReader(new InputStreamReader(process
                .getInputStream(),"GBK"));
        br.readLine();br.readLine();//去掉前两行无用信息
        if((nameString=br.readLine())!=null){
            nameString=nameString.replaceAll("DisplayName    REG_SZ    ", "");  //去掉无用信息
        }


        process = runtime.exec("cmd /c reg query " + string + " /v DisplayVersion");
        br = new BufferedReader(new InputStreamReader(process
                .getInputStream(),"GBK"));
        br.readLine();br.readLine();//去掉前两行无用信息
        if((versionString=br.readLine())!=null){
            versionString=versionString.replaceAll("DisplayVersion    REG_SZ    ", ""); //去掉无用信息
        }

        process = runtime.exec("cmd /c reg query " + string + " /v Publisher");
        br = new BufferedReader(new InputStreamReader(process
                .getInputStream(),"GBK"));
        br.readLine();br.readLine();//去掉前两行无用信息
        if((publisherString=br.readLine())!=null){
            publisherString =publisherString.replaceAll("Publisher    REG_SZ    ", ""); //去掉无用信息
        }

        process = runtime.exec("cmd /c reg query " + string + " /v UninstallString");
        br = new BufferedReader(new InputStreamReader(process
                .getInputStream(),"GBK"));
        br.readLine();br.readLine();//去掉前两行无用信息
        if((uninstallPathString=br.readLine())!=null){
            uninstallPathString=uninstallPathString.replaceAll("UninstallString    REG_SZ    ", "");    //去掉无用信息
        }

        String[] resultString=new String[4];
        resultString[0]= nameString ;//== null ? null : new String(nameString.getBytes(),"GB-2312");
        resultString[1]= versionString ;//== null ? null : new String(versionString.getBytes(),"GB-2312");
        resultString[2]= publisherString ;//== null ? null : new String(publisherString.getBytes(),"GB-2312");
        resultString[3]= uninstallPathString ;//== null ? null : new String(uninstallPathString.getBytes(),"GB-2312");
        if(resultString[0]==null) resultString=null;    //没有名字的不显示
        return resultString;
    }

    static File[] getFile(){
        File[] files = new File("C:\\ProgramData\\Microsoft\\Windows\\Start Menu\\Programs").listFiles();
        for (File file : files) {
            System.out.println(file);
        }
        return files;
    }

    /**
     * 读取注册表指定节点所有的键值对
     * @param nodePath
     * @return
     */
    public static String getRegValue(String nodePath) {
        List<String> regList = new ArrayList<>();
        try {
            //cmd命令:REG QUERY HKCR\ApplicationNameXTFW\shell\open\command /ve
            Process process = Runtime.getRuntime().exec("reg query " + nodePath);
            process.getOutputStream().close();
            InputStreamReader isr = new InputStreamReader(process.getInputStream());
            String line = null;
            BufferedReader ir = new BufferedReader(isr);
            while ((line = ir.readLine()) != null) {
                //[,
                // HKEY_CLASSES_ROOT\ApplicationNameXTFW\shell\open\command
                // ,     (Ĭ��)    REG_SZ    D:\YWXT\nw.exe
                // , ]
                regList.add(line);
            }
            process.destroy();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //regList.get(2):"     (Ĭ��)    REG_SZ    D:\YWXT\nw.exe"
        String[] s = regList.get(2).split(" ");
        /** s
         * 0:
         * 1:
         * 2:
         * 3:
         * 4:(Ĭ��)
         * 5:
         * 6:
         * 7:
         * 8:REG_SZ
         * 9:
         * 10:
         * 11:
         * 12:D:\YWXT\nw.exe
         */
        return s[12];
    }

}
class MyTable{
    private JTable jTable;
    private Object[][] data=new Object[100][4];
    @Getter
    private Object[] colNames= { "软件名称","版本号","出版商","卸载路径"};
    private int p=-1;

    public MyTable(){

    }

    public void addRow(Object[] data){
        p++;
        if(p>=100) return ;
        this.data[p]=data;
    }


    public JTable getTable(){
        jTable=new JTable(data,colNames);
        return jTable;
    }

}