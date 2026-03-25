package com.mockserver.api.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum WebHookStatus {
    DONE("DONE"),FAIL("FAIL");
    private final String status;
}
