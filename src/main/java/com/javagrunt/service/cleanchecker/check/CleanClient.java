package com.javagrunt.service.cleanchecker.check;

import org.springframework.web.service.annotation.GetExchange;

interface CleanClient {
    @GetExchange("/capture")
    byte[] getCapture();
}
