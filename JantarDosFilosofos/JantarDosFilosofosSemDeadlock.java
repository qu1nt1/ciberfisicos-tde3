import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class JantarDosFilosofosSemDeadlock {

    static final int N = 5;
    // O array agora é da classe "FilosofoCorrigido"
    static final FilosofoCorrigido[] filosofos = new FilosofoCorrigido[N];
    static final Object[] garfos = new Object[N];

    // Esta é a classe da SOLUÇÃO
    static class FilosofoCorrigido implements Runnable {
        private final int id;
        private final Object primeiroGarfo; // O de menor ID
        private final Object segundoGarfo;  // O de maior ID
        private String estado = "PENSANDO";

        public FilosofoCorrigido(int id, Object garfo1, Object garfo2) {
            this.id = id;

            // --- A LÓGICA DA HIERARQUIA (A SOLUÇÃO) ---
            // Compara o "ID" (hashcode) dos dois garfos
            if (System.identityHashCode(garfo1) < System.identityHashCode(garfo2)) {
                this.primeiroGarfo = garfo1;
                this.segundoGarfo = garfo2;
            } else {
                this.primeiroGarfo = garfo2; // Inverte a ordem
                this.segundoGarfo = garfo1;
            }
        }

        // Método para o "observador" (main) ler o estado
        public String getEstado() {
            return estado;
        }

        private void pensar() throws InterruptedException {
            estado = "PENSANDO";
            Thread.sleep(500); // Tempo fixo pensando
        }

        private void comer() throws InterruptedException {
            estado = "COMENDO";
            Thread.sleep(500); // Tempo fixo comendo
        }

        @Override
        public void run() {
            try {
                while (true) {
                    pensar();

                    estado = "COM FOME";

                    // --- A LÓGICA CORRIGIDA ---
                    // 1. Pega o "primeiroGarfo" (menor ID)
                    synchronized (primeiroGarfo) {

                        // A "armadilha" ainda existe, mas não causa deadlock
                        Thread.sleep(200);

                        // 2. Pega o "segundoGarfo" (maior ID)
                        synchronized (segundoGarfo) {
                            comer();
                        }
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    // Método do "Observador" para imprimir os estados (idêntico)
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

        // Inicializa garfos (idêntico)
        for (int i = 0; i < N; i++) {
            garfos[i] = new Object();
        }

        // Inicializa filósofos (chama a classe "FilosofoCorrigido")
        for (int i = 0; i < N; i++) {
            Object garfoEsquerda = garfos[i];
            Object garfoDireita = garfos[(i + 1) % N];
            // --- ÚNICA MUDANÇA NO MAIN ---
            filosofos[i] = new FilosofoCorrigido(i, garfoEsquerda, garfoDireita);
            executor.execute(filosofos[i]);
        }

        // Ciclos de monitoramento do estado (idêntico)
        for (int ciclo = 1; ciclo <= 30; ciclo++) {
            System.out.printf("Ciclo %02d -> ", ciclo);
            imprimirEstados();
            Thread.sleep(500); // O "ciclo" do observador
        }

        executor.shutdownNow(); // Força o fim da simulação
        System.out.println("\n--- Fim da simulação (Solução Provada) ---");
    }
}