package org.sapphon.upwise.model.datatransfer;

public interface IncomingDataTransfer<T> {

    T convertToModelObject();

    boolean isValidAsModelObject();

}
