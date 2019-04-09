package org.sapphon.personal.upwise.model.datatransfer;

import org.sapphon.personal.upwise.factory.DomainObjectFactory;
import org.sapphon.personal.upwise.model.IUser;

public class UserRegistration extends AbstractIncomingDataTransfer<IUser> {

    private String desiredUsername;
    private String displayName;
    private String password;
    private String confirmPassword;


    private String email;

    public UserRegistration(){}

    private boolean validateIncomingRegistration(){
        return this.password != null &&
                this.password.equals(this.confirmPassword) &&
                this.password.length() > 0 &&
                this.desiredUsername != null &&
                this.desiredUsername.length() > 0;
    }

    @Override
    public IUser convertToModelObject() {
        if(validateIncomingRegistration()) {
            return DomainObjectFactory.createUserWithCreatedTimeNow(desiredUsername, displayName, password, email);
        }
        return null;
    }

    //region SettersGetters
    public String getDesiredUsername() {
        return desiredUsername;
    }

    public UserRegistration setDesiredUsername(String desiredUsername) {
        this.desiredUsername = desiredUsername;
        return this;
    }

    public String getDisplayName() {
        return displayName;
    }

    public UserRegistration setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public UserRegistration setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public UserRegistration setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public UserRegistration setEmail(String email) {
        this.email = email;
        return this;
    }
    //endregion
}
