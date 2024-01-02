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
