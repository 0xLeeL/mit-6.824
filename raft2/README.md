以下是将内容翻译成中文并以 Markdown 格式呈现的结果：

---

# 最新任务
1. 实现一阶段和二阶段提交事务  
   为了实现类似于 ZAB（Zookeeper 原子广播）的功能
2. 日志恢复


## 0
- [ ] 发送信息并回复成功
- [ ] 超时异常报错
- [ ] 连接失败异常报错
- [ ] 序列化与反序列化
- [ ] 服务路由
- [ ] 部分配置需要从 `config` 中获取，因为配置会更新，不能直接写死

## 2. 机器角色
- [ ] Slave（从节点）
    - [ ] 响应来自候选者和领导者的 RPC 请求
    - [ ] 重定向：如果收到写请求，则将请求重定向到 Master
    - [ ] 如果选举超时时间到，且未收到当前领导者的 `AppendEntries` RPC 或未给候选者投票，则转换为候选者
    - [ ] 收到 `put data` 的日志条目后，更新数据库数据
- [ ] Leader（领导者）
    - [ ] 选举成功后：向每个服务器发送初始的空 `AppendEntries` RPC（心跳包）；在空闲期间重复发送以防止选举超时，并维护所有从节点的元数据
    - [ ] 如果从 `clientSocket` 收到命令：将条目追加到本地日志，待条目应用到状态机后响应
    - [ ] 如果最后一个日志索引 ≥ 从节点的 `nextIndex`：发送从 `nextIndex` 开始的 `AppendEntries` RPC 日志条目
        - [ ] 如果成功：更新从节点的 `nextIndex` 和 `matchIndex`
        - [ ] 如果 `AppendEntries` 因日志不一致失败：减少 `nextIndex` 并重试
    - [ ] 如果存在一个 `N`，满足 `N > commitIndex`，且大多数从节点的 `matchIndex[i] ≥ N`，并且 `log[N].term == currentTerm`：则设置 `commitIndex = N`
- [ ] ClientSocket（客户端）
    - [ ] 处理各种不同的重定向情况

## 2. 追加日志
- [x] Master 发送数据
- [x] Slave 接收数据
- [ ] 如果 RPC 请求或响应包含的任期 `T > currentTerm`：设置 `currentTerm = T`，转换为从节点 (§5.1)
- [ ] 日志状态追加：如果加入一个新集群或日志同步过程中落后，需要请求 Master 或通知 Master 同步日志状态到 Master 的状态
    - [ ] 处理同步过程中同时存在日志不断追加的情况，这一过程如何管理

## 3. 心跳检测/健康机制
- [ ] 正常请求与回复
- [ ] 健康检查，健康判断
- [ ] 超时判断，判定为异常
- [ ] Master 重启恢复
- [ ] Follower 重启恢复

## 4. 选举
- [ ] 将当前机器从 Follower 状态配置为 Candidate
- [ ] 发送消息给所有 Candidate
    - [ ] 任期号（epoch）+ 选举号
    - [ ] 随机暂停
- [ ] 接受来自其他 Candidate 的消息，并比较任期号（epoch）判断是否接受提议
- [ ] 选举出新 Leader 后，Leader 同步全局状态
- [ ] 转换为 Candidate 时，开始选举：
    - [ ] 增加 `currentTerm`
    - [ ] 为自己投票
    - [ ] 重置选举定时器
    - [ ] 向所有其他服务器发送 `RequestVote` RPC
- [ ] 如果收到大多数服务器的投票：成为 Leader/Master
- [ ] 如果收到新 Leader 的 `AppendEntries` RPC：转换为 Follower
- [ ] 中途加入集群
    - [ ] 同样直接发起选举，但此时会收到其他服务器的提示，告知当前存在 Master，直接成为 Master 的 Follower 即可
- [ ] 如果选举超时时间到：开始新一轮选举

## 5. 配置更新（更新集群总数量）
- [ ] 增加/减少集群机器的总数量
- [ ] 配置文件化
- [ ] 多进程工具

# 代码架构

## RPC
所有网络调用的基础层面，所有模块都依赖于它

--- 

希望这个格式符合你的需求！如果需要进一步调整，请告诉我。