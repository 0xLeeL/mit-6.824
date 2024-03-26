# raft 需求
**raft status flow**
![](./img/raft_status_flow.png)

## 1. 基础RPC
- [x] 发送信息，并且回复成功  
- [x] 超时异常报错  
- [x] 连接失败异常报错  
- [x] 序列化与反序列化  
- [ ] 服务路由  
- [x] 部分配置需要从config 里面去获取，因为配置会更新不能取出来

## 2. 机器角色
- [x] salve
  - [x] Respond to RPCs from candidates and leader
  - [ ]If electionRaft timeout elapses without receiving AppendEntries  RPC from current leader or granting vote to candidate: convert to candidate
- [x] leader 
  - [ ] Upon electionRaft: send initial empty AppendEntries RPCs  (heartbeat) to each server; repeat during idle periods to prevent electionRaft timeouts, and maintain metadata of the all followers;
  - [ ] If command received from client: append entry to local log, respond after entry applied to state machine
  - [ ] If last log index ≥ nextIndex for a follower: send AppendEntries RPC with log entries starting at nextIndex
    - [ ] If successful: update nextIndex and matchIndex for follower
    - [ ] If AppendEntries fails because of log inconsistency: decrement nextIndex and retry
  - [ ] If there exists an N such that N > commitIndex, a majority of matchIndex[i] ≥ N, and log[N].term == currentTerm: set commitIndex = N
## 2. append log
- [x] master send data
- [x] slave receive data
- [ ] If RPC request or response contains term T > currentTerm: set currentTerm = T, convert to follower (§5.1)


## 3. 心跳检测/健康机制
- [x] 正常请求与回复
- [x] 健康检查，健康判断
- [x] 超时判断，判断为异常
- [ ] master 重启恢复
- [ ] follower 重启恢复

## 4. 选举
- [ ] 将当前机器从 follower 状态配置为 candidate
- [ ] 发送消息给所有的 candidate   
    - [ ] epoch 任期号+选举号  
    - [x] 随机 pause     

- [x] 接受来自其他 candidate 的消息，并且比较epoch 来判断是否接受 propose
- [x] 选举出新的leader之后，leader同步全局状态。
- [ ] On conversion to candidate, start electionRaft:   
    - [x] Increment currentTerm   
    - [x] Vote for self   
    - [ ] Reset electionRaft timer   
    - [ ] Send RequestVote RPCs to all other server   
- [x] If votes received from majority of servers: become leader/master
- [ ] If AppendEntries RPC received from new leader: convert to follower
- [ ] 在途中加入cluster
- [ ] If electionRaft timeout elapses: start new electionRaft

## 5. 配置更新（更新集群总数量）
- [ ] 增加/减少集群机器的总数量
- [ ] 配置文件化
- [ ] 多进程工具

