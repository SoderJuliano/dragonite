package org.app.services.impl;

import org.app.Exceptions.NotFoundException;
import org.app.model.PageUserConfig;
import org.app.repository.CustomCvUserConfigRepository;
import org.app.repository.UserRepository;
import org.app.services.CustomCvUserConfigService;
import org.springframework.beans.factory.annotation.Autowired;

public class CustomCvUserConfigServiceImpl implements CustomCvUserConfigService {

    @Autowired
    private CustomCvUserConfigRepository customCvUserConfigRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public PageUserConfig getConfigs(String id) {
        return customCvUserConfigRepository.findById(id).orElseThrow(() -> new NotFoundException("Not found any config for id " + id));
    }

    @Override
    public PageUserConfig serNewConfigs(String userId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User does not exist"));
        return customCvUserConfigRepository.insert(new PageUserConfig(userId));
    }
}
