package org.sapphon.personal.upwise.model;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.sql.Timestamp;

@MappedSuperclass
public abstract class AbstractAnalyticsEvent implements IAnalyticsEvent {
    @Column(length=1024)
    private String eventDescription;
    private String eventInitiator;
    private Timestamp eventTime;
    private AnalyticsAction eventType;

    public AbstractAnalyticsEvent() {
    }

    public AbstractAnalyticsEvent(String eventDescription, String eventInitiator, Timestamp timeOccurred, AnalyticsAction eventType) {
        this.eventDescription = eventDescription;
        this.eventInitiator = eventInitiator;
        this.eventTime = timeOccurred;
        this.eventType = eventType;
    }

    //region ToStringEqualsHashCode
    @Override
    public String toString() {
        return "EVENT: " + eventType + "with description '" + eventDescription + "' happened at " + eventTime + " done by " + eventInitiator + ".";
    }

    @Override
    public int hashCode() {
        int result = 11;
        result = 31 * result + ((eventDescription == null) ? 0 : eventDescription.hashCode());
        result = 31 * result + ((eventInitiator == null) ? 0 : eventInitiator.hashCode());
        result = 31 * result + ((eventTime == null) ? 0 : eventTime.hashCode());
        result = 31 * result + ((eventType == null) ? 0 : eventType.hashCode());

        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof AbstractAnalyticsEvent)) {
            return false;
        }
        if (this == obj) return true;
        AbstractAnalyticsEvent other = (AbstractAnalyticsEvent) obj;
        return this.eventDescription == null ? other.eventDescription == null : this.eventDescription.equals(other.eventDescription) &&
                this.eventInitiator == null ? other.eventInitiator == null : this.eventInitiator.equals(other.eventInitiator) &&
                this.eventTime == null ? other.eventTime == null : this.eventTime.equals(other.eventTime) &&
                this.eventType == null ? other.eventType == null : this.eventType.equals(other.eventType);
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

    @Override
    public AnalyticsAction getEventType() {
        return eventType;
    }

    public void setEventDescription(String newDescription) {
        this.eventDescription = newDescription;
    }

    public void setEventInitiator(String newInitiator) {
        this.eventInitiator = newInitiator;
    }

    public void setEventType(AnalyticsAction newEventType) { this.eventType = newEventType; }

    @Override
    public void setEventTime(Timestamp newTime) {
        this.eventTime = newTime;
    }
    //endregion
}
