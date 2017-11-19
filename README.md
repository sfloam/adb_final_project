# adb_final_project
Advanced Database Systems Final Project

**Data Requirements**
- [ ] Create 20 distinct variables x1, ..., x20 (the numbers between 1 and 20 will be referred to as indexes below)
- [ ] Create 10 sites numbered 1 to 10. A copy is indicated by a dot. Thus, x6.2 is the copy of variable x6 at site 2. 
- [ ] Ensure odd indexed variables are at one site each (1 + index mod 10) --> x3 and x13 are both at site 4
- [ ] Even indexed variables are at all sites
- [ ] Each variable xi is initialized to value 10i
- [ ] Each site has an independent lock table
- [ ] If a site fails, the lock table is erased

**Algorithm Requirements**
- [ ] Implement the available copies approach to replication using 2PL (using read and write locks) at each site and validation at commit 
      time. 
- [ ] A transaction may read a variable and later write that same variable as well as others.
- [ ] Use the version of the algorithm specified in my notes. 
- [ ] Note that available copies allows writes and commits to just the available sites, so if site A is down, its last committed value of 
      x may be different from site B which is up.
- [ ] Detect deadlocks using cycle detection and abort the youngest transaction in the cycle. 
- [ ] System must keep track of the oldest transaction time of any transaction holding a lock.
- [ ] Ensure that no two transactions will have the same age.
- [ ] There is never a need to restart an aborted transaction. That is the job of the application (and is often done incorrectly). 
- [ ] Deadlock detection need not happen at every tick. When it does, it should happen at the beginning of the tick.
- [ ] Read-only transactions should use multiversion read consistency.
