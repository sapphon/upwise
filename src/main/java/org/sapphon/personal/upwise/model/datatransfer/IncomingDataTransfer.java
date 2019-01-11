package org.sapphon.personal.upwise.model.datatransfer;

public interface IncomingDataTransfer<T> {

    T convertToModelObject();

    boolean isValidAsModelObject();

}
