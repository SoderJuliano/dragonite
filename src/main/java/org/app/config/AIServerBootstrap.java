package org.app.config;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class AIServerBootstrap {

    private final String basePath = "/home/user/Desktop/app/ai";
    private final String scriptPath = basePath + "/start_ai_server.sh";

    // NOME DO MODELO USADO
    private final String MODEL_NAME = "llama-3.1-8b-instruct-q4_k_m.gguf";

    // LINKS REAIS — AGORA FUNCIONA DE VERDADE
    private final String LMSTUDIO_URL =
            "https://github.com/lmstudio-ai/lmstudio/releases/latest/download/lmstudio-linux-x64-portable.tar.gz";

    private final String MODEL_URL =
            "https://huggingface.co/bartowski/Llama-3.1-8B-Instruct-GGUF/resolve/main/" + MODEL_NAME;


    @PostConstruct
    public void init() {
        try {
            if (!isPortInUse(1234)) {
                System.out.println("[AI BOOTSTRAP] IA não está rodando. Iniciando...");
                Files.createDirectories(Path.of(basePath));
                Files.createDirectories(Path.of(basePath, "models"));
                prepareEnvironment();
                createScript();
                runScript();
            } else {
                System.out.println("[AI BOOTSTRAP] IA já está rodando.");
            }
        } catch (Exception e) {
            System.err.println("[AI BOOTSTRAP] Erro ao iniciar IA: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ===============================
    // DOWNLOAD COM PROGRESS BAR
    // ===============================
    private void downloadWithProgress(String url, Path target) throws IOException {
        URLConnection connection = new URL(url).openConnection();
        int totalSize = connection.getContentLength();

        try (InputStream in = connection.getInputStream();
             OutputStream out = Files.newOutputStream(target)) {

            byte[] buffer = new byte[8192];
            long downloaded = 0;
            long lastPrinted = 0;
            long startTime = System.currentTimeMillis();

            System.out.println("\n[AI BOOTSTRAP] Baixando: " + target.getFileName());
            System.out.println("[AI BOOTSTRAP] Tamanho: " + (totalSize / 1024 / 1024) + " MB");

            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
                downloaded += read;

                long now = System.currentTimeMillis();
                if (now - lastPrinted > 200) {
                    printProgress(downloaded, totalSize, startTime);
                    lastPrinted = now;
                }
            }

            printProgress(totalSize, totalSize, startTime);
            System.out.println();
        }
    }

    private void printProgress(long downloaded, long total, long startTime) {
        int barLength = 40;
        double progress = (double) downloaded / total;
        int filled = (int) (progress * barLength);

        StringBuilder bar = new StringBuilder("[");
        bar.append("=".repeat(Math.max(0, filled)));
        bar.append(">".repeat(Math.max(0, 1)));
        bar.append(" ".repeat(Math.max(0, barLength - filled)));
        bar.append("]");

        long elapsed = (System.currentTimeMillis() - startTime) / 1000;
        long speed = elapsed == 0 ? 0 : (downloaded / 1024 / elapsed);

        System.out.print("\r" + bar + " " +
                String.format("%.1f%%", progress * 100) +
                "  " + speed + " KB/s");
    }

    // ===============================
    // PREPARAÇÃO
    // ===============================
    private void prepareEnvironment() throws Exception {

        // Download LM Studio portable
        File bin = new File(basePath + "/lmstudio");
        if (!bin.exists()) {
            downloadWithProgress(LMSTUDIO_URL, bin.toPath());
            bin.setExecutable(true);
        }

        // Download do modelo GGUF
        File model = new File(basePath + "/models/" + MODEL_NAME);
        if (!model.exists()) {
            downloadWithProgress(MODEL_URL, model.toPath());
        }
    }

    // ===============================
    // SCRIPT DE EXECUÇÃO
    // ===============================
    private void createScript() throws IOException {
        String script = """
            #!/bin/bash
            cd "$(dirname "$0")"
            echo "[IA] Iniciando LM Studio Server portable..."
            ./lmstudio --server --port 1234 --model ./models/%s
        """.formatted(MODEL_NAME);

        Files.writeString(Path.of(scriptPath), script);
        new ProcessBuilder("chmod", "+x", scriptPath).start();
    }

    private void runScript() throws IOException {
        new ProcessBuilder("/bin/bash", scriptPath)
                .directory(new File(basePath))
                .inheritIO()
                .start();

        System.out.println("[AI BOOTSTRAP] IA iniciada em segundo plano.");
    }

    private boolean isPortInUse(int port) {
        try (Socket ignored = new Socket("localhost", port)) {
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
