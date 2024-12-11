/*******************************************************************************
 * Copyright (c) 2016, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.service.authentication.token.shiro;

import java.util.Date;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.model.AbstractKapuaEntity;
import org.eclipse.kapua.commons.model.AbstractKapuaUpdatableEntity;
import org.eclipse.kapua.commons.model.id.IdGenerator;
import org.eclipse.kapua.commons.model.id.KapuaEid;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.authentication.token.AccessToken;

/**
 * {@link AccessToken} implementation.
 *
 * @since 1.0.0
 */

@Entity(name = "AccessToken")
@Table(name = "atht_access_token")
public class AccessTokenImpl extends AbstractKapuaUpdatableEntity implements AccessToken {

    private static final long serialVersionUID = -6003387376828196787L;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "eid", column = @Column(name = "user_id", updatable = false, nullable = false))
    })
    private KapuaEid userId;

    @Basic
    @Column(name = "token_id", updatable = false, nullable = false)
    private String tokenId;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "expires_on", nullable = false)
    private Date expiresOn;

    @Basic
    @Column(name = "refresh_token", updatable = false, nullable = false)
    private String refreshToken;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "refresh_expires_on", nullable = false)
    private Date refreshExpiresOn;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "invalidated_on", nullable = true)
    private Date invalidatedOn;

    @Transient
    private String trustkey;

    @Basic
    @Column(name = "token_identifier", updatable = false, nullable = false)
    private String tokenIdentifier;

    /**
     * Constructor.
     *
     * @since 1.0.0
     */
    public AccessTokenImpl() {
        super();
    }

    /**
     * Constructor.
     *
     * @param userId    user identifier
     * @param scopeId   scope identifier
     * @param tokenId   token identifier
     * @param expiresOn token expiration {@link Date}
     * @since 1.0.0
     */
    public AccessTokenImpl(KapuaId scopeId, KapuaId userId, String tokenId, Date expiresOn, String refreshToken, Date refreshExpiresOn, String tokenIdentifier) {
        super(scopeId);

        setUserId(userId);
        setTokenId(tokenId);
        setExpiresOn(expiresOn);
        setRefreshToken(refreshToken);
        setRefreshExpiresOn(refreshExpiresOn);
        setTokenIdentifier(tokenIdentifier);
    }

    public AccessTokenImpl(KapuaId scopeId) {
        super(scopeId);
    }

    /**
     * Clone constructor.
     *
     * @param accessToken
     * @throws KapuaException
     * @since 1.1.0
     */
    public AccessTokenImpl(AccessToken accessToken) throws KapuaException {
        super(accessToken);

        setUserId(accessToken.getUserId());
        setTokenId(accessToken.getTokenId());
        setExpiresOn(accessToken.getExpiresOn());
        setRefreshToken(accessToken.getRefreshToken());
        setRefreshExpiresOn(accessToken.getRefreshExpiresOn());
        setInvalidatedOn(accessToken.getInvalidatedOn());
        setTokenIdentifier(accessToken.getTokenId());
    }


    @Override
    public KapuaId getUserId() {
        return userId;
    }

    @Override
    public void setUserId(KapuaId userId) {
        this.userId = KapuaEid.parseKapuaId(userId);
    }

    @Override
    public String getTokenId() {
        return tokenId;
    }

    @Override
    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    @Override
    public Date getExpiresOn() {
        return expiresOn;
    }

    @Override
    public void setExpiresOn(Date expiresOn) {
        this.expiresOn = expiresOn;
    }

    /**
     * The {@link AbstractKapuaUpdatableEntity#prePersistsAction()} is overridden because the property {@link AbstractKapuaEntity#getCreatedBy()}
     * must be set to the current userId instead of the user in session, which is not set at the time of the creation of this {@link AccessToken}.
     *
     * @since 1.0.0
     */
    @Override
    protected void prePersistsAction() {
        setId(new KapuaEid(IdGenerator.generate()));
        setCreatedBy(userId);
        setCreatedOn(new Date());
        setModifiedBy(getCreatedBy());
        setModifiedOn(getCreatedOn());
    }

    @Override
    public String getRefreshToken() {
        return refreshToken;
    }

    @Override
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    @Override
    public Date getRefreshExpiresOn() {
        return refreshExpiresOn;
    }

    @Override
    public void setRefreshExpiresOn(Date refreshExpiresOn) {
        this.refreshExpiresOn = refreshExpiresOn;

    }

    @Override
    public Date getInvalidatedOn() {
        return invalidatedOn;
    }

    @Override
    public void setInvalidatedOn(Date invalidatedOn) {
        this.invalidatedOn = invalidatedOn;
    }

    @Override
    public String getTrustKey() {
        return trustkey;
    }

    @Override
    public void setTrustKey(String trustKey) {
        this.trustkey = trustKey;
    }

    @Override
    public String getTokenIdentifier() {
        return tokenIdentifier;
    }

    @Override
    public void setTokenIdentifier(String tokenId) {
        this.tokenIdentifier = tokenId;
    }
}
