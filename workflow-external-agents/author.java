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

public class author extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        // Routes are loaded from YAML files
    }

    @BindToRegistry(lazy=true)
    public static Processor createAuthorMessage(){

        return new Processor() {
            public void process(Exchange exchange) throws Exception {

                String payload = exchange.getMessage().getBody(String.class);
                List<ChatMessage> messages = new ArrayList<>();

                String systemMessage = """
                    You are an author of a popular blog.
                    You work with a researcher who gives you research bullets that you write up into blog posts.
                    Do not make up your own research - rely on the facts provided by the researcher.
                    Do not include any images in your blog.
                    Do not include external links in your blog.
                    You may be given a previous draft of your article along with review comments.
                    If so, address all review comments in your new revision of the article.
                    Do not comment about how you addressed the review comments.
                    """;

                String userMessage = """
                            Write a blog post for the following:
                            Topic: %s
                            Research: %s
                            %s
                            %s
                        """;

                String topic    =  exchange.getVariable("topic",   String.class);
                String research =  exchange.getVariable("research",String.class);
                String draft    =  exchange.getVariable("draft",   String.class);
                String revision =  exchange.getVariable("revision",String.class);

                if(draft != null)
                    draft = "Previous Draft: " + draft;

                if(revision != null)
                    revision = "Review Comments: " + revision;

                messages.add(new SystemMessage(systemMessage));
                messages.add(new UserMessage(userMessage.formatted(topic, research, draft, revision)));

                exchange.getIn().setBody(messages);
            }
        };
    }



}
