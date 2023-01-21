package com.purpleio.backend.exception;

import java.io.IOException;

public class InValidUrl extends IOException {
    public InValidUrl(String message) {
        super(message);
    }
}
