package org.app.services;

import org.app.model.PageUserConfig;

public interface CustomCvUserConfigService {

    public PageUserConfig getConfigs(String id);

    public PageUserConfig saveNewConfigs(String userId, PageUserConfig pageUserConfig);
}