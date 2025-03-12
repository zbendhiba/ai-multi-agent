//DEPS dev.langchain4j:langchain4j-open-ai:0.33.0


import org.apache.camel.BindToRegistry;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.PropertyInject;
import org.apache.camel.builder.RouteBuilder;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;

import static java.time.Duration.ofSeconds;

import java.util.ArrayList;
import java.util.List;

import org.apache.camel.builder.ExchangeBuilder;

public class bindings extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        // Routes are loaded from YAML files
    }

    private static String LLM_URL;

    @PropertyInject("llm.url")
    public void setLlmUrl(String url) {
        LLM_URL = url;
    }

    public static String getLlmUrl() {
        return LLM_URL;
    }

    @BindToRegistry(lazy=true)
    public static ChatLanguageModel chatModel(){

        // ChatLanguageModel model = OpenAiChatModel.builder()
        OpenAiChatModel model = OpenAiChatModel.builder()
            .apiKey(System.getenv("OPEN_API_KEY"))
            // .modelName("qwen2.5:0.5b-instruct")
            // .modelName("qwen2.5:14b-instruct")
            .modelName("gpt-4o-mini")
            // .modelName("qwen2.5:3b-instruct")
            // .modelName("qwen2.5-coder:3b")
           // .baseUrl("http://"+getLlmUrl()+"/v1/")
            .temperature(0.0)
            .timeout(ofSeconds(50000))
            // .strictTools()
            .strictTools(Boolean.TRUE)
            .logRequests(true)
            .logResponses(true)
            .build();

        return model;
    }

}
