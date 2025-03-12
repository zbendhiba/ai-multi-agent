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

public class Facts extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        // Routes are loaded from YAML files
    }

    @BindToRegistry(lazy=true)
    public static Processor createFactsMessage(){

        return new Processor() {
            public void process(Exchange exchange) throws Exception {

                String payload = exchange.getMessage().getBody(String.class);
                List<ChatMessage> messages = new ArrayList<>();

                String systemMessage = """
                    You are a fact service. 
                    Your job is to provide a list of facts about a topic.
                    Provide each fact on its own line, starting with a hyphen, with no additional formatting.
                    """;

                String userMessage = """
                            Please provide facts about the following topic: : %s
                            """;

                String topic    =  exchange.getMessage().getHeader("topic",   String.class);

                messages.add(new SystemMessage(systemMessage));
                messages.add(new UserMessage(userMessage.formatted(topic)));

                exchange.getIn().setBody(messages);
            }
        };
    }
}
