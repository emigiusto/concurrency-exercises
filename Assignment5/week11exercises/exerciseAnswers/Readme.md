# Exercise 11.1

## Mandatory

**11.1.1** Design and implement the guardian actor (in Guardian.java) and complete the Main.java class to start the system. The Main class must send a kick-off message to the guardian. For now, when the guardian receives the kick-off message, it should spawn an MobileApp actor.

Finally, explain the design of the guardian, e.g., state (if any), purpose of messages, etc. Also, briefly explain the purpose of the program statements added to the Main.java to start off the actor system.


Note: In this exercise you should only modify the files Main.java and Guardian.java. The code skeleton already contains the minimal actor code to start the MobileApp actor. If your implementation is correct, you will observe a message INFO mobilepaymentsolution.MobileApp - Mobile app XXX started! or similar when running the system.

> The _Guardian_ does not contain any state because it simply iniates a `MobileApp` by sending a `KickOff` command. The `KickOff` command implements the empty markup interface `GuardianCommand` which is not necessary yet but implemented to allow for further extension.
> 
> In the Main method we are creating the ActorSystem with a new guardian and give it a name. Then we send a message to the guardian to trigger the Kickoff.


**11.1.2.** Design and implement the Account actor (see the file Account.java in the skeleton), including the messages it must handle. Explain your design choices, e.g., elements of the state, how it is initialized, purpose of messages, etc.

> The state of our Account Actor contains only an integer which keeps track of the balance. Account handles messages of type Transaction. They also contain an integer denoting the amount to be added or deducted from the Account.


**11.1.3.** Design and implement the Bank actor (see the file Bank.java in the skeleton), including the messages it must handle. Explain your design choices, e.g., elements of the state, how it is initialized, purpose of messages, etc.

> The `Bank` actor has subclasses named `TransactionExecution` and `AccountCreation` which are in charge of executing transactions between accounts received through the `MobileApp` actor, and create a new `Account` actor respectively. In its state, it keeps track of all accounts registered in that specific bank.
> It supports receiving two types of messages `TransactionExecution` and `AccountCreation`, executing the corresponding methods when those arrive in the mailbox: `onTransactionExecution` and `onAccountCreation`. `onTransactionExecution` in particular sends one message to each account involved

**11.1.4.** Design and implement the Mobile App actor (see the file MobileApp.java in the skeleton), including the messages it must handle. Explain your design choices, e.g., elements of the state, how it is initialized, purpose of messages, etc.

> The `MobileApp` actor has a subclass named `MobileTransaction` which sends transaction messages to bank including the two accounts and the deposit amount. `MobileApp` doesn't hold any state as its use on this system is only as a tool to generate these transactions between accounts. It can only receive message of class `MobileTransaction`.

**11.1.5.** Update the guardian so that it starts 2 mobile apps, 2 banks, and 2 accounts. The guardian must be able to send a message to mobile app actors to execute a set of payments between 2 accounts in a specified bank. Finally, the guardian must send two payment messages: 1) from a1 to a2 via b1 , and 2) from a2 to a1 via b2 . The amount in these payments is a constant of your choice.

> The implementation can be found in method `Guardian#onKickOff`. The message sent will trigger 2 transactions (if uncommented):
> ```java
> mb1.tell(new MobileApp.MobileTransaction(a1, a2, b1));
> mb2.tell(new MobileApp.MobileTransaction(a2, a1, b2));
> ```

**11.1.6.** Modify the mobile app actor so that, when it receives a make payments message from the guardian, it sends 100 transactions between the specified accounts and bank with a random amount. Hint: You may use Random::ints to generate a stream of random integers. The figure below illustrates the computation.

> The implementation can be found in method `MobileApp#onTransaction`. 100 transactions are generated in every call to this method, with a random deposit amount between 20 and 10.000.

**11.1.7.** Update the Account actor so that it can handle a message PrintBalance. The message handler should print the current balance in the target account. Also, update the guardian actor so that it sends PrintBalance messages to accounts 1 and 2 after sending the make payments messages in the previous item.
What balance will be printed? The one before all payments are made, or the one after all payments are made, or anything in between, or anything else? Explain your answer.

> The implementation can be found in method `Guardian#onKickOff`. After the execution of two transactions through the `MobileApp` actor as requested in exercise 11.1.5, one message of class `Account.PrintBalance()` is sent for each account (a1 and a2).
> It can be observed, though, that the printing of the balance in the log record is executed before all the transactions, contrary to what is stated on the codebase. The reason for this interleaving is related to the actor system itself, as the message to print balance arrives earlier to the account actor mailboxes, while the transactions involve another actor (`MobileApp`), creating an small delay due to the MobileApp's mailbox handling and execution.

**11.1.8.** Consider a case where two different bank actors send two deposit exactly at the same time to the same account actor. Can a race condition occur when updating the balance? Explain your answer.

> No, because the messages will both be delivered to the mailbox of that account actor. It will then exceute the corresponding logic and change its internal state based on that. As "[a]n actor can be seen as a sequential unit of computation" and "one can safely assume that there are not concurrency issues within the actor" we can conclude that there will not be any race conditions.

## Challenging
**11.2.9.** Modify the system above so that Accountactors areassociated with a bank,and ensure that negative deposits are only executed if they are sent by that bank. In other words, only the account's bank is allowed to withdraw money. Note that positive deposits may be received from any bank.

>...

**11.2.10.** Modify the system so that an account's balance cannot be below 0. If an account actor receives a negative deposit that reduces the balance below 0, the deposit should be rejected. In such a case, the bank should be informed and the positive deposit for the payee should not be performed.
