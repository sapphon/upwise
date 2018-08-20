package org.sapphon.personal.upwise;

import javax.persistence.MappedSuperclass;
import java.sql.Timestamp;

@MappedSuperclass
public abstract class AbstractWisdom implements IWisdom {

    protected String wisdomContent;
    protected String attribution;
    protected String addedByUsername;
    protected Timestamp timeAdded;

    public AbstractWisdom(){}

    public AbstractWisdom(String wisdomContent, String attribution, String addedByUsername, Timestamp timeAdded) {
        this.wisdomContent = wisdomContent;
        this.attribution = attribution;
        this.addedByUsername = addedByUsername;
        this.timeAdded = timeAdded;
    }

    @Override
    public String toString(){
        return "WISDOM: '" + wisdomContent + "', said " + attribution + " at " + timeAdded + " according to " + addedByUsername;
    }

	@Override
	public int hashCode() {
		int result = 11;
		result = 31 * result + ((wisdomContent == null) ? 0 : wisdomContent.hashCode());
		result = 31 * result + ((attribution == null) ? 0 : attribution.hashCode());
		result = 31 * result + ((addedByUsername == null) ? 0 : addedByUsername.hashCode());
		result = 31 * result + ((timeAdded == null) ? 0 : timeAdded.hashCode());

		return result;
	}

	@Override
	public boolean equals(Object obj) {
        if(obj == null){
            return false;
        }
		if (!(obj instanceof AbstractWisdom)) {
			return false;
		}
		if(this == obj) return true;
		AbstractWisdom other = (AbstractWisdom) obj;
		return this.wisdomContent == null ? other.wisdomContent == null : this.wisdomContent.equals(other.wisdomContent) &&
                this.attribution == null ? other.attribution == null : this.attribution.equals(other.attribution) &&
                this.addedByUsername == null ? other.addedByUsername == null : this.addedByUsername.equals(other.addedByUsername) &&
                this.timeAdded == null ? other.timeAdded == null : this.timeAdded.equals(other.timeAdded);
	}

	//region SettersGetters
    @Override
    public String getWisdomContent(){
        return this.wisdomContent;
    }
    @Override
    public String getAttribution(){
        return attribution;
    }
    @Override
    public Timestamp getTimeAdded(){
        return this.timeAdded;
    }
    @Override
    public String getAddedByUsername(){
        return this.addedByUsername;
    }
    @Override
    public void setAddedByUsername(String addedByUsername) {
        this.addedByUsername = addedByUsername;
    }
    @Override
    public void setAttribution(String attribution) {
        this.attribution = attribution;
    }
    @Override
    public void setTimeAdded(Timestamp timeAdded) {
        this.timeAdded = timeAdded;
    }
    @Override
    public void setWisdomContent(String wisdomContent) {
        this.wisdomContent = wisdomContent;
    }

    //endregion
}
