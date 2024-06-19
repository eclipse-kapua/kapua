/*******************************************************************************
 * Copyright (c) 2019, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.commons.jersey.rest.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.eclipse.kapua.commons.security.KapuaSecurityUtils;

/**
 * This {@link Filter} cleans up the {@link Subject#getSession()} state and the {@link KapuaSecurityUtils#getSession()} after a request.
 * <p>
 * The processing of the request can leave some information on the Shiro {@link Subject} or the {@link org.eclipse.kapua.commons.security.KapuaSession} and we must clean it to avoid that a subsequent
 * request uses a {@link Thread} with dirty data inside.
 * <p>
 * Apache Shiro it is possible to define {@code noSessionCreation} on the urls mappings in the shiro.ini. Unfortunately using the {@code noSessionCreation} does not have any effect because our
 * {@link KapuaTokenAuthenticationFilter} is invoked before the {@link org.apache.shiro.web.filter.session.NoSessionCreationFilter} so it has no effect (see
 * {@link org.apache.shiro.web.filter.session.NoSessionCreationFilter javadoc}.
 *
 * @since 1.1.0
 */
public class KapuaSessionCleanupFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // No init required
    }

    @Override
    public void destroy() {
        // No destroy required
    }

    /**
     * After the invokation of {@link FilterChain#doFilter(ServletRequest, ServletResponse)} the {@link Subject} and the {@link org.eclipse.kapua.commons.security.KapuaSession} are checked and cleaned
     * accordingly.
     * <p>
     * See also {@link Filter#doFilter(ServletRequest, ServletResponse, FilterChain)} javadoc.
     *
     * @param request
     *         See {@link Filter#doFilter(ServletRequest, ServletResponse, FilterChain)} javadoc.
     * @param response
     *         See {@link Filter#doFilter(ServletRequest, ServletResponse, FilterChain)} javadoc.
     * @param chain
     *         See {@link Filter#doFilter(ServletRequest, ServletResponse, FilterChain)} javadoc.
     * @throws IOException
     *         See {@link Filter#doFilter(ServletRequest, ServletResponse, FilterChain)} javadoc.
     * @throws ServletException
     *         See {@link Filter#doFilter(ServletRequest, ServletResponse, FilterChain)} javadoc.
     * @since 1.1.0
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        try {
            chain.doFilter(request, response);
        } finally {
            Subject subject = SecurityUtils.getSubject();
            if (subject.isAuthenticated()) {
                subject.logout();
            }

            if (KapuaSecurityUtils.getSession() != null) {
                KapuaSecurityUtils.clearSession();
            }
        }
    }
}
