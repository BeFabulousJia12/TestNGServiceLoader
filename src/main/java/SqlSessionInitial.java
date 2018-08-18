
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.Reader;

/**
 * @Author You Jia
 * @Date 7/24/2018 9:19 AM
 */
public class SqlSessionInitial {

    public SqlSession sqlSession;

    public SqlSession getSqlSession(String configFile) throws IOException {
        Reader reader = Resources.getResourceAsReader(configFile);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
        sqlSession = sqlSessionFactory.openSession();
        return sqlSession;
    }
}
