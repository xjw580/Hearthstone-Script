package club.xiaojiawei.hsscript.bean.single.repository;

/**
 * @author 肖嘉威
 * @date 2024/5/23 19:19
 */
public class GithubRepository extends AbstractRepository{

    private GithubRepository() {}

    private static class Instance{
        private static final GithubRepository INSTANCE = new GithubRepository();

        public static GithubRepository getInstance(){
            return INSTANCE;
        }
    }

    public static AbstractRepository getInstance() {
        return Instance.getInstance();
    }

    @Override
    protected String getDomain() {
        return "github.com";
    }

    @Override
    protected String getUserName() {
        return "xjw580";
    }

}
