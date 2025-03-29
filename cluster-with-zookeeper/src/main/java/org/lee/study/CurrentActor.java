package org.lee.study;

public enum CurrentActor {
    MASTER,
    FOLLOWER,
    CANDIDATE,
    NEW_NODE,    // 刚刚加入还没有同步完成日志

}
