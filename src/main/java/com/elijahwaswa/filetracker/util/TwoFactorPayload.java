package com.elijahwaswa.filetracker.util;

import com.elijahwaswa.filetracker.model.User;

public record TwoFactorPayload(String idNumber, User user,String qrCodeBase64,boolean dbSecretExists) {
}
