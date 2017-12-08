#Advanced Database Systems Final Project

#Authors: Scott Floam and Pratik Karnik

This project will demonstrate distributed replicated concurrency control and recovery. This project creates a light-weight, distributed database, with multiversion read consistency, deadlock detection, replication, failure, and recovery. 

Our system executes transactions using strict two phase locking (using read and write locks) at each site and validation at commit time. A transaction may read a variable and later write that same variable as well as others. Since this site uses replication, we implement the available copies algorithm which allows writes and commits to just the available sites. For example, if Site 1 is down, its last committed value of 123 may be different from Site 2, which is up and has a value of 573.

Our system detects deadlock using a cycle detection mechanism which checks which transactions are waiting on other transactions and aborts the youngest transaction in the cycle. A cycle is determined when at least one transaction, T1, holds a lock on a variable, x1, that transaction, T2, needs and transaction, T2, holds a variable, x2, that transaction, T1, needs. Since both are waiting on each other, none can proceed until one releases its resources. In our system, we abort the youngest transaction involved in the deadlock. Since this system executes instructions line by line, no two transactions can have the same age. The system assumes that users will not restart an aborted transaction for simplicity reasons. Also, we do not test deadlock at every tick since it only affects actions where locking are affected.

Our application currently supports 10 Site locations (1-10) each with 10 DataManagers. All sites hold variables x2, x4, x6, x8, x10, x12, x14, x16, x18, x20. Odd variables (x1, x3, x5, x7, x9, x11, x13, x15, x17, x19) are not replicated, and are found only at a site location equivalent to ((1 + index) mod 10).

Our project made use of Object Oriented Programming to represent the various components of our distributed replicated concurrency control and recovery application. The following describes the setup: 

The DataManager class creates one Site for data replication. The initialization of variables at a Site is conducted in the Site class during the Site's instantiation.

The DataTable class holds Variables at a Site. All Sites have a DataTable that holds even Variables (x2, x4, x6, x8, x10, x12, x14, x16, x18, x20). Odd variables (x1, x3, x5, x7, x9, x11, x13, x15, x17, x19) are not replicated, and are found only at a site location equivalent to ((1 + index) mod 10).

The GlobalConstants class stores commonly used constant values.

The LockObj class stores information about a lock held by a transaction. These locks are used to determine if a transaction can access a variable a particular site.

The LockTable class contains a list of locks on Variables held by Transaction at a given Site.

The Operation class stores information about each action performed in the system.

The Site class consists of a DataTable that holds Variables and a LockTable corresponding with those Variables. See DataTable and LockTable for more information about these respective classes.

The Transaction class keeps track of the transaction details. It holds information such as whether it is blocked, how long it has been running, the id of the transaction, and the variables corresponding to that Transaction. In addition, it also keeps a log of its operations.

The Variable class keeps contains information about the data being stored across the Sites. Each Variable has an ID, value, intermediate value (used to hold information before it commits), whether it is exclusive to the Site, correspondingTransactions (Transactions that use this variable), and other critical information about the data used in this system.

The TransactionManager class assigns each operation provided by the user's text file or operations entered directly by the user. The TransactionManager reads the parsed operation and determines if it is a begin, beginRO, read, write, dump, fail, recover, end. Depending on the operation, the TransactionManager executes an action. The TransactionManager drives all operations. It determines which Transactions can operate, which have to wait, and which must abort. The TransactionManager also can cause sites to fail and recover. The TransactionManager is robust and understands how to detect situations where deadlock occurs. It chooses the youngest Transaction when aborting a Transaction. See the API for additional information.

The ExecuteTransaction class is used to parse the operation information to send to the TransactionManager class.
