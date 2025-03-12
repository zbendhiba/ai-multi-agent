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
public class Reviewer  {

    @Named
    public static Processor createReviewerMessage(){

        return new Processor() {
            public void process(Exchange exchange) throws Exception {

                String payload = exchange.getMessage().getBody(String.class);
                List<ChatMessage> messages = new ArrayList<>();

                String systemMessage = """
                    You are a blog reviewer for a software company.
                    It is your job to check over draft blogs and provide comments to the researcher and/or the author. 
                    The researcher does all the research for the blog, and the author writes up the blog based on the research bullets. 
                    Put each of your comments on a separate line. 
                    Comments must not include line breaks - put one comment on one line, regardless of how long that is.
                    If your comment is directed at the researcher, start the line with '- RESEARCHER: ' 
                    If your comment is directed at the author, start the line with '- AUTHOR: ' 
                    For example, given the following draft blog: 
                    ---
                    Topic: how we know the earth is not flat. 
                    Draft: The earth is a big round ball that spins around the sun. 
                    ---
                    You could say: 
                    - RESEARCHER: provide more technical details about how it is known that the earth is round. 
                    - RESEARCHER: provide details about the history of astronomy that help explain the topic. 
                    - AUTHOR: use more precise language.
                    - AUTHOR: change 'spins around the sun' to 'orbits the sun'
                    """;

                String userMessage = """
                            Read the following draft blog post and provide your comments: 
                            Topic: %s
                            Draft: %s
                            """;

                String topic    =  exchange.getVariable("topic",   String.class);
                String draft    =  exchange.getVariable("draft",   String.class);

                messages.add(new SystemMessage(systemMessage));
                messages.add(new UserMessage(userMessage.formatted(topic, draft)));

                exchange.getIn().setBody(messages);
            }
        };
    }
}
