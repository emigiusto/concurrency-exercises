package mobilepayment;

import akka.actor.typed.ActorSystem;

import java.io.IOException;

public class Main {

	public static void main(String[] args) {
		
		// Creating the ActorSystem with a guardian and giving it a name. Then send the guardian a message to trigger the Kickoff
		final ActorSystem<Guardian.GuardianCommand> guardian = ActorSystem.create(Guardian.create(), "guardian_actor");
		
		// start actor system
		guardian.tell(new Guardian.KickOff());

		// init message
		// To be implemented

		// wait until user presses enter
		try {
			System.out.println(">>> Press ENTER to exit <<<");
			System.in.read();
		} catch (IOException e) {
			System.out.println("Error " + e.getMessage());
			e.printStackTrace();
		} finally {
			// terminate actor system execution
			// To be implemented
		}
	}
}
