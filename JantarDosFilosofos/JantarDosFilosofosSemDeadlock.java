import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class JantarDosFilosofosSemDeadlock {
    private static final int N = 5;
    private static final Lock[] garfos = new ReentrantLock[N];
    private static final Estado[] estados = new Estado[N];
    private static volatile int ciclo = 0;
    private static final int CICLOS_MAX = 100;

    private enum Estado {
        PENSANDO, COM_FOME, COMENDO
    }

    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < N; i++) {
            garfos[i] = new ReentrantLock();
            estados[i] = Estado.PENSANDO;
        }

        ExecutorService pool = Executors.newFixedThreadPool(N);
        for (int i = 0; i < N; i++) {
            final int id = i;
            pool.execute(() -> filosofo(id));
        }

        while (ciclo < CICLOS_MAX) {
            imprimirEstados();
            Thread.sleep(200);
            ciclo++;
        }

        pool.shutdownNow();
        pool.awaitTermination(1, TimeUnit.SECONDS);
        System.out.println("Simulação encerrada.");
    }

    private static void filosofo(int id) {
        int garfoEsq = id;
        int garfoDir = (id + 1) % N;

        int primeiro = Math.min(garfoEsq, garfoDir);
        int segundo = Math.max(garfoEsq, garfoDir);

        try {
            while (!Thread.currentThread().isInterrupted()) {
                pensar(id);
                estados[id] = Estado.COM_FOME;

                garfos[primeiro].lock();
                garfos[segundo].lock();

                comer(id);

                garfos[segundo].unlock();
                garfos[primeiro].unlock();
            }
        } catch (Exception e) {
            Thread.currentThread().interrupt();
        }
    }

    private static void pensar(int id) throws InterruptedException {
        estados[id] = Estado.PENSANDO;
        Thread.sleep(100 + (int)(Math.random() * 100));
    }

    private static void comer(int id) throws InterruptedException {
        estados[id] = Estado.COMENDO;
        Thread.sleep(100 + (int)(Math.random() * 100));
    }

    private static synchronized void imprimirEstados() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Ciclo %d -> ", ciclo));
        for (int i = 0; i < N; i++) {
            sb.append(String.format("[%d: %s] ", i, estados[i]));
        }
        System.out.println(sb);
    }
}
