package club.xiaojiawei.hearthstone.enums;

import lombok.SneakyThrows;

import java.io.File;
import java.io.RandomAccessFile;

/**
 * @author 肖嘉威
 * @date 2022/11/29 22:04
 */
public enum SpecialCardEnum {

    PRINCE_RENATHAL("REV_018", "雷纳索尔王子"),
    C_THUN_THE_SHATTERED("DMF_254", "克苏恩，破碎之劫"),
    BAKU_THE_MOONEATER("GIL_826", "噬月者巴库"),
    GENN_GREYMANE("GIL_692", "吉恩·格雷迈恩"),

    ;
    private final String value;
    private final String comment;

    SpecialCardEnum(String value, String comment) {
        this.value = value;
        this.comment = comment;
    }

    public String getValue() {
        return value;
    }

    public String getComment() {
        return comment;
    }

    @SneakyThrows
    @Override
    public String toString() {
        return "SpecialCardEnum{" +
                "value='" + value + '\'' +
                ", comment='" + comment + '\'' +
                '}';
    }
}
