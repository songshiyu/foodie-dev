package com.lxk.pojo.bo;

/**
 * @author songshiyu
 * @date 2020/6/17 22:44
 *
 *   前端表单传过来的表单内容
 **/
public class UserBO {

    private String username;

    private String password;

    private String confirmPassword;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
