package com.targetmol.account.controller;

import com.targetmol.account.service.ContactService;
import com.targetmol.common.vo.PageResult;
import com.targetmol.common.vo.ResultMsg;
import com.targetmol.domain.account.Contact;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RequestMapping("/contact")
@RestController
public class ContactController {
    @Autowired
    private ContactService contactService;

    //按ID查询联系人
    @GetMapping("{autoid}")
    public ResponseEntity<ResultMsg> findbyContactid(@PathVariable ("autoid") Integer autoid){
              return ResponseEntity.ok(ResultMsg.success(contactService.findByAutoId(autoid)));
    }

    //查询联系人
    @GetMapping
    public ResponseEntity<PageResult<Contact>> findByAll(
            @RequestParam(value="page",defaultValue = "1") Integer page,
            @RequestParam(value="pagesize",defaultValue = "30") Integer pageSize,
            @RequestParam(value="softby",required = false) String softBy,
            @RequestParam(value="desc",defaultValue = "false") Boolean desc,
            @RequestParam(value="key",required = false) String key,
            @RequestParam(value="showdel" ,defaultValue="false") Boolean showDel
    ){

        return ResponseEntity.ok(contactService.findByAll(page,pageSize,softBy,desc,key,showDel));
    }


    //新增联系人
    @PostMapping
    public ResponseEntity<ResultMsg> addContact(@RequestBody Contact contact){
        contactService.add(contact);
        return ResponseEntity.ok(ResultMsg.success());
    }
    //修改联系人
    @PutMapping("{contid}")
    public ResponseEntity<ResultMsg> updateContact(@PathVariable("contid") Integer contid,
                                                   @RequestBody Contact contact){
        contact.setContactid(contid);
        contactService.update(contact);
        return ResponseEntity.ok(ResultMsg.success());
    }
    //删除联系人
    @DeleteMapping("{contid}")
    public ResponseEntity<ResultMsg> deleteContanct(@PathVariable("contid") Integer contid){
        contactService.setActived(contid,0);
        return ResponseEntity.ok(ResultMsg.success());
    }

}
