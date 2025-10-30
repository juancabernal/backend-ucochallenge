package co.edu.uco.messageservice.controller;

import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.uco.messageservice.catalog.Message;
import co.edu.uco.messageservice.catalog.MessageCatalog;

@RestController
@RequestMapping("/api/v1/messages")
public class MessageController {

    @GetMapping("/{key}")
    public ResponseEntity<Message> getMessage(@PathVariable String key) {

        final Message value = MessageCatalog.getMessageValue(key);
        if (value == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .cacheControl(CacheControl.noStore().mustRevalidate())
                    .header("Pragma", "no-cache")
                    .header("Expires", "0")
                    .build();
        }

        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore().mustRevalidate())
                .header("Pragma", "no-cache")
                .header("Expires", "0")
                .body(new Message(value.getKey(), value.getValue()));
    }

    @PutMapping("/{key}")
    public ResponseEntity<Message> modifyMessage(@PathVariable String key, @RequestBody Message value) {

        // Asegura que la clave sea consistente con la URL
        Message updated = new Message(key, value.getValue());
        MessageCatalog.synchronizeMessageValue(updated);

        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore().mustRevalidate())
                .header("Pragma", "no-cache")
                .header("Expires", "0")
                .body(updated);
    }
}
