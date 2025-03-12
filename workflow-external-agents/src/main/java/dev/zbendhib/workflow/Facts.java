package dev.zbendhib.workflow;


import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;


import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class Facts  {


    @Named
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
