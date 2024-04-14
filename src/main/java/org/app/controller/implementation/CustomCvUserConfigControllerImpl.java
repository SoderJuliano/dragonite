package org.app.controller.implementation;

import org.app.controller.CustomCvUserConfigController;
import org.app.model.common.DefaultAnswer;
import org.app.services.CustomCvUserConfigService;
import org.springframework.http.ResponseEntity;

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
