package club.xiaojiawei.hsscript.bean.single.repository;

import club.xiaojiawei.hsscript.bean.Release;
import club.xiaojiawei.hsscript.consts.ScriptStaticData;

/**
 * @author 肖嘉威
 * @date 2024/5/23 19:18
 */
public abstract class AbstractRepository {

    public String getReleaseURL(Release release){
        return String.format("https://%s/%s/%s/releases/download/%s/%s_%s.zip", getDomain(), getUserName(), ScriptStaticData.PROJECT_NAME, release.getTagName(), ScriptStaticData.SCRIPT_NAME, release.getTagName());
    }

    abstract protected String getDomain();

    abstract protected String getUserName();

}
