package co.edu.uco.messageservice.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.edu.uco.messageservice.catalog.Message;
import co.edu.uco.messageservice.catalog.MessageCatalog;
import co.edu.uco.messageservice.controller.dto.MessageResponse;

@RestController
@RequestMapping("/api/v1/messages")
public class MessageController {

        @GetMapping("/{key}")
        public ResponseEntity<MessageResponse> getMessage(@PathVariable String key,
                        @RequestParam Map<String, String> parameters) {

                final Message value = MessageCatalog.getMessageValue(key, parameters);
                if (value == null) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
                }

                return ResponseEntity.ok(new MessageResponse(value.getKey(), value.getValue()));

        }

        @PutMapping("/{key}")
        public ResponseEntity<MessageResponse> modifyMessage(@PathVariable String key, @RequestBody Message value) {

                value.setKey(key);
                MessageCatalog.synchronizeMessageValue(value);
                return ResponseEntity.ok(new MessageResponse(value.getKey(), value.getValue()));

        }

}
