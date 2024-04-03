package com.elijahwaswa.filetracker.util;

import com.elijahwaswa.filetracker.dto.UserDto;

public record ResetLinkPayload(UserDto userDto, String resetLink, String resetPasswordToken) {
}
