package mathsserver;

// Hint: The imports below may give you hints for solving the exercise.
//       But feel free to change them.

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.ChildFailed;
import akka.actor.typed.Terminated;
import akka.actor.typed.javadsl.*;

import java.util.UUID;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.IntStream;

import mathsserver.Client.ClientCommand;

public class Server extends AbstractBehavior<Server.ServerCommand> {
	/* --- Messages ------------------------------------- */
	public interface ServerCommand {
	}

	public static final class ComputeTasks implements ServerCommand {
		public final List<Task> tasks;
		public final ActorRef<Client.ClientCommand> client;

		public ComputeTasks(List<Task> tasks,
				ActorRef<Client.ClientCommand> client) {
			this.tasks = tasks;
			this.client = client;
		}
	}

	public static final class WorkDone implements ServerCommand {
		ActorRef<Worker.WorkerCommand> worker;

		public WorkDone(ActorRef<Worker.WorkerCommand> worker) {
			this.worker = worker;
		}
	}

	/* --- State ---------------------------------------- */
	// 12.1.1
	private final List<ActorRef<Worker.WorkerCommand>> idleWorkers;
	private final Map<ActorRef<Worker.WorkerCommand>, Task> busyWorkers;
	private final Map<Task, ActorRef<ClientCommand>> pendingTasks;

	// Keeping track of all the tasks and corresponding clients.
	// TODO See if this can be solved smarter
	private final Map<Task, ActorRef<ClientCommand>> tasksAndClients;

	private final int minWorkers;
	private final int maxWorkers;

	/* --- Constructor ---------------------------------- */
	private Server(ActorContext<ServerCommand> context,
			int minWorkers,
			int maxWorkers) {
		super(context);
		// 12.1.1
		this.idleWorkers = new ArrayList<>();
		this.busyWorkers = new HashMap<>();
		this.pendingTasks = new HashMap<>();
		this.tasksAndClients = new HashMap<>();
		// 12.1.2
		this.minWorkers = minWorkers;
		this.maxWorkers = maxWorkers;

		IntStream.range(0, minWorkers).forEach((id) -> {
			ActorRef<Worker.WorkerCommand> worker = getContext().spawn(Worker.create(context.getSelf()),
					"worker_" + id);
			idleWorkers.add(worker);
			getContext().watch(worker);
		});
	}

	/* --- Actor initial state -------------------------- */
	public static Behavior<ServerCommand> create(int minWorkers, int maxWorkers) {
		return Behaviors.setup(context -> new Server(context, minWorkers, maxWorkers));
	}

	/* --- Message handling ----------------------------- */
	@Override
	public Receive<ServerCommand> createReceive() {
		return newReceiveBuilder()
				.onMessage(ComputeTasks.class, this::onComputeTasks)
				.onMessage(WorkDone.class, this::onWorkDone)
				// To be extended
				.onSignal(ChildFailed.class, this::onChildFailed)
				.onSignal(Terminated.class, this::onTerminated)
				.build();
	}

	/* --- Handlers ------------------------------------- */
	public Behavior<ServerCommand> onComputeTasks(ComputeTasks msg) {
		// 12.1.3
		// If there are idle workers the task is sent to a worker using a ComputeTask
		// message.
		// If there are no idle workers, but the number of busy workers is less than
		// maxWorkers, then spawn a new worker and send the task.
		// If none of the above conditions hold, then the task must be placed in the
		// list of pending tasks.
		msg.tasks.forEach(task -> {
			tasksAndClients.put(task, msg.client);
			Worker.ComputeTask computeTaskMsg = new Worker.ComputeTask(task, msg.client);

			if (!this.idleWorkers.isEmpty()) {
				ActorRef<Worker.WorkerCommand> worker = this.idleWorkers.remove(0);
				worker.tell(computeTaskMsg);
				busyWorkers.put(worker, task);
				getContext().watch(worker);
			} else if (this.busyWorkers.size() < this.maxWorkers) {
				ActorRef<Worker.WorkerCommand> worker = getContext().spawn(Worker.create(getContext().getSelf()),
						"worker_" + UUID.randomUUID());
				worker.tell(computeTaskMsg);
				busyWorkers.put(worker, task);
				getContext().watch(worker);
			} else {
				this.pendingTasks.put(task, msg.client);
			}
		});

		return this;
	}

	public Behavior<ServerCommand> onWorkDone(WorkDone msg) {

		ActorRef<Worker.WorkerCommand> worker = msg.worker;
		System.out.println("No. of idle workers: " + idleWorkers.size());
		System.out.println("No. of workers: " + idleWorkers.size() + busyWorkers.size());

		// Stopping the worker if there are more idle workers than minWorkers
		if (idleWorkers.size() > minWorkers) {
			Worker.Stop stopMsg = new Worker.Stop();
			worker.tell(stopMsg);
			return this;
		}

		if (pendingTasks.isEmpty()) {
			// Put worker into idle and unwatch it
			busyWorkers.remove(worker);
			idleWorkers.add(worker);
			getContext().unwatch(worker);
		} else {
			// get some task from pending and snd it to the worker. Update busy worker. No
			// need to watch the worker because it is still being watched.
			Task task = (Task) pendingTasks.keySet().toArray()[0];
			Worker.ComputeTask computeTaskMsg = new Worker.ComputeTask(task, pendingTasks.remove(task));
			worker.tell(computeTaskMsg);
			busyWorkers.put(worker, task);
		}
		return this;
	}

	public Behavior<ServerCommand> onChildFailed(ChildFailed msg) {
		// Get reference of crashed child
		ActorRef<Void> crashedWorker = msg.getRef();

		// Retreive the task the crashed worker was checking
		Task nonProcessedTask = busyWorkers.remove(crashedWorker);

		// Remove crashed worker from busy workers
		if (nonProcessedTask != null) {
			// Create a new worker
			final ActorRef<Worker.WorkerCommand> newWorker = getContext().spawn(Worker.create(getContext().getSelf()),
					"worker_" + UUID.randomUUID());
			// Watch it in case it crashes (it can be that the replacement also crashes)
			getContext().watch(newWorker);

			Worker.ComputeTask computeTaskMsg = new Worker.ComputeTask(nonProcessedTask,
					tasksAndClients.get(nonProcessedTask));
			newWorker.tell(computeTaskMsg);

			// Add the new worker to busy workers
			busyWorkers.put(newWorker, nonProcessedTask);
			getContext().getLog().info("{}: Worker {} crashed.\nNew worker{} retrying to compute the task.",
					getContext().getSelf().path().name(),
					crashedWorker.path().name(), nonProcessedTask, msg.cause(),
					newWorker.path().name());

		} else {
			// Never going to be executed
			getContext().getLog().info("{}: No job from worker {} found.",
					getContext().getSelf().path().name(),
					crashedWorker.path().name());
		}
		return this;
	}

	public Behavior<ServerCommand> onTerminated(Terminated msg) {
		ActorRef<Void> worker = msg.getRef();

		if (busyWorkers.containsKey(worker)) {
			busyWorkers.remove(worker);

			// Is it necessary to unwatch terminated workers?
			getContext().unwatch(worker);
			getContext().getLog().info("{}: {} terminated normally.",
					getContext().getSelf().path().name(),
					worker.path().name());
		} else {
			// Never going to be executed
			getContext().getLog().info("{}: No job from worker {} found.",
					getContext().getSelf().path().name(),
					worker.path().name());
		}

		return this;
	}
}
