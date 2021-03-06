package com.targetmol.sales.service.Account;

import com.targetmol.common.emums.ExceptionEumn;
import com.targetmol.common.exception.ErpExcetpion;
import com.targetmol.domain.sales.Account.AreaGroup;
import com.targetmol.sales.dao.Account.AreaGroupDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.util.StringUtil;

import java.util.List;

@Service
@Transactional(rollbackFor = {Exception.class, ErpExcetpion.class})
public class AreaGroupService {
    @Autowired
    private AreaGroupDao accountGroupDao;


    //查找所有
    public List<AreaGroup> findAll() {
        return accountGroupDao.selectAll();
    }

    //根据ID查找
    public AreaGroup findById(Integer id) {
        return accountGroupDao.selectByPrimaryKey(id);
    }

    //添加
    public void addnew(AreaGroup areaGroup) {
        if(StringUtil.isEmpty(areaGroup.getGroupname())){
            throw new ErpExcetpion(ExceptionEumn.OBJECT_VALUE_ERROR);
        }

        areaGroup.setCode( getNewCode(areaGroup.getPcode()));

        if(accountGroupDao.insert(areaGroup)!=1){
            throw new ErpExcetpion(ExceptionEumn.FAIIL_TO_SAVE);
        }

    }
    //根据父ID获取新的code
    private String getNewCode(String pcode){
        if(pcode==null){
            pcode="AG-";
        }
        Integer maxcode=accountGroupDao.getMaxCode(pcode);
        return  pcode+(String.format("%02d",(maxcode==null?(Integer)0:maxcode)+1));
    }

    //修改
    public void update(AreaGroup areaGroup) {
        if(StringUtil.isEmpty(areaGroup.getGroupname())){
            throw new ErpExcetpion(ExceptionEumn.OBJECT_VALUE_ERROR);
        }
        AreaGroup oldaccountGroup=accountGroupDao.selectByPrimaryKey(areaGroup.getId());
        if(oldaccountGroup==null){
            throw new ErpExcetpion(ExceptionEumn.ACCOUNT_GROUP_IS_NOT_FOUND);
        }
        //判断code是否变更
        String code= areaGroup.getCode();
        if(code!=oldaccountGroup.getCode()){
            //判断code中的父code是否存在
            if(code.length()>5){
                if(accountGroupDao.getBycode(code.substring(0,code.length()-2))==null){
                    throw new ErpExcetpion(ExceptionEumn.ACCOUNT_GROUP_PCODE_IS_NOT_FOUND);
                }
            }
            //判断code是否存在
            if(accountGroupDao.getBycode(code)!=null){
                throw  new ErpExcetpion(ExceptionEumn.ACCOUNT_GROUP_IS_EXist);
            }
        }
        //保存
        if(accountGroupDao.updateByPrimaryKey(areaGroup)!=1){
            throw new ErpExcetpion(ExceptionEumn.FAIIL_TO_SAVE);
        }

    }


    //删除
    public void delete(Integer id) {
        if(accountGroupDao.selectByPrimaryKey(id)==null){
            throw new ErpExcetpion(ExceptionEumn.ACCOUNT_GROUP_IS_NOT_FOUND);
        }
        if(accountGroupDao.deleteByPrimaryKey(id)!=1){
            throw new ErpExcetpion(ExceptionEumn.FAIIL_TO_DELETE);
        }
    }


}
