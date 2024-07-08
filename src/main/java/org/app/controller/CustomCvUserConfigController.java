package org.app.controller;

import org.app.model.PageUserConfig;
import org.app.model.common.DefaultAnswer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/config")
public interface CustomCvUserConfigController {

    @GetMapping("/{id}")
    public ResponseEntity<DefaultAnswer> getConfigs(@PathVariable("id") String id);

    @PostMapping("new/{id}")
    public ResponseEntity<DefaultAnswer> saveConfigs(@PathVariable("id") String id, @RequestBody PageUserConfig pageConfig);
}
