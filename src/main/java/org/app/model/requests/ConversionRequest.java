package org.app.model.requests;

import org.app.model.PageUserConfig;
import org.app.model.entity.User;

public record ConversionRequest(User user, PageUserConfig configs) {
}
