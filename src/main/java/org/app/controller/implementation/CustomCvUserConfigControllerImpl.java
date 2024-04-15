package org.app.controller.implementation;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.app.controller.CustomCvUserConfigController;
import org.app.model.common.DefaultAnswer;
import org.app.services.CustomCvUserConfigService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Configuration for custom-cv-online users", description = "Endpoints for users change the stander page configs of https://custom-cv-online.netlify.app.")
public class CustomCvUserConfigControllerImpl implements CustomCvUserConfigController {

    private CustomCvUserConfigService customCvUserConfigService;

    @Override
    public ResponseEntity<DefaultAnswer> getConfigs(String id) {
        return ResponseEntity.status(200).body(new DefaultAnswer(customCvUserConfigService.getConfigs(id)));
    }

    @Override
    public ResponseEntity<DefaultAnswer> setNewConfigs(String id) {
        return ResponseEntity.status(201).body(new DefaultAnswer(customCvUserConfigService.serNewConfigs(id)));
    }
}
