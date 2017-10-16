package com.idibros.study.service;

import com.idibros.study.dao.UserDao;
import com.idibros.study.dto.Level;
import com.idibros.study.dto.User;

import java.util.List;

/**
 * Created by dongba on 2017-10-16.
 */
public class UserService {

    private UserDao userDao;

    public void setUserDao (UserDao userDao) {
        this.userDao = userDao;
    }

    public void upgradeLevels() {
        List<User> allUsers = userDao.getAll();

        for(User user : allUsers) {
            Boolean changed = null;
            if (user.getLevel() == Level.BASIC && user.getLoginCount() >= 30) {
                user.setLevel(Level.SILVER);
                changed = true;
            } else if (user.getLevel() == Level.SILVER && user.getRecommendCount() >= 30) {
                user.setLevel(Level.GOLD);
                changed = true;
            } else if (user.getLevel() == Level.GOLD) {
                changed = false;
            } else {
                changed = false;
            }

            if (changed) {
                userDao.update(user);
            }
        }
    }
}
