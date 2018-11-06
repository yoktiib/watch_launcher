package com.pomohouse.launcher.utils;

import com.pomohouse.launcher.models.contacts.CallDao;
import com.pomohouse.launcher.models.contacts.ContactCollection;
import com.pomohouse.launcher.content_provider.POMOContract;

import java.util.ArrayList;

/**
 * Created by Admin on 9/14/16 AD.
 */
public class CombineObjectConstance {
    private static CombineObjectConstance ourInstance = new CombineObjectConstance();
    private boolean initDevice = false;
    private boolean inClassTime = false;
    private boolean watchAlarm = false;
    private boolean silentMode = false;
    private boolean batteryStatusCharging = false;
    private boolean autoAnswer = false;
    private boolean clearContactNotifyDataChange = false;
    private boolean haveMissCall = false;
    private boolean callIn = false;

    public boolean isCallIn() {
        return callIn;
    }

    public void setCallIn(boolean callIn) {
        this.callIn = callIn;
    }

    public boolean isHaveMissCall() {
        return haveMissCall;
    }

    public void setHaveMissCall(boolean haveMissCall) {
        this.haveMissCall = haveMissCall;
    }


/*    public enum TypeOfCall {VOIP, CALL}

    private TypeOfCall typeCall = TypeOfCall.CALL;*/


   /* public TypeOfCall getTypeCall() {
        return typeCall;
    }*/

    /*public void setTypeCall(TypeOfCall typeCall) {
        this.typeCall = typeCall;
    }*/

    public boolean isInitDevice() {
        return initDevice;
    }

    public void setInitDevice(boolean initDevice) {
        this.initDevice = initDevice;
    }

    public boolean isInClassTime() {
        return inClassTime;
    }

    public void setInClassTime(boolean inClassTime) {
        this.inClassTime = inClassTime;
    }

    public boolean isWatchAlarm() {
        return watchAlarm;
    }

    public void setWatchAlarm(boolean watchAlarm) {
        this.watchAlarm = watchAlarm;
    }

    public boolean isSilentMode() {
        return silentMode;
    }

    public void setSilentMode(boolean silentMode) {
        this.silentMode = silentMode;
    }

    public boolean isBatteryStatusCharging() {
        return batteryStatusCharging;
    }

    public void setBatteryStatusCharging(boolean batteryStatusCharging) {
        this.batteryStatusCharging = batteryStatusCharging;
    }

    public boolean isAutoAnswer() {
        return autoAnswer;
    }

    public void setAutoAnswer(boolean autoAnswer) {
        this.autoAnswer = autoAnswer;
    }

    public boolean isClearContactNotifyDataChange() {
        return clearContactNotifyDataChange;
    }

    public void setClearContactNotifyDataChange(boolean clearContactNotifyDataChange) {
        this.clearContactNotifyDataChange = clearContactNotifyDataChange;
    }

    public static CombineObjectConstance getInstance() {
        return ourInstance;
    }

    private CombineObjectConstance() {
    }

    private ContactEntityConstance contactEntity = new ContactEntityConstance();
    private CallEntityConstance callEntity = new CallEntityConstance();

    public CallEntityConstance getCallEntity() {
        return callEntity;
    }

    public ContactEntityConstance getContactEntity() {
        return contactEntity;
    }

    public class CallEntityConstance {
        ArrayList<CallDao> callDao = new ArrayList<>();
        private int missCalls;
        private int inComingCalls;
        private int outGoingCalls;

        public int calculateMissCall(String number) {
            int missCall = 0;
            if (callDao != null) {
                for (CallDao call : callDao)
                    if (number.equalsIgnoreCase(call.getNumber()) && call.getType() == POMOContract.CallEntry.MISSED_TYPE)
                        missCall++;
                return missCall;
            }
            return missCall;
        }

        public int getMissCalls() {
            return missCalls;
        }

        public void setMissCalls(int missCalls) {
            this.missCalls = missCalls;
        }

        public int getInComingCalls() {
            return inComingCalls;
        }

        public void setInComingCalls(int inComingCalls) {
            this.inComingCalls = inComingCalls;
        }

        public int getOutGoingCalls() {
            return outGoingCalls;
        }

        public void setOutGoingCalls(int outGoingCalls) {
            this.outGoingCalls = outGoingCalls;
        }

        public ArrayList<CallDao> getCallDao() {
            return callDao;
        }

        public void setCallDao(ArrayList<CallDao> callDao) {
            this.callDao = callDao;
        }
    }

    public class ContactEntityConstance {
        private boolean contactSynced = false;
        private boolean contactQueried = false;

        public boolean isContactQueried() {
            return contactQueried;
        }

        public void setContactQueried(boolean contactQueried) {
            this.contactQueried = contactQueried;
        }

        ContactCollection contactCollection;

        public boolean isContactSynced() {
            return contactSynced;
        }

        public void setContactSynced(boolean contactSynced) {
            this.contactSynced = contactSynced;
        }

        public ContactCollection getContactCollection() {
            if (contactCollection == null) {
                contactCollection = new ContactCollection();
                contactCollection.setContactModelList(new ArrayList<>());
                return contactCollection;
            }
            return contactCollection;
        }

        public ContactCollection setContactCollection(ContactCollection contactCollection) {
            this.contactCollection = contactCollection;
            return contactCollection;
        }
    }
}
