package com.targetmol.users.dao;

import com.targetmol.common.mapper.BaseMapper;
import com.targetmol.domain.Contact;
import com.targetmol.domain.User;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;


public interface UserDao  extends BaseMapper<User> {

    //@Select("select * from [user] where uid<>#{uid}  and  username=#{username}")
    @SelectProvider(type=userDaoProvider.class,method = "findUserbyUsername")
    List<User> findUserbyUsername(@Param("uid") Integer uid, @Param("username") String username);


    class userDaoProvider{
        public String findUserbyUsername(Integer uid,String username){
            String sqlstr="select * from [user] ";
            if(uid!=null){
                sqlstr +="where (uid<>#{uid}) and username=#{username}";
            }else
            {
                sqlstr +="where username=#{username}";
            }

            return sqlstr;
        }

    }
}
