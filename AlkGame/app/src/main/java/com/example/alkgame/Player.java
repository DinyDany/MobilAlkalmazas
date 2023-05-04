package com.example.alkgame;


public class Player {
    private String userName;

    private String email;
    private int score;

    public Player(){}

    public Player(String userName, String email, int score){
        this.userName=userName;
        this.email=email;
        this.score=score;
    }



    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail( String email) {
        this.email = email;
    }
}
