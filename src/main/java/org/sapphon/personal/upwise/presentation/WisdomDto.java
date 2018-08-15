package org.sapphon.personal.upwise.presentation;

import org.sapphon.personal.upwise.IWisdom;
import org.sapphon.personal.upwise.factory.DomainObjectFactory;
import org.sapphon.personal.upwise.time.TimeLord;

public class WisdomDto implements DTO<IWisdom> {
    private String wisdomContent;
    private String attribution;
    private String submitterUsername;

    public WisdomDto(){
        this("", "", "");
    }

    public WisdomDto(String content, String attribution, String submitterUsername){
        this.wisdomContent = content;
        this.attribution = attribution;
        this.submitterUsername = submitterUsername;
    }

    public WisdomDto(String content, String attribution){
        this(content, attribution, "WISDOMDTO_DEFAULT_USER");
    }

    @Override
    public IWisdom getModelObject() {
        return DomainObjectFactory.createWisdom(getWisdomContent(), getAttribution(), getSubmitterUsername(), TimeLord.getNow());
    }

    public String getWisdomContent() {
        return wisdomContent;
    }

    public String getAttribution() {
        return attribution;
    }

    public String getSubmitterUsername() {
        return submitterUsername;
    }
    public void setWisdomContent(String wisdomContent) {
        this.wisdomContent = wisdomContent;
    }

    public void setAttribution(String attribution) {
        this.attribution = attribution;
    }

    public void setSubmitterUsername(String submitterUsername) {
        this.submitterUsername = submitterUsername;
    }
}
