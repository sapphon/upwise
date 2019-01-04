package org.sapphon.personal.upwise.model;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.sapphon.personal.upwise.repository.jpa.WisdomJpa;

import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import java.sql.Timestamp;

@MappedSuperclass
public abstract class AbstractVote implements IVote {

    protected String addedByUsername;

    @ManyToOne(targetEntity = WisdomJpa.class, fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "wisdom_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    protected IWisdom wisdom;
    protected Timestamp timeAdded;

    public AbstractVote(){}

    public AbstractVote(IWisdom wisdom, String addedByUsername, Timestamp timeAdded) {
        this.wisdom = wisdom;
        this.addedByUsername = addedByUsername;
        this.timeAdded = timeAdded;
    }

    //region EqualsHashCodeToString
    @Override
    public String toString() {
        return "VOTE: " + addedByUsername + " voted for " + wisdom + " on date " + timeAdded;
    }

	@Override
	public int hashCode() {
		int result = 11;
		result = 31 * result + ((wisdom == null) ? 0 : wisdom.hashCode());
		result = 31 * result + ((addedByUsername == null) ? 0 : addedByUsername.hashCode());
		result = 31 * result + ((timeAdded == null) ? 0 : timeAdded.hashCode());

		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof AbstractVote)) {
			return false;
		}
		if(this == obj) return true;
		AbstractVote other = (AbstractVote) obj;
		return this.wisdom.equals(other.wisdom) &&
				this.addedByUsername.equals(other.addedByUsername) &&
				this.timeAdded.equals(other.timeAdded);
	}

	//endregion
	//region SettersGetters

    @Override
    public IWisdom getWisdom(){
        return this.wisdom;
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
    public IVote setWisdom(IWisdom wisdom){
        this.wisdom = wisdom;
        return this;
    }
        @Override
        public IVote setAddedByUsername(String addedByUsername) {
            this.addedByUsername = addedByUsername;
            return this;
        }
        @Override
        public IVote setTimeAdded(Timestamp timeAdded) {
            this.timeAdded = timeAdded;
            return this;
        }
    //endregion
}
