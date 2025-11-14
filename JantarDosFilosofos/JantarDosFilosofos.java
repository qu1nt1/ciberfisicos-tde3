import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class JantarDosFilosofos {

    static final int N = 5;
    static final Filosofo[] filosofos = new Filosofo[N];
    static final Object[] garfos = new Object[N];

    static class Filosofo implements Runnable {
        private final int id;
        private final Object garfoEsquerda;
        private final Object garfoDireita;
        private String estado = "PENSANDO";

        public Filosofo(int id, Object garfoEsquerda, Object garfoDireita) {
            this.id = id;
            this.garfoEsquerda = garfoEsquerda;
            this.garfoDireita = garfoDireita;
        }


        public String getEstado() {
            return estado;
        }

        private void pensar() throws InterruptedException {
            estado = "PENSANDO";
            Thread.sleep(500);
        }

        private void comer() throws InterruptedException {
            estado = "COMENDO";
            Thread.sleep(500);
        }

        @Override
        public void run() {
            try {
                while (true) {
                    pensar();

                    estado = "COM FOME";
                    synchronized (garfoEsquerda) {

                        Thread.sleep(200);

                        synchronized (garfoDireita) {
                            comer();
                        }
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    static void imprimirEstados() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < N; i++) {
            String estado = filosofos[i] != null ? filosofos[i].getEstado() : "INICIANDO";
            sb.append("[").append(i).append(": ").append(estado).append("] ");
        }
        System.out.println(sb.toString());
    }

    public static void main(String[] args) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(N);

        // iniciando garfo por garfo
        for (int i = 0; i < N; i++) {
            garfos[i] = new Object();
        }

        // iniciando filosofo por filosofo
        for (int i = 0; i < N; i++) {
            Object garfoEsquerda = garfos[i];
            Object garfoDireita = garfos[(i + 1) % N];
            filosofos[i] = new Filosofo(i, garfoEsquerda, garfoDireita);
            executor.execute(filosofos[i]);
        }

        for (int ciclo = 1; ciclo <= 10; ciclo++) {
            System.out.printf("Ciclo %02d -> ", ciclo);
            imprimirEstados();
            Thread.sleep(500);
        }

        executor.shutdownNow();
        System.out.println("\n--- Fim da simulação Deadlock Provado ---");
    }
}