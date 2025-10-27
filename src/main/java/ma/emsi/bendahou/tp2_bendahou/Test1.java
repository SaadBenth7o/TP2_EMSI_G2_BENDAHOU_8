package ma.emsi.bendahou.tp2_bendahou;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import java.util.Scanner;

public class Test1 {
    public static void main(String[] args) {
        String cle = System.getenv("GEMINI_KEY");

        ChatModel model = GoogleAiGeminiChatModel.builder()
                .apiKey(cle)
                .modelName("gemini-2.5-flash")
                .temperature(0.6)
                .build();

        Scanner scanner = new Scanner(System.in);
        System.out.println("💬 Chat avec Gemini (tape 'exit' pour quitter)");

        while (true) {
            System.out.print("\nVous : ");
            String question = scanner.nextLine();

            if (question.equalsIgnoreCase("exit")) {
                System.out.println("👋 Fin du chat !");
                break;
            }

            String response = model.chat(question);
            System.out.println("🤖 Gemini : " + response);
        }

        scanner.close();
    }
}
