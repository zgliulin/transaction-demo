package com.fpx.demo.bank2.service.impl;

import com.fpx.demo.bank2.dao.AccountInfoDao;
import com.fpx.demo.bank2.feign.Bank2Client;
import com.fpx.demo.bank2.service.AccountInfoService;
import io.seata.core.context.RootContext;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
@Slf4j
public class AccountInfoServiceImpl implements AccountInfoService {

    @Autowired
    AccountInfoDao accountInfoDao;

    @Resource
    Bank2Client bank2Client;

    @Transactional
    @GlobalTransactional//开启全局事务
    @Override
    public void updateAccountBalance(String accountNo, Double amount) {
        log.info("bank1 service begin,XID：{}", RootContext.getXID());
        //扣减张三的金额
        accountInfoDao.updateAccountBalance(accountNo, amount * -1);
        //调用李四微服务，转账
        String transfer = bank2Client.transfer(amount);
        if ("fallback".equals(transfer)) {
            //调用李四微服务异常
            throw new RuntimeException("调用李四微服务异常");
        }
        if (amount == 2) {
            //人为制造异常
            throw new RuntimeException("bank1 make exception..");
        }
    }
}
