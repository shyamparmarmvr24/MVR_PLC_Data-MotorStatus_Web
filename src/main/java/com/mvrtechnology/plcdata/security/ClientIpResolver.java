package com.mvrtechnology.plcdata.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ClientIpResolver
{
    @Value("${app.security.trust-forwarded-headers:false}")
    private boolean trustForwardedHeaders;


    public String resolve(HttpServletRequest request)
    {
        /*
         * Only trust X-Forwarded-For when your application
         * is actually behind a trusted reverse proxy.
         */
        if (trustForwardedHeaders)
        {
            String forwarded =
                    request.getHeader("X-Forwarded-For");

            if (forwarded != null &&
                    !forwarded.isBlank())
            {
                return forwarded
                        .split(",")[0]
                        .trim();
            }
        }


        String remoteAddr =
                request.getRemoteAddr();


        /*
         * IPv6 localhost -> IPv4 localhost
         */
        if ("0:0:0:0:0:0:0:1".equals(remoteAddr) ||
                "::1".equals(remoteAddr))
        {
            return "127.0.0.1";
        }


        return remoteAddr;
    }
}