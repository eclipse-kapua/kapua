/*******************************************************************************
 * Copyright (c) 2017, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.service.endpoint.internal;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.eclipse.kapua.KapuaEntityNotFoundException;
import org.eclipse.kapua.KapuaEntityUniquenessException;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.model.domains.Domains;
import org.eclipse.kapua.commons.security.KapuaSecurityUtils;
import org.eclipse.kapua.commons.util.ArgumentValidator;
import org.eclipse.kapua.commons.util.CommonsValidationRegex;
import org.eclipse.kapua.model.KapuaEntityAttributes;
import org.eclipse.kapua.model.domain.Actions;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.model.query.predicate.AndPredicate;
import org.eclipse.kapua.model.query.predicate.AttributePredicate.Operator;
import org.eclipse.kapua.model.query.predicate.QueryPredicate;
import org.eclipse.kapua.service.account.Account;
import org.eclipse.kapua.service.account.AccountService;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.permission.Permission;
import org.eclipse.kapua.service.endpoint.EndpointInfo;
import org.eclipse.kapua.service.endpoint.EndpointInfoAttributes;
import org.eclipse.kapua.service.endpoint.EndpointInfoCreator;
import org.eclipse.kapua.service.endpoint.EndpointInfoListResult;
import org.eclipse.kapua.service.endpoint.EndpointInfoQuery;
import org.eclipse.kapua.service.endpoint.EndpointInfoRepository;
import org.eclipse.kapua.service.endpoint.EndpointInfoService;
import org.eclipse.kapua.storage.TxContext;
import org.eclipse.kapua.storage.TxManager;

/**
 * {@link EndpointInfoService} implementation.
 *
 * @since 1.0.0
 */
