package com.idibros.study.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by dongba on 2017-08-21.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private String id;

    private String name;

    private String password;

    private Level level;

    private int loginCount;

    private int recommendCount;

    public void upgradeLevel() {
        Level nextLevel = this.level.nextLevel();
        if (nextLevel == null) {
            throw new IllegalStateException(this.level + "은 업그레이드가 불가능합니다.");
        }
        this.level = nextLevel;
    }
}
