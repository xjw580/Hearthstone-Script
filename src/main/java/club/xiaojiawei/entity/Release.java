package club.xiaojiawei.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author 肖嘉威
 * @date 2023/9/16 20:05
 * @msg
 */
@Data
public class Release {
    @JsonProperty("tag_name")
    private String tagName;
    @JsonProperty("prerelease")
    private boolean preRelease;
}
