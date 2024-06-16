package org.app.services;

import org.app.model.PageUserConfig;
import org.springframework.stereotype.Service;

public interface CustomCvUserConfigService {

    public PageUserConfig getConfigs(String id);

    public PageUserConfig serNewConfigs(String userId);
}