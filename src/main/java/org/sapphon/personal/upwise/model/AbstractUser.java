package org.sapphon.personal.upwise.model;

import javax.persistence.MappedSuperclass;
import java.sql.Timestamp;

@MappedSuperclass
public abstract class AbstractUser implements IUser {

    public String loginUsername;
    public String displayUsername;
    public Timestamp timeAdded;
    public String password;

    public AbstractUser(){}

    public AbstractUser(String loginUsername, String displayUsername, Timestamp timeAdded, String password) {
        this.loginUsername = loginUsername;
        this.displayUsername = displayUsername;
        this.timeAdded = timeAdded;
        this.password = password;
    }

    @Override
    public String toString(){
        return "USER: '"+loginUsername+"' appearing as '" + displayUsername + "'";
    }

	@Override
	public int hashCode() {
		int result = 11;
		result = 31 * result + ((loginUsername == null) ? 0 : loginUsername.hashCode());
		result = 31 * result + ((displayUsername == null) ? 0 : displayUsername.hashCode());
		result = 31 * result + ((timeAdded == null) ? 0 : timeAdded.hashCode());

		return result;
	}

	@Override
	public boolean equals(Object obj) {
        if(obj == null){
            return false;
        }
		if (!(obj instanceof AbstractUser)) {
			return false;
		}
		if(this == obj) return true;
		AbstractUser other = (AbstractUser) obj;
		return this.loginUsername == null ? other.loginUsername == null : this.loginUsername.equals(other.loginUsername) &&
                this.displayUsername == null ? other.displayUsername == null : this.displayUsername.equals(other.displayUsername) &&
                this.timeAdded == null ? other.timeAdded == null : this.timeAdded.equals(other.timeAdded);
	}

	//region SettersGetters
    @Override
    public Timestamp getTimeAdded(){
        return this.timeAdded;
    }
    @Override
    public String getLoginUsername(){
        return this.loginUsername;
    }
    @Override
    public void setLoginUsername(String addedByUsername) {
        this.loginUsername = addedByUsername;
    }
    @Override
    public String getDisplayName(){
        return this.displayUsername;
    }
    @Override
    public void setDisplayName(String addedByUsername) {
        this.displayUsername = addedByUsername;
    }
    @Override
    public void setTimeAdded(Timestamp timeAdded) {
        this.timeAdded = timeAdded;
    }
    @Override
    public String getPassword() {
        return this.password;
    }
    @Override
    public void setPassword(String password) {
        this.password = password;
    }

    //endregion
}
