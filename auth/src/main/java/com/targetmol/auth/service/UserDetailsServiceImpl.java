package com.targetmol.auth.service;

import com.targetmol.common.client.UserFeignClent;
import com.targetmol.common.emums.ExceptionEumn;
import com.targetmol.common.exception.ErpExcetpion;
import com.targetmol.common.vo.ResultMsg;
import com.targetmol.domain.system.ext.AuthUserExt;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.util.StringUtil;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    ClientDetailsService clientDetailsService;
    @Autowired
    UserFeignClent userFeignClent;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //取出身份，如果身份为空说明没有认证
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //没有认证统一采用httpbasic认证，httpbasic中存储了client_id和client_secret，开始认证client_id和client_secret
        if(authentication==null){
            ClientDetails clientDetails = clientDetailsService.loadClientByClientId(username);
            if(clientDetails!=null){
                //密码
                String clientSecret = clientDetails.getClientSecret();
                return new User(username,clientSecret,AuthorityUtils.commaSeparatedStringToAuthorityList(""));
            }
        }
        if (StringUtils.isEmpty(username)) {
            return null;
        }
        //通过用户管理模块调用获取用户信息
        ResponseEntity rs=userFeignClent.login(username);

        if(rs==null){
            throw new ErpExcetpion(ExceptionEumn.LOGIN_USERNAME_IS_FAIL);
        }
        Integer code=(Integer) ((ResultMsg) rs.getBody()).getCode();
        if(code==402) {
           return null;
        }

        LinkedHashMap<String, Object> result=(LinkedHashMap<String,Object>)((ResultMsg) rs.getBody()).getData();
        AuthUserExt usertext = new AuthUserExt();
        usertext.setUsername(result.get("username").toString());
        usertext.setName(result.get("name").toString());
        usertext.setPassword(result.get("dcode").toString());
        usertext.setUid((Integer)result.get("uid"));
        usertext.setUserpic((String)result.get("userpic"));
        if(usertext == null){
            return null;
        }
        //取出正确密码（hash值）
        String password = usertext.getPassword();

        List<LinkedHashMap> permissions=(List<LinkedHashMap>) result.get("permissions");
        if(permissions==null){
            permissions=new ArrayList<>();
        }
        List<String> user_permission = new ArrayList<>();
        for ( LinkedHashMap<String,Object> item:permissions){
            String codex=(String)item.get("code");
            if(StringUtil.isEmpty(codex)==false){
                user_permission.add(codex);
            }
        }
//        user_permission.add("company_list_all");
//        user_permission.add("user_list_sales");
        String user_permission_string  = StringUtils.join(user_permission.toArray(), ",");
        UserJwt userDetails = new UserJwt(username,password,AuthorityUtils.commaSeparatedStringToAuthorityList(user_permission_string));
        if(usertext.getUid()!=null){
            userDetails.setUid((usertext.getUid()));
        }
        //userDetails.setCompanyId(userext.getCompanyId());//所属企业3
        userDetails.setName(usertext.getName());//用户名称
        userDetails.setUserpic(usertext.getUserpic());//用户头像
        userDetails.setUid(usertext.getUid());



       /* UserDetails userDetails = new org.springframework.security.core.userdetails.User(username,
                password,
                AuthorityUtils.commaSeparatedStringToAuthorityList(""));*/
//       AuthorityUtils.createAuthorityList("course_get_baseinfo","course_get_list"));
        return userDetails;
    }
}
