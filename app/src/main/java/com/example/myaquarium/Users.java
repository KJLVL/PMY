package com.example.myaquarium;

import com.orm.SugarRecord;

public class Users extends SugarRecord {
    public String login;
    public String password;
    public String user_name;
    public String surname;
    public String avatar;
    public String aquarium_volume;
    public String city;
    public String phone;

    public Users() {
    }

    public Users(
            String login,
            String password,
            String user_name,
            String surname,
            String avatar,
            String aquarium_volume,
            String city,
            String phone
    ) {
        this.login = login;
        this.password = password;
        this.user_name = user_name;
        this.surname = surname;
        this.avatar = avatar;
        this.aquarium_volume = aquarium_volume;
        this.city = city;
        this.phone = phone;
    }

    public Users(String login, String password, String user_name, String avatar) {
        this.login = login;
        this.password = password;
        this.user_name = user_name;
        this.avatar = avatar;
    }

}
