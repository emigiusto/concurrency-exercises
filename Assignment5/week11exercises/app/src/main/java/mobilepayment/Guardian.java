package mobilepayment;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

public class Guardian extends AbstractBehavior<Guardian.GuardianCommand> {

    public interface GuardianCommand {
    };

    public static final class KickOff implements GuardianCommand {
    }

    /* --- Messages ------------------------------------- */
    // empty

    /* --- State ---------------------------------------- */
    // empty

    /* --- Constructor ---------------------------------- */
    private Guardian(ActorContext<GuardianCommand> context) {
        super(context);
    }

    /* --- Actor initial state -------------------------- */
    public static Behavior<GuardianCommand> create() {
        return Behaviors.setup(Guardian::new);
    }

    /* --- Message handling ----------------------------- */
    @Override
    public Receive<GuardianCommand> createReceive() {
        return newReceiveBuilder()
                .onMessage(KickOff.class, this::onKickOff)
                .build();
    }

    /* --- Handlers ------------------------------------- */
    private Behavior<GuardianCommand> onKickOff(KickOff msg) {
        // 11.1.5
        // Spawn the MobileApp
        ActorRef<MobileApp.MobileAppCommand> mb1 = getContext().spawn(MobileApp.create(), "mb1");
        ActorRef<MobileApp.MobileAppCommand> mb2 = getContext().spawn(MobileApp.create(), "mb2");
        // Spawn the Accounts
        ActorRef<Account.AccountCommand> a1 = getContext().spawn(Account.create(), "a1");
        ActorRef<Account.AccountCommand> a2 = getContext().spawn(Account.create(), "a2");
        // Spawn the Bank
        ActorRef<Bank.BankCommand> b1 = getContext().spawn(Bank.create(), "b1");
        ActorRef<Bank.BankCommand> b2 = getContext().spawn(Bank.create(), "b2");

        // 11.1.5
        // mb1.tell(new MobileApp.MobileTransaction(a1, a2, b1));
        // mb2.tell(new MobileApp.MobileTransaction(a2, a1, b2));

        // 11.1.6
        mb1.tell(new MobileApp.MakeRandomPayments(a1, a2, b1));
        mb2.tell(new MobileApp.MakeRandomPayments(a2, a1, b2));

        // 11.1.7
        a1.tell(new Account.PrintBalance());
        a2.tell(new Account.PrintBalance());
        return this;
    }
}
