package com.idibros.study.service;

import com.idibros.study.dto.User;

/**
 * Created by dongba on 2017-10-18.
 */
public class TestUserService extends UserService {

    /**
     * 트랜젝션 테스트를 위해서 특정 유저의 레벨을 업그레이드 하는 중간에 예외가 발생하는 상황을 고의로 만들 것이다.
     * TestUserService는 UserService를 상속한 클래스로, 특정 유저 정보를 보관하고 그 유저일 경우 예외를 발생시킨다.
     * 예를 들어 5명의 유저 중에 2번 째 유저의 업그레이드 작업 도중 예외가 발생하는 경우가 있을 것이다.
     */
    private String id;

    public TestUserService(String id) {
        this.id = id;
    }

    protected void upgradeLevel (User user) {
        /**
         * 테스트를 위해 특정 유저일 경우 예외를 발생시켜서 트랜젝션을 종료한다.
         */
        if (user.getId().equals(this.id)) {
            throw new TestUserServiceException();
        }
        super.upgradeLevel(user);
    }

    public class TestUserServiceException extends RuntimeException {
    }

}
