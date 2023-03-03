package mobilepayment;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

public class Account extends AbstractBehavior<Account.AccountCommand> {
    
    /* --- Messages ------------------------------------- */
    public interface AccountCommand {
    }

    public static final class Transaction implements AccountCommand {
        public final int deposit;

        public Transaction(int deposit) {
	        this.deposit = deposit;
        }
    }
    
    public static final class PrintBalance implements AccountCommand {
    }

    /* --- State ---------------------------------------- */
    private int balance;


    /* --- Constructor ---------------------------------- */
    private Account(ActorContext<AccountCommand> context) {
        super(context);
        this.balance = 0;
    }

    
    /* --- Actor initial state -------------------------- */
    public static Behavior<AccountCommand> create() {
        return Behaviors.setup(Account::new);
    }
    

    /* --- Message handling ----------------------------- */
    //11.1.7
    @Override
    public Receive<AccountCommand> createReceive() {
        return newReceiveBuilder()
                .onMessage(Transaction.class, this::onTransaction)
                .onMessage(PrintBalance.class, this::onPrintBalance)
                .build();
    }


    /* --- Handlers ------------------------------------- */
    public Behavior<AccountCommand> onTransaction(Transaction msg) {
        this.balance = this.balance + msg.deposit;
        this.getContext()
            .getLog()
            .info((int) msg.deposit + " was added to the account " + this.getContext().getSelf().path().name() + ". The new balance is: " + this.balance);
        return this;
    }
    //11.1.7
    public Behavior<AccountCommand> onPrintBalance(PrintBalance msg) {
        this.getContext()
            .getLog()
            .info("The balance of the account "+ this.getContext().getSelf().path().name() + " is: " +  this.balance);
        return this;
    }
}
