# Exercise 12.1

**Mandatory**

**12.1.1** The Server should only send tasks to workers that are idle. If a task needs to be processed and there are no idle workers, then it should be placed in a list of pending tasks. Define the state of the Server so that it can keep track of idle workers, busy workers and pending tasks. Explain why the elements of the state that you added are sufficient to keep track of busy workers and pending tasks.

> To keep track of busy workers one has to store references to the workers and the task they are working on. The latter is necessary to retrieve the task a worker was working on in case it fails. We implement this behaviour with a HasMap.
>
> To keep track of pending tasks one has to store the reference to the task and the client that sent the task. This is also necessary because the reference to a client is only sent in the ComputeTasks message in the beginning. The client reference has to be stored when a task will be carried out at a later point in time.

**12.1.2.** As mentioned above, the Server should always have a minimum number of active workers, and a limit in the number of workers it can hold active at the same time. Extend the state of the Server with two variables containing the number of minimum minWorkers and maximum workers maxWorkers. Also, write the constructor of the Server so that it spawns minWorkers workers. The constructor must properly initialize all the elements of the state, i.e., idle and busy workers, pending tasks, and min/max workers. Explain the implementation of the constructor and why the new elements of the state are sufficient for this exercise.

> The constructor initializes the collections for keeping track of busy and idle workers as well as tasks. Then _minWorkers_ workers are intialised and put into the idle worker collection. This is sufficient because on instantiation there are not tasks to work on and therefore all the workers are idle.

**12.1.3.** Now we move to handling a list of tasks sent from a client. Write the message handler for messages of type ComputeTasks, which contains the list of tasks sent by the client. For each task in the list, the Server
must proceed as follows:
(a) If there are idle workers the task is sent to a worker—using a ComputeTask message.
(b) If there are no idle workers, but the number of busy workers is less than maxWorkers, then spawn a
new worker and send the task.
(c) If none of the above conditions hold, then the task must be placed in the list of pending tasks.
Explain how your implementation addresses these cases.

> We address these cases with an if statement. If there is an idle worker in the list the task gets assigned to this worker and the worker is put into the busy list. Also the worker is watched in case it fails. If there are no idle workers but the maximum amount of workers is not reached we create a new worker and assign the task similar to the first case. If there are no idle workers and the maxWorkers is reached we store the task into a map together with its client reference.

**12.1.4.** If you run the system now, you will notice that two actors will crash due to a division by zero exception.Your task now is to make the server fault-tolerant. The server must watch all the workers it spawns, and, in case any of them fails, a new worker should be spawned and added to the list of idle workers. Explain how your implementation handles this situation. Hint: It might be useful to revisit getContext().watch(...), the class ChildFailed, and onSignal(...).

> Using onSignal() we are listening ChildFailes messages sent by failed workers. It is important that the workers have been watched before using getContext().watch(). In onChildFailed() we are first getting a reference to the failed worker from the message and remove the worker from the busyWorkers HashMap. From there we are also getting a reference to the task it was working on. We create a new worker and a computeTask message with the failed task which we assign to the new worker. We are watching the new worker in case it crashes again and put it into the busyWorkers collection with the corresponding task.

**12.1.5.** As you probably have already noticed, workers send a WorkDone message to the Server when they finish the computation. Here you have to implement the handler for messages of type WorkDone. In particular, when the Server receives this type of message, it must proceed as follows:
(a) If there are pending tasks, the Server takes one and sends it to the worker.
(b) If there are no pending tasks, the Server updates the status of the worker (i.e., move it from busy to
idle).
Explain how your implementation handles the WorkDone message and addresses these cases.

> We are checking with an if statement if a there are pending tasks in the pendingTasks collection. If so we assign it to the free worker and update the busyWorkers collection. We do set the worker to beign watched at this point because it is still being watched.
>
> In case there are no pending tasks we remove the worker from the busyWorkers collection, put it into the idleWorkers colection and unwatch it.

**Challenging**

**12.1.6.** Is there any other situation (different than those covered above) where the Server could check the list of pending tasks and pick one? If so, explain it, and implement a solution that handles the list of pending tasks as you describe.

> not implemented

**12.1.7.** This exercise sheet started by mentioning that we will implement an elastic and fault-tolerant server. How- ever, we have not taken care of elasticity. In this context, elasticity means that the server keeps a number of workers proportional to the workload. Extend the system so that, when the Server receives a WorkDone message, it tells the worker to terminate if the number of idle workers is greater than minWorkers.
Hint I: Note that workers can handle messages of type Stop that stop the worker.
Hint II: When an actor executes Behaviours.stopped() it automatically sends a signal of type
Terminated to the actor watching it. It is important to note two things: (a) ChildFailed extends from Terminated
(b) ThemessagehandlertriestofindahandlerinthesameorderasdefinedinnewReceiveBuilder(). Consequently, if you place the handler for Terminated before the one for ChildFailed, the latter will never be executed.

> Implemented in onWorkDone() and onTerminated()
