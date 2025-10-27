package ma.emsi.bendahou.tp2_bendahou;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import java.util.Scanner;

public class Test5 {

    public static void main(String[] args) {

        // Récupération de la clé API stockée dans les variables d’environnement
        String llmKey = System.getenv("GEMINI_KEY");

        // Initialisation du modèle Gemini avec une température faible pour garantir des réponses cohérentes
        ChatModel model = GoogleAiGeminiChatModel.builder()
                .apiKey(llmKey)
                .modelName("gemini-2.5-flash")
                .temperature(0.3)
                .build();

        // Chargement d’un document local et création d’un magasin d’embeddings en mémoire
        String nomDocument = "ml.pdf";
        Document document = FileSystemDocumentLoader.loadDocument(nomDocument);
        EmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();

        // Transformation du contenu du document en vecteurs d’embeddings et ajout dans la base vectorielle
        EmbeddingStoreIngestor.ingest(document, embeddingStore);

        // Construction d’un assistant conversationnel avec :
        // - un modèle de chat (LLM)
        // - une mémoire limitée à 10 messages
        // - un système RAG basé sur la recherche d’embeddings pertinents
        Assistant assistant = AiServices.builder(Assistant.class)
                .chatModel(model)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .contentRetriever(EmbeddingStoreContentRetriever.from(embeddingStore))
                .build();

        // Lancement de la boucle de conversation interactive
        conversationAvec(assistant);
    }

    private static void conversationAvec(Assistant assistant) {
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.println("================================");
                System.out.println("Comment puis-je vous aider ? ");
                String question = scanner.nextLine();

                if (question.isBlank()) continue;
                if ("end".equalsIgnoreCase(question)) break;

                // Envoi de la question à l’assistant et affichage de la réponse
                System.out.println("================================");
                String reponse = assistant.chat(question);
                System.out.println("Assistant : " + reponse);
                System.out.println("==================================================");
            }
        }
    }

    // Interface décrivant le comportement d’un assistant conversationnel
    interface Assistant {
        String chat(String userMessage);
    }

}
