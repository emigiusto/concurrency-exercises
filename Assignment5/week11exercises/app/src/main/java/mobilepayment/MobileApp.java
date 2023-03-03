package mobilepayment;

// Hint: You may generate random numbers using Random::ints
import java.util.Random;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

public class MobileApp extends AbstractBehavior<MobileApp.MobileAppCommand> {
    /* --- Messages ------------------------------------- */
    public static abstract class MobileAppCommand {
        public final ActorRef<Account.AccountCommand> sourceAccount;
        public final ActorRef<Account.AccountCommand> targetAccount;
        public final ActorRef<Bank.BankCommand> bank;

        public MobileAppCommand(ActorRef<Account.AccountCommand> from,
                ActorRef<Account.AccountCommand> to,
                ActorRef<Bank.BankCommand> bank) {
            this.sourceAccount = from;
            this.targetAccount = to;
            this.bank = bank;
        }
    }

    public static final class MakeRandomPayments extends MobileAppCommand {
        public final int[] deposits;

        public MakeRandomPayments(ActorRef<Account.AccountCommand> from,
                ActorRef<Account.AccountCommand> to,
                ActorRef<Bank.BankCommand> bank) {
            super(from, to, bank);
            // Creates 100 random transactions with amount in the range of 20 to 10000
            this.deposits = new Random().ints(20, 10000).limit(100).toArray();
        }
    }

    public static final class MobileTransaction extends MobileAppCommand {
        public final int deposit;

        public MobileTransaction(ActorRef<Account.AccountCommand> from,
                ActorRef<Account.AccountCommand> to,
                ActorRef<Bank.BankCommand> bank) {
            super(from, to, bank);
            this.deposit = 10;
        }
    }

    /* --- State ---------------------------------------- */
    // empty

    /* --- Constructor ---------------------------------- */
    // Feel free to extend the contructor at your convenience
    private MobileApp(ActorContext<MobileAppCommand> context) {
        super(context);
        context.getLog()
                .info("Mobile app {} started!", context.getSelf().path().name());
    }

    /* --- Actor initial state -------------------------- */
    public static Behavior<MobileApp.MobileAppCommand> create() {
        return Behaviors.setup(MobileApp::new);
    }

    /* --- Message handling ----------------------------- */
    @Override
    public Receive<MobileAppCommand> createReceive() {
        return newReceiveBuilder()
                .onMessage(MakeRandomPayments.class, this::onRandomPayments)
                .onMessage(MobileTransaction.class, this::onMobileTransaction)
                .build();
    }

    /* --- Handlers ------------------------------------- */
    public Behavior<MobileAppCommand> onRandomPayments(MakeRandomPayments msg) {
        // 11.1.6
        for (int deposit : msg.deposits) {
            msg.bank.tell(new Bank.TransactionExecution(
                    msg.sourceAccount,
                    msg.targetAccount,
                    deposit));
        }
        return this;
    }

    public Behavior<MobileAppCommand> onMobileTransaction(MobileTransaction msg) {
        msg.bank.tell(new Bank.TransactionExecution(
                msg.sourceAccount,
                msg.targetAccount,
                msg.deposit));
        return this;
    }
}
