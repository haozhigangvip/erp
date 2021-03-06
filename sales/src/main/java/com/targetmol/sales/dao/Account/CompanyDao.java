package com.targetmol.sales.dao.Account;

import com.targetmol.common.mapper.BaseMapper;
import com.targetmol.domain.sales.Account.Company;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.mapping.FetchType;

import java.util.List;



public interface CompanyDao extends BaseMapper<Company> {

    @Select("select * from company_info where autoid<>#{companyid} and companyname=#{cname}")
    Company findRepeatCompanyName(Integer companyid, String cname);
//    @Select("select * from company_info where comid=#{comid}")
//    Company getCompanyBycomId(String comid);


    @Select("select * from company_info")
    @Results({
            @Result(id=true,column = "companyid",property = "companyid"),
            @Result(property = "contacts",column = "companyid",many = @Many(select = "com.targetmol.sales.dao.Account.ContactDao.searchByCompanyId",fetchType = FetchType.LAZY))
    })
    List<Company> getCompanyAll();

    @Select("select * from company_info where companyid in (select companyid from contact_company where contactid=#{contactid}) order by creatime desc")
    List<Company> searchByContactId(Integer contactid);


    @Select("select * from company_info as a where companyid  in (select companyid from contact_company where contactid=#{contactid} and def=1) order by creatime desc")
    Company searchByContactIdDef(Integer contactid);

}
