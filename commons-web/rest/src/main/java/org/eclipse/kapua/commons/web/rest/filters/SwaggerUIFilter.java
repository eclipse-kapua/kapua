/*******************************************************************************
 * Copyright (c) 2022 Eurotech and/or its affiliates and others
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Eurotech - initial API and implementation
 *******************************************************************************/
package org.eclipse.kapua.commons.web.rest.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SwaggerUIFilter implements Filter {

    private final Logger logger =
            LoggerFactory.getLogger(SwaggerUIFilter.class);
    private boolean swaggerEnable;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        swaggerEnable = Boolean.parseBoolean(System.getenv("SWAGGER"));
        logger.info("Initializing with swagger-enable: {}", swaggerEnable);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain)
            throws IOException, ServletException {
        if (!swaggerEnable) {
            HttpServletResponse httpResponse = WebUtils.toHttp(response);
            httpResponse.sendError(
                    HttpServletResponse.SC_NOT_FOUND, "Swagger UI disabled");
            return;
        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
