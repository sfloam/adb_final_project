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


**Test Specifications**
- [ ] Input instructions come from a file or the standard input, output goes to standard out. (That means your algorithms may not look 
      ahead in the input.)
- [ ] Input instructions occurring in one step begin at a new line and end with a carriage return
- [ ] System supports several operations in each step, though at most only one per transaction. 
- [ ] If operations is blocked due to conflicting locks, that does not affect the operations of other transactions.
      You may assume that all operations on a single line occur concurrently. 
- [ ] When running our tests, ensure that operations occurring concurrently don’t conflict with one another. 
- [ ] Ensure that when a transaction is waiting, it will not receive another operation.
- [ ] If an operation for T1 is waiting for a lock held by T2, then in the same time step when T2 commits, the operation for T1 proceeds 
      if there is no other lock request ahead of it. 
- [ ] Lock acquisition is first come first served but several transactions may hold read locks on the same item. 
      So, if x is currently read-locked by T1 and there is no waiting list and T2 requests a read lock on x, then T2 can get it. However, 
      if x is currently read-locked by T1 and T3 is waiting for a write lock on x and T2 subsequently requests a read lock on x, then T2 
      must wait for T3 either to be aborted or to complete its possesion of x; that is, T2 cannot skip T3.
- [ ] The execution file has the following format:
            
            begin(T1) says that T1 begins 
            
            beginRO(T3) says that T3 begins and is read-only
            
            R(T1, x4) says transaction 1 wishes to read x4 (provided it can get the locks or provided it doesn’t 
            need the locks (if T1 is a read-only transaction)). It should read any up (i.e. alive) copy and 
            return the current value (or  the value when T1 started for read-only transaction).It should print
            that value.
            
            W(T1, x6,v) says transaction 1 wishes to write all available copies of x6  (provided it can get the 
            locks on available copies) with the value v. So, T1 can write to x6 only when T1 has locks on all 
            sites that are up and that contain x6.
            
            
      `     dump() gives the committed values of all copies of all variables at all sites, sorted per site.

            dump(i) gives the committed values of all copies of all variables at site i.
            
            dump(xj) gives the committed values of all copies of variable xj at all sites.
            
            end(T1) causes your system to report whether T1 can commit.
            
            fail(6) says site 6 fails. (This is not issued by a transaction, but is just an event that the tester will execute.)
            
            recover(7) says site 7 recovers. (Again, a tester-caused event) We discuss this further below.
            
            A newline means time advances by one. There will be one instruction per line.
                  Example (partial script with six steps in which transactions T1 commits, and one of T3 and T4 may commit)
                        begin(T1) 
                  begin(T2)
            begin(T3)
           
                  W(T1, x1,5) 
            W(T3, x2,32)
                  W(T2, x1,17) — will cause T2 to wait, but the write will go ahead after T1 commits
                        end(T1) ]] begin(T4)
                        W(T4, x4,35) 
                  W(T3, x5,21)
                        W(T4,x2,21)]] W(T3,x4,23)
                        — T4 will abort because it’s younger
