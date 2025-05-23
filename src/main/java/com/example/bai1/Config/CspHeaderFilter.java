package com.example.bai1.Config;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;

public class CspHeaderFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        // Ghi đè CSP header
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        // CSP cho VNPAY + các CDN cần thiết
        // Chỉ set CSP header nếu chưa có
        if (httpResponse.getHeader("Content-Security-Policy") == null) {
            httpResponse.setHeader("Content-Security-Policy",
                    "default-src 'self'; "
                            + "script-src 'self' https://sandbox.vnpayment.vn https://vnpayment.vn https://cdn.jsdelivr.net https://code.jquery.com https://cdn.tailwindcss.com https://unpkg.com 'unsafe-inline' 'unsafe-eval'; "
                            + "style-src 'self' https://sandbox.vnpayment.vn https://vnpayment.vn https://cdnjs.cloudflare.com https://cdn.tailwindcss.com https://unpkg.com 'unsafe-inline'; "
                            + "img-src 'self' data: https://sandbox.vnpayment.vn https://vnpayment.vn https://unpkg.com; "
                            + "font-src 'self' https://cdnjs.cloudflare.com https://fonts.gstatic.com https://unpkg.com; "
                            + "connect-src 'self' https://sandbox.vnpayment.vn https://vnpayment.vn https://unpkg.com; "
                            + "frame-src https://sandbox.vnpayment.vn https://vnpayment.vn;");
        }

        chain.doFilter(request, response);
    }

}
