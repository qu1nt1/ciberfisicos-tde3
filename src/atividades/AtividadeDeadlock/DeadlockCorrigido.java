package AtividadeDeadlock;

public class DeadlockCorrigido {

    static final Object LOCK_A = new Object();
    static final Object LOCK_B = new Object();

    public static void main(String[] args) {

        Thread t1 = new Thread(() -> {
            synchronized (LOCK_A) {
                System.out.println("T1: Segurando LOCK_A...");
                dormir(100);
                System.out.println("T1: Esperando por LOCK_B...");

                synchronized (LOCK_B) {
                    System.out.println("T1: Segurando LOCK_A e LOCK_B.");
                }
            }
            System.out.println("T1 concluiu");
        });

        Thread t2 = new Thread(() -> {

            synchronized (LOCK_A) {
                System.out.println("T2: Segurando LOCK_A...");
                dormir(100);
                System.out.println("T2: Esperando por LOCK_B...");

                synchronized (LOCK_B) {
                    System.out.println("T2: Segurando LOCK_A e LOCK_B.");
                }
            }

            System.out.println("T2 concluiu");
        });

        t1.start();
        t2.start();

        System.out.println("Iniciado... (Nao vai travar)");
    }

    static void dormir(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}