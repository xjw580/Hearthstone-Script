package club.xiaojiawei.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author 肖嘉威
 * @date 2023/9/16 20:05
 */
@Data
@Slf4j
public class Release implements Comparable<Release>{

    @JsonProperty("tag_name")
    private String tagName;
    @JsonProperty("prerelease")
    private boolean preRelease;
    @JsonProperty("name")
    private String name;
    @JsonProperty("body")
    private String body;

    /**
     * 只比较纯数字，例：匹配v3.2.3.3-DEV中的3.2.3.3
     * @param release the object to be compared.
     * @return
     */
    @Override
    public int compareTo(@NonNull Release release) {
        if (release.getTagName() == null || release.getTagName().isBlank()){
            return Integer.MAX_VALUE;
        }
        String version1 = this.getTagName(), version2 = release.getTagName(), regex = "\\d+(\\.\\d+)*";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher1 = pattern.matcher(version1), matcher2 = pattern.matcher(version2);
        boolean isFind1 = matcher1.find(), isFind2 = matcher2.find();
        if (!isFind1 || !isFind2){
            log.warn(String.format("版本号有误，version1：%s，version2：%s", version1, version2));
            return Integer.MAX_VALUE;
        }
        String[] v1 = matcher1.group().split("\\."), v2 = matcher2.group().split("\\.");
        int minLength = Math.min(v1.length, v2.length);
        int result = 0;
        for (int i = 0; i < minLength; i++) {
            String s1 = v1[i], s2 = v2[i];
            result = Integer.compare(Integer.parseInt(s1), Integer.parseInt(s2));
            if (result != 0){
                return result;
            }
        }
        if (v1.length == v2.length){
            String[] split1 = version1.split("-");
            String[] split2 = version2.split("-");
            if (split1.length > 1 && split2.length > 1){
                int type1 = switch (split1[1].toLowerCase(Locale.ROOT)) {
                    case "ga" -> 5;
                    case "dev" -> 4;
                    case "beta" -> 3;
                    default -> 0;
                };
                int type2 = switch (split2[1].toLowerCase(Locale.ROOT)) {
                    case "ga" -> 5;
                    case "dev" -> 4;
                    case "beta" -> 3;
                    default -> 0;
                };
                return type1 - type2;
            }else {
                return split1.length - split2.length;
            }
        }
        return Integer.compare(v1.length, v2.length);
    }

    @Override
    public String toString() {
        return "Release{" +
                "tagName='" + tagName + '\'' +
                ", preRelease=" + preRelease +
                ", name='" + name + '\'' +
                ", body='" + (body == null? null : ("\n" + body + "\n")) + '\'' +
                '}';
    }
}
