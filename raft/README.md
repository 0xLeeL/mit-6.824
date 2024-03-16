# raft 需求

## 1. 基础RPC
1. 发送信息，并且回复成功
2. 超时异常报错
3. 连接失败异常报错
4. 序列化与反序列化
5. 服务路由

## 2. 机器角色
1. salve
2. master 

## 2. append log
1. master send data
2. slave receive data

## 3. 心跳检测
1. 正常请求与回复
2. 健康检查，健康判断
3. 超时判断，判断为异常

## 4. 选举
1. 将当前机器从 follower 状态配置为 candidate
2. 随机 pause， 然后发送消息给所有的 candidate   
    消息内容以及格式
   - epoch 任期号+选举号
- 选举过程中出现版本号比较的时候会出现同步问题
for example:
    在认为可以接受
3. 接受来自其他 candidate 的消息，并且比较epoch 来判断是否接受 propose