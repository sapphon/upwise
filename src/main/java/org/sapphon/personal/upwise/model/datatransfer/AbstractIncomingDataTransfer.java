package org.sapphon.personal.upwise.model.datatransfer;

public abstract class AbstractIncomingDataTransfer<T> implements IncomingDataTransfer<T> {

    @Override
    public boolean isValidAsModelObject() {
        T temp;
        try{
            temp = this.convertToModelObject();
        }catch(Exception e){
            return false;
        }
        return temp != null;
    }
}
