package exercises03;

import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class Person {

    // There is no mutable state that can escape (Strings can, but they're
    // immutable)
    // Safe publication: Fields are initialized before publicated, they're volatile
    // or final
    // Objects are initialized because they're declared final or volatile
    // Class state can't be immutable but setter method is synchronized -> mutual
    // exclusive

    private final long id;
    private volatile String name;
    private volatile int zip;
    private volatile String address;

    // Holds the next id in the thread safe class AtomicLong
    private static AtomicLong nextId = new AtomicLong(0);

    private static final String initialStringValue = "";

    public Person() {
        this(nextId.getAndIncrement());
    }

    public Person(long id) {
        this.id = id;
        this.name = initialStringValue;
        this.zip = 0; // Redundant, but explicitely mentioned for clarity
        this.address = initialStringValue;
    }

    public synchronized void setZipAndAddress(int zip, String address) {
        this.zip = zip;
        this.address = address;
    }

    public long getId() {
        // Primitive variable
        return this.id;
    }

    public String getName() {
        if (this.name != null) {
            // String is immutable therefore it can be returned
            // witout worrying about a safe publication issue
            return this.name;
        } else {
            return initialStringValue;
        }

    }

    public int getZip() {
        // Primitive variable
        return this.zip;
    }

    public String getAddress() {
        if (this.address != null) {
            // String is immutable
            return this.address;
        } else {
            return initialStringValue;
        }
    }

    public static void main(String[] args) {

        Thread t1 = new Thread(() -> {
            Person p = new Person();
            // Question: is this a problem, as the string concatenation is not atomic?
            System.out.println("t1: " + p.getId());
            System.out.println("t1: " + p.getName());
            System.out.println("t1: " + p.getZip());
            System.out.println("t1: " + p.getAddress());
            p.setZipAndAddress(22450, "Cool address");
            System.out.println("t1: " + p.getZip());
            System.out.println("t1: " + p.getAddress());
        });

        Thread t2 = new Thread(() -> {
            Person p = new Person(23428L);
            p.setZipAndAddress(00002, "t2-address");
            System.out.println("t2: " + p.getId()); // Should be 23428
            System.out.println("t2: " + p.getName());
            System.out.println("t2: " + p.getZip());
            System.out.println("t2: " + p.getAddress());

        });

        Thread t3 = new Thread(() -> {
            Person p = new Person();
            System.out.println("t3: " + p.getId());
        });

        Thread t4 = new Thread(() -> {
            Person p = new Person();
            System.out.println("t4: " + p.getId());
        });

        t1.start();
        t2.start();
        t3.start();
        t4.start();

    }

}