package net.ryzen.paylinksystem.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class MessageValidationService {
    private final ResourceLoader resourceLoader;

    public void checkValidationMessage(String message, String channel) {
        List<String> validationMessageErrorDetails = new ArrayList<>();
        try {
            Resource resource = resourceLoader.getResource("classpath:schema/schema-message-"+channel+".json");
            InputStream input = resource.getInputStream();
            JSONObject jsonSchema = new JSONObject(new JSONTokener(input));
            JSONObject jsonSubject = new JSONObject(message);
            Schema schema = SchemaLoader.load(jsonSchema);
            schema.validate(jsonSubject);
        } catch (JSONException | IOException e) {
            log.warn("error: {}", e.getMessage());
            String[] elements = e.getMessage().split("\n");
            validationMessageErrorDetails.add(elements[0]);
            throw new RuntimeException();
        }
        catch (ValidationException ve) {
            log.debug("validation error: {}", ve.getMessage());
            if (!ve.getCausingExceptions().isEmpty()) {
                for (ValidationException exception : ve.getCausingExceptions()) {
                    validationMessageErrorDetails.add(exception.getMessage());
                }
            } else {
                validationMessageErrorDetails.add(ve.getMessage());
            }
        }
        log.warn(validationMessageErrorDetails.toString());
        throw new RuntimeException();
    }
}
