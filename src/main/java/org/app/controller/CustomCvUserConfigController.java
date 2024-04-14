package org.app.controller;

import org.app.model.common.DefaultAnswer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/config")
public interface CustomCvUserConfigController {

    @GetMapping("/{id}")
    public ResponseEntity<DefaultAnswer> getConfigs(@PathVariable("id") String id);

    @PostMapping("new/{id}")
    public ResponseEntity<DefaultAnswer> setNewConfigs(@PathVariable("id") String id);
}
