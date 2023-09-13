package club.xiaojiawei.test;

/**
 * @author 肖嘉威
 * @date 2023/9/13 13:28
 * @msg
 */

/**
 * @author  <a href="mailto:foohsinglong@gmail.com">kevin.long</a>
 * @description Java获取系统软件安装列表,代码核心来自网上,主要通过Runtime实现,
 *  用JNI也行,解决乱码问题
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Arrays;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextPane;

public class SystemSoftware {
    private JFrame f = new JFrame("本系统已经安装的软件列表");
    private JTextPane textPane = new JTextPane();
    private MyTable myTable=new MyTable();
    public static Charset charset = Charset.forName("GBK");
    public SystemSoftware() {
        f.setLocation(300, 200);
        f.setSize(800,500);
        JScrollPane jScrollPane = new JScrollPane(myTable.getTable());
        f.add(jScrollPane);
        f.setVisible(true);
        f.setDefaultCloseOperation(f.EXIT_ON_CLOSE);

        try {
            check();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void check() throws Exception {
        textPane.setText("您已经安装的软件：");
        Runtime runtime = Runtime.getRuntime();
        Process process;
        process = runtime
                .exec("cmd /c reg query HKEY_LOCAL_MACHINE\\SOFTWARE\\Wow6432Node\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\");
        BufferedReader in = new BufferedReader(new InputStreamReader(process
                .getInputStream(),"GBK"));

        String string;
        while ((string = in.readLine()) != null) {
            String[] message = queryValue(string);
            if(message!=null) {
                if (message[3] != null && ((String)message[3]).contains("Blizzard")){
                    System.out.println(string);
                    System.out.println(Arrays.toString(message));
                }
                myTable.addRow(message);
            }
            f.repaint();
        }
        in.close();
        process.destroy();

    }
    static int i;

    //具体查询每一个软件的详细信息
    private String[] queryValue(String string) throws IOException {
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

        String installLocation;
        process = runtime.exec("cmd /c reg query " + string + " /v InstallLocation");
        br = new BufferedReader(new InputStreamReader(process
                .getInputStream(),"GBK"));
        br.readLine();br.readLine();//去掉前两行无用信息
        if((installLocation=br.readLine())!=null){
            System.out.println(installLocation.replaceAll("InstallLocation    REG_SZ    ", ""));;    //去掉无用信息
        }

        String[] resultString=new String[5];
        resultString[1]= nameString ;//== null ? null : new String(nameString.getBytes(),"GB-2312");
        resultString[2]= versionString ;//== null ? null : new String(versionString.getBytes(),"GB-2312");
        resultString[3]= publisherString ;//== null ? null : new String(publisherString.getBytes(),"GB-2312");
        resultString[4]= uninstallPathString ;//== null ? null : new String(uninstallPathString.getBytes(),"GB-2312");
        if(resultString[1]==null) resultString=null;    //没有名字的不显示
        return resultString;
    }

    //列表
    private class MyTable{
        private JTable jTable;
        private Object[][] data=new Object[200][5];
        private Object[] colNames= { "序号", "软件名称","版本号","出版商","卸载路径"};
        private int p=-1;

        public MyTable(){

        }

        public void addRow(Object[] data){
            data[0] = String.valueOf(++i);
            p++;
            if(p>= this.data.length) return ;
            this.data[p]=data;
        }


        public JTable getTable(){
            jTable=new JTable(data,colNames);
            return jTable;
        }

    }

    public static void main(String[] args) {
        new SystemSoftware();
    }
}
