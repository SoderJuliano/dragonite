package org.app.services.impl;

import org.app.Exceptions.NotFoundException;
import org.app.model.PageUserConfig;
import org.app.repository.CustomCvUserConfigRepository;
import org.app.repository.UserRepository;
import org.app.services.CustomCvUserConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.app.utils.Commons.isNull;
import static org.app.utils.LocalLog.log;

@Service
public class CustomCvUserConfigServiceImpl implements CustomCvUserConfigService {

    private final CustomCvUserConfigRepository customCvUserConfigRepository;

    private final UserRepository userRepository;

    public CustomCvUserConfigServiceImpl(CustomCvUserConfigRepository customCvUserConfigRepository,
                                         UserRepository userRepository) {
        this.customCvUserConfigRepository = customCvUserConfigRepository;
        this.userRepository = userRepository;
    }

    @Override
    public PageUserConfig getConfigs(String id) {
        return customCvUserConfigRepository.findById(id).orElseThrow(() -> new NotFoundException("Not found any config for id " + id));
    }

    @Override
    public PageUserConfig saveNewConfigs(String userId, PageUserConfig pageUserConfig) {
        log(":loz Saving new site's config for userId " + userId);
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User does not exist"));
        if(isNull(pageUserConfig)) {
            return customCvUserConfigRepository.insert(new PageUserConfig(userId));
        }
        return customCvUserConfigRepository.insert(pageUserConfig);
    }
}
