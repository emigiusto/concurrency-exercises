package mobilepayment;

import java.util.ArrayList;
import java.util.List;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

public class Bank extends AbstractBehavior<Bank.BankCommand> {

    /* --- Messages ------------------------------------- */
    public interface BankCommand {
    }
    // Feel free to add message types at your convenience

    public static final class TransactionExecution implements BankCommand {
        public final ActorRef<Account.AccountCommand> sourceAccount;
        public final ActorRef<Account.AccountCommand> targetAccount;
        public final int deposit;

        public TransactionExecution(ActorRef<Account.AccountCommand> from,
                ActorRef<Account.AccountCommand> to,
                int deposit) {
            this.sourceAccount = from;
            this.targetAccount = to;
            this.deposit = deposit;
        }
    }

    public static final class AccountCreation implements BankCommand {
        public final ActorRef<Account.AccountCommand> account;

        public AccountCreation(ActorRef<Account.AccountCommand> account) {
            this.account = account;
        }
    }

    /* --- State ---------------------------------------- */
    private final List<ActorRef<Account.AccountCommand>> bankAccounts;

    /* --- Constructor ---------------------------------- */
    private Bank(ActorContext<BankCommand> context) {
        super(context);
        this.bankAccounts = new ArrayList<ActorRef<Account.AccountCommand>>();
    }

    /* --- Actor initial state -------------------------- */
    public static Behavior<BankCommand> create() {
        return Behaviors.setup(Bank::new);
    }

    /* --- Message handling ----------------------------- */
    @Override
    public Receive<BankCommand> createReceive() {
        return newReceiveBuilder()
                .onMessage(TransactionExecution.class, this::onTransactionExecution)
                .onMessage(AccountCreation.class, this::onAccountCreation)
                .build();
    }

    /* --- Handlers ------------------------------------- */
    public Behavior<BankCommand> onTransactionExecution(TransactionExecution msg) {
        msg.sourceAccount.tell(new Account.Transaction(-(msg.deposit)));
        msg.targetAccount.tell(new Account.Transaction(msg.deposit));
        return this;
    }

    public Behavior<BankCommand> onAccountCreation(AccountCreation msg) {
        bankAccounts.add(msg.account);
        this.getContext()
                .getLog()
                .info("Account " + msg.account + " was registered in the bank.");
        return this;
    }
}
