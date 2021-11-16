package com.example.survirun.data;

import java.util.List;

public class rankingData {


    public  List<Integer> scores;

    public List<UsersEntity> users;

    public List<Integer> getScores() {
        return scores;
    }

    public void setScores(List<Integer> scores) {
        this.scores = scores;
    }

    public List<UsersEntity> getUsers() {
        return users;
    }

    public void setUsers(List<UsersEntity> users) {
        this.users = users;
    }
}
