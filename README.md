# adb_final_project
Advanced Database Systems Final Project

**Data Requirements**
- [x] Create 20 distinct variables x1, ..., x20 (the numbers between 1 and 20 will be referred to as indexes below)
- [x] Create 10 sites numbered 1 to 10. A copy is indicated by a dot. Thus, x6.2 is the copy of variable x6 at site 2. 
- [x] Ensure odd indexed variables are at one site each (1 + index mod 10) --> x3 and x13 are both at site 4
- [x] Even indexed variables are at all sites
- [x] Each variable xi is initialized to value 10i
- [x] Each site has an independent lock table
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
- [ ] (REMOVED from syllabus) When running our tests, ensure that operations occurring concurrently don’t conflict with one another.
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

**Design Specifications**

- [ ] Program should consist of two parts:
  - [ ] a single transaction manager that translates read and write requests on variables to read and write requests on copies using the available copy algorithm described in the notes. 
  - [ ] The transaction manager never fails. (Having a single global transaction manager that never fails is a simplification of reality, but it is not too hard to get rid of that assumption by using a shared disk configuration.)
- [ ] If the TM requests a read for transaction T and cannot get it due to failure, the TM should try another site (all in the same step).
  - [ ] If no relevant site is available, then T must wait. 
  - [ ] This applies to read-only transactions as well which must have access to the latest version of each variable before the transaction begins.
  - [ ] T may also have to wait for conflicting locks.
    - [ ] Thus the TM may accumulate an input command for T and will try it on the next tick (time moment). 
- [ ] While T is blocked (whether waiting for a lock to be released or a failure to be cleared), our test files will emit no no operations 
      for T so the buffer size for messages from any single transaction can be of size 1.
- [ ] If a site fails and recovers, the DM would normally perform local recovery first (perhaps by asking the TM about transactions that 
      the DM holds pre-committed but not yet committed), but this is unnecessary since, in the simulation model, commits are atomic with 
      respect to failures. Therefore, all non-replicated variables are available for reads and writes. 
- [ ] Regarding replicated variables, the site makes them available for writing, but not reading. In fact, reads will not be allowed at 
      recovered sites until a committed write takes place (see lecture notes on recovery when using the available copies algorithm).
- [ ] During execution, your program should say which transactions commit and which abort and why. For debugging purposes you should 
      implement (for your own sake) a command like querystate() which will give the state of each DM and the TM as well as the data 
      distribution and data values. 
- [ ] Finally, each read that occurs should show the value read.


**Code Structure**
- [ ] Author of the function should also be listed. 
- [ ] Should see the major components, relationships between the components, and communication between components. This should work recursively within components.
- [ ] A figure showing interconnections and no more than three pages of text would be helpful.
- [ ] The submitted code similarly should include as a header:
      - The author
      - The date
      - The general description of the function
      - The inputs
      - The outputs
      - The side effects.
- [ ] Provide a few testing scripts in plain text to the grader in the format above and say what you believe will happen in each
test.
- [x] You should store you entire repository in git, enabling the graders to trace your progress.
- [ ] Finally, you will use reprozip to make your project reproducible even across different architectures.
