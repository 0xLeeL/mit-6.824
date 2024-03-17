# raft 需求

## 1. 基础RPC
- [x] 发送信息，并且回复成功  
- [ ] 超时异常报错  
- [ ] 连接失败异常报错  
- [x] 序列化与反序列化  
- [ ] 服务路由  

## 2. 机器角色
- [x] salve
- [x] master 

## 2. append log
- [x] master send data
- [x] slave receive data

## 3. 心跳检测
- [x] 正常请求与回复
- [x] 健康检查，健康判断
- [ ] 超时判断，判断为异常

## 4. 选举
- [ ] 将当前机器从 follower 状态配置为 candidate
- [ ] 发送消息给所有的 candidate   
    - [ ] epoch 任期号+选举号  
    - [x] 随机 pause     
  
    [//]: # (- 选举过程中出现版本号比较的时候会出现同步问题)
    [//]: # (for example:)
    [//]: # (    在认为可以接受)
- [x] 接受来自其他 candidate 的消息，并且比较epoch 来判断是否接受 propose
- [ ] 失败之后如何开启下一轮选举
    - [ ] candidate 直接开启？




