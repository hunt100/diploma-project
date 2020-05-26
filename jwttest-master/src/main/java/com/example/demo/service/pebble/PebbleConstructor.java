package com.example.demo.service.pebble;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.loader.StringLoader;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

@Component
@Slf4j
public class PebbleConstructor {
    private final PebbleEngine engine = new PebbleEngine.Builder().loader(new StringLoader()).strictVariables(false).build();

    @Getter
    @Setter
    private StringBuilder source;

    public String createCompleteMessage(Map<String, Object> params, String unformattedText) {
        try {
            source = new StringBuilder(unformattedText);
            PebbleTemplate template = engine.getTemplate(source.toString());
            Writer writer = new StringWriter();
            template.evaluate(writer, params);
            return writer.toString();
        } catch (IOException e) {
            log.error("createCompleteMessage() -> IOException: {}", e);
            return null;
        }
    }
}
