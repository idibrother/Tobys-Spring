package com.idibros.study.service.impl;

import com.idibros.study.dto.User;
import com.idibros.study.service.UserService;
import lombok.Setter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.sql.SQLException;

/**
 * Created by dongba on 2017-10-24.
 */
public class UserServiceTx implements UserService {

    @Setter
    private UserService userService;

    @Setter
    private PlatformTransactionManager transactionManager;

    @Override
    public void add(User user) throws SQLException, ClassNotFoundException {
        userService.add(user);
    }

    @Override
    public void upgradeLevels() {
        TransactionStatus status = this.transactionManager.getTransaction(new DefaultTransactionDefinition());
        try {
            userService.upgradeLevels();
            this.transactionManager.commit(status);
        } catch (RuntimeException e) {
            this.transactionManager.rollback(status);
//            throw e;
        }
    }

}
