package co.edu.uco.parametersservice.controller;

import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.uco.parametersservice.catalog.Parameter;
import co.edu.uco.parametersservice.catalog.ParameterCatalog;
import co.edu.uco.parametersservice.controller.dto.ParameterResponse;

@RestController
@RequestMapping("/api/v1/parameters")
public class ParameterController {

        @GetMapping("/{key}")
        public ResponseEntity<ParameterResponse> getParameter(@PathVariable String key) {

                final Parameter value = ParameterCatalog.getParameterValue(key);
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
                                .body(new ParameterResponse(value.getKey(), value.getValue()));

        }

        @PutMapping("/{key}")
        public ResponseEntity<ParameterResponse> modifyParameter(@PathVariable String key, @RequestBody Parameter value) {

                value.setKey(key);
                ParameterCatalog.synchronizeParameterValue(value);
                return ResponseEntity.ok()
                                .cacheControl(CacheControl.noStore().mustRevalidate())
                                .header("Pragma", "no-cache")
                                .header("Expires", "0")
                                .body(new ParameterResponse(value.getKey(), value.getValue()));

        }

}

