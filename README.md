# 整体课程表格
https://pdos.csail.mit.edu/6.824/schedule.html


# LEC 1  6.5840 Lab 1: MapReduce

https://pdos.csail.mit.edu/6.824/labs/lab-mr.html
实现思路：  
一个master用于接收task，worker 执行任务，同时使用submitter 来提交任务，提交一个function

还需要完成的内容，容错
- worker 容错
- master 容错？
- task stealing 任务窃取，快速执行完成的worker 回去尝试抢夺 没有完成的worker的task
## step 1
实现一个数字数据增加的方法

# 6.5840 Lab 2: Raft
https://pdos.csail.mit.edu/6.824/labs/lab-raft.html[rpc.go]

- 实现一个rpc
- 监控、
- 错误检测、
- 容错
- 自动恢复 
- 并行化、容错、数据分发和负载均衡

# gfs
- 怎么保证一致性
- 怎么处理错误
-   大多数文件通过追加新数据而不是覆盖现有数据来变更。文件内的随机写入几乎不存在。一旦写入，文件只被读取，而且通常只是顺序读取。多种数据共享这些特征。有些可能构成大型仓库，供数据分析程序扫描。有些可能是运行中的应用程序不断生成的数据流。有些可能是档案数据。有些可能是在一台机器上产生的中间结果，并在另一台机器上处理，无论是同时还是随后。鉴于在巨大文件上的这种访问模式，追加成为性能优化和
- 系统必须有效实现多个客户端同时追加到同一文件的明确语义。
- 工作负载还包括许多大型的、顺序的写入，这些写入会向文件追加数据。
- 系统由许多经济实惠的商品组件构建，这些组件经常会失败。系统必须不断监测自身，并能够及时检测、容忍并从组件故障中恢复。


# raft


- 为了阻止选票起初就被瓜分，选举超时时间是从一个固定的区间（例如 150-300 毫秒）随机选择。


# netty 使用注意问题
### pipeline
pipeline 的读写顺序是相反的
<pre>

 HEAD <-> BaseHandler1 <-> BaseHandler2 <-> CustomHandler1 <-> CustomHandler2 <-> TAIL
 数据进站的顺序为 BaseHandler1 , BaseHandler2 , CustomHandler1 , CustomHandler2
 数据出战的顺序为 CustomHandler2 , CustomHandler1 , BaseHandler2  , BaseHandler1
 这样的设计可以保证 在进站的时候基础handler 优先处理数据，然后再由我们的自定义handler 处理数据，同时在出战的时候也是最后处理的handler

</pre>
```java
class Demo {
    void test_pipeline(Channel ch) {
        ch.pipeline()
                .addLast(inBoundHandler1)
                .addLast(inBoundHandler2)
                .addLast(inBoundHandler3)
                // 这里的 消息进入顺序是 
                // inBoundHandler1 -> inBoundHandler2 -> inBoundHandler3

                .addLast(outBoundHandler1)
                .addLast(outBoundHandler2)
                .addLast(outBoundHandler3);
                // 这里的 消息写出顺序是 
                // outBoundHandler3 -> outBoundHandler2 outBoundHandler1
    }
}
```