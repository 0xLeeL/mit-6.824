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