package com.idibros.study.service;

import com.idibros.study.dto.User;

import java.sql.SQLException;

/**
 * Created by dongba on 2017-10-24.
 */
public interface UserService {

    /**
     * 트랜젝션이 적용된 UserService와 적용되지 않은 것을 별도로 구현하기 위해 인터페이스를 정의했다.
     */

    void add(User user) throws SQLException, ClassNotFoundException;

    void upgradeLevels();

}
