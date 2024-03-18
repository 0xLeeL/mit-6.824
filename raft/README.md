# raft 需求
**raft status flow**
![](./img/raft_status_flow.png)

## 1. 基础RPC
- [x] 发送信息，并且回复成功  
- [ ] 超时异常报错  
- [ ] 连接失败异常报错  
- [x] 序列化与反序列化  
- [ ] 服务路由  

## 2. 机器角色
- [x] salve
  - [x] Respond to RPCs from candidates and leader
  - [ ]If election timeout elapses without receiving AppendEntries
    RPC from current leader or granting vote to candidate:
    convert to candidat
- [x] leader 
  - [ ] Upon election: send initial empty AppendEntries RPCs
    (heartbeat) to each server; repeat during idle periods to
    prevent election timeouts, and maintain metadata of the all of follower;
  - [ ] If command received from client: append entry to local log,
    respond after entry applied to state machine
  - [ ] If last log index ≥ nextIndex for a follower: send AppendEntries RPC with log entries starting at nextIndex
    - [ ] If successful: update nextIndex and matchIndex for follower
    - [ ] If AppendEntries fails because of log inconsistency: decrement nextIndex and retry
  - [ ] If there exists an N such that N > commitIndex, a majority
      of matchIndex[i] ≥ N, and log[N].term == currentTerm:
      set commitIndex = N
## 2. append log
- [x] master send data
- [x] slave receive data
- [ ] If RPC request or response contains term T > currentTerm:
    set currentTerm = T, convert to follower (§5.1)


## 3. 心跳检测/健康机制
- [x] 正常请求与回复
- [x] 健康检查，健康判断
- [ ] 超时判断，判断为异常
- [ ] master 重启恢复
- [ ] follower 重启恢复

## 4. 选举
- [ ] 将当前机器从 follower 状态配置为 candidate
- [ ] 发送消息给所有的 candidate   
    - [ ] epoch 任期号+选举号  
    - [x] 随机 pause     
  
    [//]: # (- 选举过程中出现版本号比较的时候会出现同步问题)
    [//]: # (for example:)
    [//]: # (    在认为可以接受)
- [x] 接受来自其他 candidate 的消息，并且比较epoch 来判断是否接受 propose
- [ ] On conversion to candidate, start election:   
    - [x] Increment currentTerm   
    - [ ] Vote for self   
    - [ ] Reset election timer   
    - [ ] Send RequestVote RPCs to all other server   
- [x] If votes received from majority of servers: become leader/master
- [ ] If AppendEntries RPC received from new leader: convert to follower
- [ ] 在途中加入cluster
- [ ] If election timeout elapses: start new election

## 5. 配置更新（更新集群总数量）
- [ ] 增加/减少集群机器的总数量


