package dev.zbendhib.workflow;

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

public class Researcher extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        // Routes are loaded from YAML files
    }

    @BindToRegistry(lazy=true)
    public static Processor createResearchMessage(){

        return new Processor() {
            public void process(Exchange exchange) throws Exception {

                String payload = exchange.getMessage().getBody(String.class);
                List<ChatMessage> messages = new ArrayList<>();

                String systemMessage = """
                    You are an experienced researcher for a software company. 
                    Your job is to dig up as many facts as you can related to the topic provided. 
                    You output your research notes as bullets, one per line, starting with a hyphen. 
                    You may be given a previous set of research bullets and corresponding review comments.
                    If so, address all review comments in your new research bullets.
                    Do not make up your own facts -- use the provided fact service.
                    Do not comment about how you addressed the review comments.
                    Do not just query for the exact same words as the topic given -- be creative.

                    Always call the tool available
                    """;

                String userMessage = """
                    Provide your research on the following: 
                    Topic: %s
                    %s
                    %s
                    """;

                String topic    =  exchange.getVariable("topic",   String.class);
                String research =  exchange.getVariable("research",String.class);
                String revision =  exchange.getVariable("revision",String.class);

                if(research != null && research.length()>0 && !research.equals("ignore"))
                    research = "Previous Research Bullets: " + research;

                if(revision != null && revision.length()>0 && !research.equals("ignore"))
                    revision = "Review Comments: " + revision;

                messages.add(new SystemMessage(systemMessage));
                messages.add(new UserMessage(userMessage.formatted(topic, research, revision)));

                exchange.getIn().setBody(messages);
            }
        };
    }
}
