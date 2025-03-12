package dev.zbendhib.workflow;


import org.apache.camel.BindToRegistry;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;

import java.util.ArrayList;
import java.util.List;

public class Formatter extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        // Routes are loaded from YAML files
    }

    @BindToRegistry(lazy=true)
    public static Processor createFormatterMessage(){

        return new Processor() {
            public void process(Exchange exchange) throws Exception {

                String payload = exchange.getMessage().getBody(String.class);
                List<ChatMessage> messages = new ArrayList<>();

                String systemMessage = """
                    Your job is to format text in the format requested by the user.
                    Note that the text may already be formatted a different way (eg. in markdown) -- your job is to convert it to the format the user asks for.
                    Put a lot of effort into good layout.
                    Only provide the raw output - do not wrap it or triple-quote it.
                    """;

                String format = "format in HTML, with a cool banner at the top introducing the blog. Pay attention to spacing. Replace asterisks with HTML formatting as appropriate.";

                String userMessage = """
                            Please format the following:
                            Text: %s
                            Format: %s
                            """;

                String draft    =  exchange.getVariable("draft",   String.class);

                messages.add(new SystemMessage(systemMessage));
                messages.add(new UserMessage(userMessage.formatted(draft, format)));

                exchange.getIn().setBody(messages);
            }
        };
    }
}
