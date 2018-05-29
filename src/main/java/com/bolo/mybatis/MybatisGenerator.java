package com.bolo.mybatis;


import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.exception.InvalidConfigurationException;
import org.mybatis.generator.exception.XMLParserException;
import org.mybatis.generator.internal.DefaultShellCallback;
import org.mybatis.spring.support.SqlSessionDaoSupport;
//import sun.rmi.rmic.Generator; //这个是jdk内部的工具类，打包时会引起错误


import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/*
 * mapper自动生成类
 */
public class MybatisGenerator{


    /**
     * mapper生成器执行，扫描数据库中存在的表，依次对表生成对应mapper
     * @throws IOException
     * @throws XMLParserException
     * @throws InvalidConfigurationException
     * @throws SQLException
     * @throws InterruptedException
     */
    public static void generator() throws IOException, XMLParserException, InvalidConfigurationException, SQLException, InterruptedException {
//        List<String> warming = new ArrayList<>();
//        boolean overwrite = true;
//        ConfigurationParser cp = new ConfigurationParser(warming);
//        Configuration config = cp.parseConfiguration(Generator.class.getResourceAsStream("/generator/generatorConfig.xml"));
//        DefaultShellCallback callback = new DefaultShellCallback(overwrite);
//        MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config,callback,warming);
//        myBatisGenerator.generate(null);
    }

    public static void main(String[] args) throws IOException, XMLParserException, InvalidConfigurationException, SQLException, InterruptedException {
//        MapperHelper mapperHelper = new MapperHelper();
//        Config config = new Config();
//        mapperHelper.setConfig(config);
//        mapperHelper.registerMapper(Mapper.class);
//        //mapperHelper.processConfiguration();
//        MybatisTest mybatisTest = new MybatisTest();
//        SqlSession sqlSession = mybatisTest.getSqlSession();
//        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
//        User user = new User();
//        user.setId("123");
//        user.setPassword("123456");
//        user.setPermission("0");
//        userMapper.insert(user);
//        userMapper.deleteByPrimaryKey(user);


    }
}
