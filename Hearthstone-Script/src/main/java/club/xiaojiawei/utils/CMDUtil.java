package club.xiaojiawei.utils;

import club.xiaojiawei.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author 肖嘉威
 * @date 2024/9/7 23:49
 */
public class CMDUtil {

    @NotNull
    public static String exec(@NotNull String[] command) throws IOException {
        StringBuilder sb = new StringBuilder();
        Process process = Runtime.getRuntime().exec(command);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        return sb.toString();
    }

}
