import java.util.Scanner;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.JsonNode;

public class Main {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        System.out.println("AGENTE INICIADO");

        while (true) {

            System.out.print("digite sua mensagem: ");
            String usuarioM = scanner.nextLine();

            if (usuarioM.equalsIgnoreCase("sair")) {
                System.out.println("Agente encerrado.");
                break;
            }

            String json = """
                    {
                        "model": "qwen3:8b",
                        "messages": [
                            {
                                "role": "user",
                                "content": "%s"
                            }
                        ],
                        "stream": false
                    }
                    """.formatted(usuarioM);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:11434/api/chat"))
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            try {

                HttpClient client = HttpClient.newHttpClient();

                HttpResponse<String> response = client.send(
                        request,
                        HttpResponse.BodyHandlers.ofString()
                );

                ObjectMapper mapper = new ObjectMapper();

                JsonNode jsonResposta = mapper.readTree(response.body());
                JsonNode mensagem = jsonResposta.get("message");
                JsonNode conteudo = mensagem.get("content");

                System.out.println(conteudo.asText());

            } catch (Exception e) {
                System.out.println("Erro ao conectar com o Ollama");
            }
        }


        scanner.close();
    }
}