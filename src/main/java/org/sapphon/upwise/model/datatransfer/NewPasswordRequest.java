package org.sapphon.upwise.model.datatransfer;

public class NewPasswordRequest {
    private String loginUsername;
    private String desiredNewPassword;
    private String confirmNewPassword;

    public String getLoginUsername() {
        return loginUsername;
    }

    public void setLoginUsername(String loginUsername) {
        this.loginUsername = loginUsername;
    }

    public String getDesiredNewPassword() {
        return desiredNewPassword;
    }

    public void setDesiredNewPassword(String desiredNewPassword) {
        this.desiredNewPassword = desiredNewPassword;
    }

    public String getConfirmNewPassword() {
        return confirmNewPassword;
    }

    public void setConfirmNewPassword(String confirmNewPassword) {
        this.confirmNewPassword = confirmNewPassword;
    }

}
