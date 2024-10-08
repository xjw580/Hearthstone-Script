package club.xiaojiawei.hsscript.bean.single.repository;

/**
 * @author 肖嘉威
 * @date 2024/5/23 19:20
 */
public class GiteeRepository extends AbstractRepository{

    private GiteeRepository() {}

    private static class Instance{
        private static final GiteeRepository INSTANCE = new GiteeRepository();

        public static GiteeRepository getInstance(){
            return INSTANCE;
        }
    }

    public static AbstractRepository getInstance() {
        return Instance.getInstance();
    }

    @Override
    protected String getDomain() {
        return "gitee.com";
    }

    @Override
    protected String getUserName() {
        return "zergqueen";
    }

}