@Singleton
public class EndpointInfoServiceImpl
        implements EndpointInfoService {

    private final AuthorizationService authorizationService;
    private final EndpointInfoRepository repository;
    private final AccountService accountService;
    private final TxManager txManager;

    @Inject
    public EndpointInfoServiceImpl(
            AccountService accountService,
            AuthorizationService authorizationService,
            EndpointInfoRepository endpointInfoRepository,
            TxManager txManager) {
        this.accountService = accountService;
        this.authorizationService = authorizationService;
        this.repository = endpointInfoRepository;
        this.txManager = txManager;
    }

    private static final String ENDPOINT_INFO_CREATOR_SCHEMA = "endpointInfoCreator.schema";
    private static final String ENDPOINT_INFO_CREATOR_DNS = "endpointInfoCreator.dns";
    private static final String ENDPOINT_INFO_SCHEMA = "endpointInfo.schema";
    private static final String ENDPOINT_INFO_DNS = "endpointInfo.dns";

    @Override
    public EndpointInfo create(EndpointInfoCreator endpointInfoCreator)
            throws KapuaException {
        ArgumentValidator.notNull(endpointInfoCreator, "endpointInfoCreator");
        ArgumentValidator.notNull(endpointInfoCreator.getScopeId(), "endpointInfoCreator.scopeId");
        ArgumentValidator.notNull(endpointInfoCreator.getEndpointType(), "endpointInfoCreator.endpointType");

        ArgumentValidator.notEmptyOrNull(endpointInfoCreator.getSchema(), ENDPOINT_INFO_CREATOR_SCHEMA);
        ArgumentValidator.match(endpointInfoCreator.getSchema(), CommonsValidationRegex.URI_SCHEME, ENDPOINT_INFO_CREATOR_SCHEMA);
        ArgumentValidator.lengthRange(endpointInfoCreator.getSchema(), 1, 64, ENDPOINT_INFO_CREATOR_SCHEMA);

        ArgumentValidator.notEmptyOrNull(endpointInfoCreator.getDns(), ENDPOINT_INFO_CREATOR_DNS);
        ArgumentValidator.match(endpointInfoCreator.getDns(), CommonsValidationRegex.URI_DNS, ENDPOINT_INFO_CREATOR_DNS);
        ArgumentValidator.lengthRange(endpointInfoCreator.getDns(), 1, 1024, ENDPOINT_INFO_CREATOR_DNS);

        ArgumentValidator.notNegative(endpointInfoCreator.getPort(), "endpointInfoCreator.port");
        ArgumentValidator.numRange(endpointInfoCreator.getPort(), 1, 65535, "endpointInfoCreator.port");
        // Check Access
        KapuaId scopeIdPermission = endpointInfoCreator.getEndpointType().equals(EndpointInfo.ENDPOINT_TYPE_CORS) ?
                endpointInfoCreator.getScopeId() : null;
        authorizationService.checkPermission(
                new Permission(Domains.ENDPOINT_INFO, Actions.write, scopeIdPermission)
        );
        // Check duplicate endpoint
        checkDuplicateEndpointInfo(
                endpointInfoCreator.getScopeId(),
                null,
                endpointInfoCreator.getSchema(),
                endpointInfoCreator.getDns(),
                endpointInfoCreator.getPort(),
                endpointInfoCreator.getEndpointType());

        // Do create
        final EndpointInfo endpointInfo = new EndpointInfoImpl(endpointInfoCreator.getScopeId());
        endpointInfo.setSchema(endpointInfoCreator.getSchema());
        endpointInfo.setDns(endpointInfoCreator.getDns());
        endpointInfo.setPort(endpointInfoCreator.getPort());
        endpointInfo.setSecure(endpointInfoCreator.getSecure());
        endpointInfo.setUsages(endpointInfoCreator.getUsages());
        endpointInfo.setEndpointType(endpointInfoCreator.getEndpointType());

        return txManager.execute(tx -> repository.create(tx, endpointInfo));
    }

    @Override
    public EndpointInfo update(EndpointInfo endpointInfo) throws KapuaException {
        ArgumentValidator.notNull(endpointInfo, "endpointInfo");
        ArgumentValidator.notNull(endpointInfo.getScopeId(), "endpointInfo.scopeId");

        ArgumentValidator.notEmptyOrNull(endpointInfo.getSchema(), ENDPOINT_INFO_SCHEMA);
        ArgumentValidator.match(endpointInfo.getSchema(), CommonsValidationRegex.URI_SCHEME, ENDPOINT_INFO_SCHEMA);
        ArgumentValidator.lengthRange(endpointInfo.getSchema(), 1, 64, ENDPOINT_INFO_SCHEMA);

        ArgumentValidator.notEmptyOrNull(endpointInfo.getDns(), ENDPOINT_INFO_DNS);
        ArgumentValidator.match(endpointInfo.getDns(), CommonsValidationRegex.URI_DNS, ENDPOINT_INFO_DNS);
        ArgumentValidator.lengthRange(endpointInfo.getDns(), 1, 1024, ENDPOINT_INFO_DNS);

        ArgumentValidator.notNegative(endpointInfo.getPort(), "endpointInfo.port");
        ArgumentValidator.numRange(endpointInfo.getPort(), 1, 65535, "endpointInfo.port");
        // Check Access
        KapuaId scopeIdPermission = endpointInfo.getEndpointType().equals(EndpointInfo.ENDPOINT_TYPE_CORS) ?
                endpointInfo.getScopeId() : null;
        authorizationService.checkPermission(
                new Permission(Domains.ENDPOINT_INFO, Actions.write, scopeIdPermission)
        );
        // Check duplicate endpoint
        checkDuplicateEndpointInfo(
                endpointInfo.getScopeId(),
                endpointInfo.getId(),
                endpointInfo.getSchema(),
                endpointInfo.getDns(),
                endpointInfo.getPort(),
                endpointInfo.getEndpointType());

        // Do update
        return txManager.execute(tx -> repository.update(tx, endpointInfo));
    }

    @Override
    public void delete(KapuaId scopeId, KapuaId endpointInfoId) throws KapuaException {
        ArgumentValidator.notNull(scopeId, "scopeId");
        ArgumentValidator.notNull(endpointInfoId, "endpointInfoId");
        // Check Access
        txManager.execute(tx -> {
            KapuaId scopeIdPermission = null;
            if (repository.find(tx, scopeId, endpointInfoId)
                    .map(ei -> ei.getEndpointType())
                    .map(et -> EndpointInfo.ENDPOINT_TYPE_CORS.equals(et))
                    .orElse(false)) {
                scopeIdPermission = scopeId;
            }

            authorizationService.checkPermission(
                    new Permission(Domains.ENDPOINT_INFO, Actions.delete, scopeIdPermission)
            );
            // Do delete
            return repository.delete(tx, scopeId, endpointInfoId);
        });
    }

    @Override
    public EndpointInfo find(KapuaId scopeId, KapuaId endpointInfoId)
            throws KapuaException {
        ArgumentValidator.notNull(scopeId, "scopeId");
        ArgumentValidator.notNull(endpointInfoId, "endpointInfoId");
        // Check Access
        return txManager.execute(tx -> {
            authorizationService.checkPermission(
                    new Permission(Domains.ENDPOINT_INFO, Actions.read, scopeId)
            );
            EndpointInfo endpointInfoToFind = repository.find(tx, KapuaId.ANY, endpointInfoId)
                    .orElseThrow(() -> new KapuaEntityNotFoundException(EndpointInfo.TYPE, endpointInfoId)); // search the endpoint in any scope

            if (endpointInfoToFind.getScopeId().equals(scopeId)) { //found in the specified scope, search finish here
                return endpointInfoToFind;
            }
            //found but in another scope...is defined in the scope of the first Account that has defined endpoints? (proceeding upwards)
            String type = endpointInfoToFind.getEndpointType();
            //now find the endpoints of the search type that I can use (aka, the nearest proceeding upwards in Accounts hierarchy)
            EndpointInfoQuery query = new EndpointInfoQuery(scopeId);
            EndpointInfoListResult nearestUsableEndpoints = doQuery(tx, query, type);

            if (nearestUsableEndpoints.isEmpty() || !nearestUsableEndpoints.getFirstItem().getScopeId()
                    .equals(endpointInfoToFind.getScopeId())) { //the second condition is equivalent to verify if the searched endpoint is in this list
                throw new KapuaEntityNotFoundException(EndpointInfo.TYPE, endpointInfoId);
            } else {
                return endpointInfoToFind;
            }
        });
    }

    @Override
    public long count(KapuaQuery query) throws KapuaException {
        return txManager.execute(txContext -> doCount(txContext, query, EndpointInfo.ENDPOINT_TYPE_RESOURCE));
    }

    @Override
    public long count(KapuaQuery query, String section)
            throws KapuaException {
        return txManager.execute(txContext -> doCount(txContext, query, section));
    }

    private Long doCount(TxContext txContext, KapuaQuery query, String section) throws KapuaException {
        ArgumentValidator.notNull(query, "query");
        // Check Access
        authorizationService.checkPermission(
                new Permission(Domains.ENDPOINT_INFO, Actions.read, query.getScopeId())
        );
        return traverse(
                txContext,
                query,
                section,
                (tx, q) -> repository.count(tx, q),
                a -> a == 0);
    }

    @Override
    public EndpointInfoListResult query(KapuaQuery query) throws KapuaException {
        return txManager.execute(tx -> doQuery(tx, query, EndpointInfo.ENDPOINT_TYPE_RESOURCE));
    }

    @Override
    public EndpointInfoListResult query(KapuaQuery query, String section) throws KapuaException {
        return txManager.execute(tx -> doQuery(tx, query, section));
    }

    private EndpointInfoListResult doQuery(TxContext tx, KapuaQuery query, String section) throws KapuaException {
        ArgumentValidator.notNull(query, "query");
        //
        // Check Access
        authorizationService.checkPermission(
                new Permission(Domains.ENDPOINT_INFO, Actions.read, query.getScopeId())
        );
        return traverse(
                tx,
                query,
                section,
                (txContext, q) -> repository.query(txContext, q),
                a -> a.isEmpty()
        );
    }

    //
    // Private methods and interfaces
    //

    //this interface is created solely because java complains if you pass lambdas throwing checked exception
    //to overcome this, I created this interface which is a custom form of a Bifunction throwing the checked KapuaException
    @FunctionalInterface
    public interface kapuaBiFunction<A, B, R> {

        R apply(A input1, B input2) throws KapuaException;
    }

    /**
     * Traverse the account hierarchy bottom-up to search for {@link EndpointInfo} respecting the given query, performing for each layer the given queryExecutor until the given isEmptyResult dictates
     * to stop OR when endpoints of the same section are found in one layer In other terms, this method applies a given function to the "nearest usable endpoints", aka the ones that I see in a given
     * scopeID
     *
     * @param query
     *         The query to filter the {@link EndpointInfo}s.
     * @param section
     *         section of {@link EndpointInfo} where we want to search the information
     * @param queryExecutor
     *         function to apply at each layer
     * @param isEmptyResult
     *         predicate that dictates to stop the traversal when false
     */
    protected <R> R traverse(
            TxContext tx,
            KapuaQuery query,
            String section,
            kapuaBiFunction<TxContext, KapuaQuery, R> queryExecutor,
            Predicate<R> isEmptyResult)
            throws KapuaException {

        addSectionToPredicate(query, section);

        //
        // Do Query
        R res = queryExecutor.apply(tx, query);

        if (isEmptyResult.test(res) && query.getScopeId() != null) { //query did not find results

            KapuaId originalScopeId = query.getScopeId();

            do {
                // First check if there are any endpoint AT ALL specified in this scope/layer
                if (countAllEndpointsInScope(tx, query.getScopeId(), section)) {
                    // There are endpoints (even not matching the query), exit because I found the "nearest usable" endpoints which don't have what I'm searching
                    break;
                }
                Account account = KapuaSecurityUtils.doPrivileged(() -> accountService.find(query.getScopeId()));

                if (account == null) {
                    throw new KapuaEntityNotFoundException(Account.TYPE, query.getScopeId());
                }

                if (account.getScopeId() == null) {
                    // Query was originally on root account, and querying on parent scope id would mean querying in null,
                    // i.e. querying on all accounts. Since that's not what we want, break away.
                    break;
                }
                query.setScopeId(account.getScopeId());
                res = queryExecutor.apply(tx, query);
            }
            while (query.getScopeId() != null && isEmptyResult.test(res));

            query.setScopeId(originalScopeId);
        }
        return res;
    }

    /**
     * Checks whether another {@link EndpointInfo} already exists with the given values.
     *
     * @param scopeId
     *         The ScopeId of the {@link EndpointInfo}
     * @param entityId
     *         The entity id, if exists. On update you need to exclude the same entity.
     * @param schema
     *         The {@link EndpointInfo#getSchema()}  value.
     * @param dns
     *         The {@link EndpointInfo#getDns()}  value.
     * @param port
     *         The {@link EndpointInfo#getPort()} value.
     * @param type
     *         The {@link EndpointInfo#getEndpointType()} value.
     * @throws KapuaException
     *         if the values provided matches another {@link EndpointInfo}
     * @since 1.0.0
     */
    private void checkDuplicateEndpointInfo(KapuaId scopeId, KapuaId entityId, String schema, String dns, int port, String type) throws KapuaException {

        EndpointInfoQuery query = new EndpointInfoQuery(scopeId);

        AndPredicate andPredicate = query.andPredicate(
                query.attributePredicate(EndpointInfoAttributes.SCHEMA, schema),
                query.attributePredicate(EndpointInfoAttributes.DNS, dns),
                query.attributePredicate(EndpointInfoAttributes.PORT, port)
        );

        if (entityId != null) {
            andPredicate.and(query.attributePredicate(KapuaEntityAttributes.ENTITY_ID, entityId, Operator.NOT_EQUAL));
        }

        query.setPredicate(andPredicate);

        final Long count = txManager.execute(tx -> doCount(tx, query, type));
        if (count > 0) {
            List<Map.Entry<String, Object>> uniquesFieldValues = new ArrayList<>();
            uniquesFieldValues.add(new AbstractMap.SimpleEntry<>(KapuaEntityAttributes.SCOPE_ID, scopeId));
            uniquesFieldValues.add(new AbstractMap.SimpleEntry<>(EndpointInfoAttributes.SCHEMA, schema));
            uniquesFieldValues.add(new AbstractMap.SimpleEntry<>(EndpointInfoAttributes.DNS, dns));
            uniquesFieldValues.add(new AbstractMap.SimpleEntry<>(EndpointInfoAttributes.PORT, port));

            throw new KapuaEntityUniquenessException(EndpointInfo.TYPE, uniquesFieldValues);
        }
    }

    private boolean countAllEndpointsInScope(TxContext txContext, KapuaId scopeId, String section) throws KapuaException {
        EndpointInfoQuery totalQuery = new EndpointInfoQuery(scopeId);
        addSectionToPredicate(totalQuery, section);
        long totalCount = repository.count(txContext, totalQuery);
        return totalCount != 0;
    }

    private void addSectionToPredicate(KapuaQuery query, String section) {
        QueryPredicate sectionPredicate = query.attributePredicate(EndpointInfoAttributes.ENDPOINT_TYPE, section);
        QueryPredicate currentPredicate = query.getPredicate();
        query.setPredicate(currentPredicate == null ? sectionPredicate : query.andPredicate(currentPredicate, sectionPredicate));
    }
}
