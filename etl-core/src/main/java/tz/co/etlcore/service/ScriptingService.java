package tz.co.etlcore.service;

import groovy.lang.Binding;
import groovy.util.GroovyScriptEngine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ScriptingService {

    @Value("${scripts.dir}")
    private String scriptDir;
    public void scriptHandler(String request, String scriptName){
        try {
            Binding binding = new Binding();
            binding.setVariable("request", request);
            binding.setVariable("log", log);
            new GroovyScriptEngine(scriptDir).run( scriptName + ".groovy", binding);
        }catch (Exception e){
            log.error("Failed to process the request: {}", e.getMessage());
        }
    }
}
