package org.sapphon.personal.upwise.model;

import javax.persistence.MappedSuperclass;
import java.sql.Timestamp;

@MappedSuperclass
public abstract class AbstractAnalyticsEvent implements IAnalyticsEvent {
    private String eventDescription;
    private String eventInitiator;
    private Timestamp eventTime;

    public AbstractAnalyticsEvent(){
    }

    public AbstractAnalyticsEvent(String eventDescription, String eventInitiator, Timestamp timeOccurred){
        this.eventDescription = eventDescription;
        this.eventInitiator = eventInitiator;
        this.eventTime = timeOccurred;
    }

    //region ToStringEqualsHashCode
    @Override
    public String toString(){
        return "EVENT: '" + eventDescription + "' happened at " + eventTime + " done by " + eventInitiator + ".";
    }

    @Override
    public int hashCode() {
        int result = 11;
        result = 31 * result + ((eventDescription == null) ? 0 : eventDescription.hashCode());
        result = 31 * result + ((eventInitiator == null) ? 0 : eventInitiator.hashCode());
        result = 31 * result + ((eventTime == null) ? 0 : eventTime.hashCode());

        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null){
            return false;
        }
        if (!(obj instanceof AbstractAnalyticsEvent)) {
            return false;
        }
        if(this == obj) return true;
        AbstractAnalyticsEvent other = (AbstractAnalyticsEvent) obj;
        return this.eventDescription == null ? other.eventDescription == null : this.eventDescription.equals(other.eventDescription) &&
                this.eventInitiator == null ? other.eventInitiator == null : this.eventInitiator.equals(other.eventInitiator) &&
                this.eventTime == null ? other.eventTime == null : this.eventTime.equals(other.eventTime);
    }
    //endregion

    //region SettersGetters

    @Override
    public String getEventDescription() {
        return eventDescription;
    }

    @Override
    public String getEventInitiator() {
        return eventInitiator;
    }

    @Override
    public Timestamp getEventTime() {
        return eventTime;
    }

    public void setEventDescription(String newDescription){
        this.eventDescription = newDescription;
    }

    public void setEventInitiator(String newInitiator){
        this.eventInitiator = newInitiator;
    }

    @Override
    public void setEventTime(Timestamp newTime){
        this.eventTime = newTime;
    }
    //endregion
}
