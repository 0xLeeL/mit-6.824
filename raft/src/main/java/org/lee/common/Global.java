package org.lee.common;

import org.lee.hearbeat.MASTER_STATUS;

public class Global {
    public static MASTER_STATUS masterStatus;

    public static synchronized void setMasterStatus(MASTER_STATUS masterStatus) {
        Global.masterStatus = masterStatus;
    }

    public static synchronized void health() {
        setMasterStatus(MASTER_STATUS.HEALTH);
    }
    public static synchronized void downed() {
        setMasterStatus(MASTER_STATUS.DOWNED);
    }
    public static synchronized void suspend() {
        setMasterStatus(MASTER_STATUS.SUSPEND);
    }

    public static synchronized MASTER_STATUS getMasterStatus() {
        return masterStatus;
    }
}
