/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2014 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.pnc.rest.endpoint;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.jboss.pnc.auth.AuthenticationProvider;
import org.jboss.pnc.core.exception.CoreException;
import org.jboss.pnc.model.BuildConfigurationSet;
import org.jboss.pnc.model.User;
import org.jboss.pnc.rest.provider.BuildConfigurationProvider;
import org.jboss.pnc.rest.provider.BuildConfigurationSetProvider;
import org.jboss.pnc.rest.provider.BuildRecordProvider;
import org.jboss.pnc.rest.restmodel.BuildConfigurationRest;
import org.jboss.pnc.rest.restmodel.BuildConfigurationSetRest;
import org.jboss.pnc.rest.restmodel.response.Page;
import org.jboss.pnc.rest.restmodel.response.Singleton;
import org.jboss.pnc.rest.trigger.BuildTriggerer;
import org.jboss.pnc.rest.validation.exceptions.ConflictedEntryException;
import org.jboss.pnc.rest.validation.exceptions.ValidationException;
import org.jboss.pnc.spi.datastore.Datastore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.lang.invoke.MethodHandles;
import java.net.URL;

import static org.jboss.pnc.rest.configuration.SwaggerConstants.CONFLICTED_CODE;
import static org.jboss.pnc.rest.configuration.SwaggerConstants.CONFLICTED_DESCRIPTION;
import static org.jboss.pnc.rest.configuration.SwaggerConstants.INVALID_DESCRIPTION;
import static org.jboss.pnc.rest.configuration.SwaggerConstants.INVLID_CODE;
import static org.jboss.pnc.rest.configuration.SwaggerConstants.NOT_FOUND_CODE;
import static org.jboss.pnc.rest.configuration.SwaggerConstants.NOT_FOUND_DESCRIPTION;
import static org.jboss.pnc.rest.configuration.SwaggerConstants.NO_CONTENT_CODE;
import static org.jboss.pnc.rest.configuration.SwaggerConstants.NO_CONTENT_DESCRIPTION;
import static org.jboss.pnc.rest.configuration.SwaggerConstants.PAGE_INDEX_DEFAULT_VALUE;
import static org.jboss.pnc.rest.configuration.SwaggerConstants.PAGE_INDEX_DESCRIPTION;
import static org.jboss.pnc.rest.configuration.SwaggerConstants.PAGE_INDEX_QUERY_PARAM;
import static org.jboss.pnc.rest.configuration.SwaggerConstants.PAGE_SIZE_DEFAULT_VALUE;
import static org.jboss.pnc.rest.configuration.SwaggerConstants.PAGE_SIZE_DESCRIPTION;
import static org.jboss.pnc.rest.configuration.SwaggerConstants.PAGE_SIZE_QUERY_PARAM;
import static org.jboss.pnc.rest.configuration.SwaggerConstants.QUERY_DESCRIPTION;
import static org.jboss.pnc.rest.configuration.SwaggerConstants.QUERY_QUERY_PARAM;
import static org.jboss.pnc.rest.configuration.SwaggerConstants.SERVER_ERROR_CODE;
import static org.jboss.pnc.rest.configuration.SwaggerConstants.SERVER_ERROR_DESCRIPTION;
import static org.jboss.pnc.rest.configuration.SwaggerConstants.SORTING_DESCRIPTION;
import static org.jboss.pnc.rest.configuration.SwaggerConstants.SORTING_QUERY_PARAM;
import static org.jboss.pnc.rest.configuration.SwaggerConstants.SUCCESS_CODE;
import static org.jboss.pnc.rest.configuration.SwaggerConstants.SUCCESS_DESCRIPTION;

@Api(value = "/build-configuration-sets", description = "Set of related build configurations")
@Path("/build-configuration-sets")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BuildConfigurationSetEndpoint extends AbstractEndpoint<BuildConfigurationSet, BuildConfigurationSetRest> {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private BuildTriggerer buildTriggerer;
    
    @Context
    private HttpServletRequest httpServletRequest;
    
    @Inject
    private Datastore datastore;

    private BuildConfigurationSetProvider buildConfigurationSetProvider;
    private BuildConfigurationProvider buildConfigurationProvider;
    private BuildRecordProvider buildRecordProvider;

    public BuildConfigurationSetEndpoint() {

    }

    @Inject
    public BuildConfigurationSetEndpoint(BuildConfigurationSetProvider buildConfigurationSetProvider,
            BuildTriggerer buildTriggerer, BuildConfigurationProvider buildConfigurationProvider,
            BuildRecordProvider buildRecordProvider) {
        super(buildConfigurationSetProvider);
        this.buildConfigurationSetProvider = buildConfigurationSetProvider;
        this.buildTriggerer = buildTriggerer;
        this.buildConfigurationProvider = buildConfigurationProvider;
        this.buildRecordProvider = buildRecordProvider;
    }

    @ApiOperation(value = "Gets all Build Configuration Sets", response = Page.class)
    @ApiResponses(value = {
            @ApiResponse(code = SUCCESS_CODE, message = SUCCESS_DESCRIPTION),
            @ApiResponse(code = NO_CONTENT_CODE, message = NO_CONTENT_DESCRIPTION),
            @ApiResponse(code = INVLID_CODE, message = INVALID_DESCRIPTION),
            @ApiResponse(code = SERVER_ERROR_CODE, message = SERVER_ERROR_DESCRIPTION)
    })
    @GET
    public Response getAll(@ApiParam(value = PAGE_INDEX_DESCRIPTION) @QueryParam(PAGE_INDEX_QUERY_PARAM) @DefaultValue(PAGE_INDEX_DEFAULT_VALUE) int pageIndex,
            @ApiParam(value = PAGE_SIZE_DESCRIPTION) @QueryParam(PAGE_SIZE_QUERY_PARAM) @DefaultValue(PAGE_SIZE_DEFAULT_VALUE) int pageSize,
            @ApiParam(value = SORTING_DESCRIPTION) @QueryParam(SORTING_QUERY_PARAM) String sort,
            @ApiParam(value = QUERY_DESCRIPTION, required = false) @QueryParam(QUERY_QUERY_PARAM) String q) {
        return super.getAll(pageIndex, pageSize, sort, q);
    }

    @ApiOperation(value = "Creates a new Build Configuration Set", response = Singleton.class)
    @ApiResponses(value = {
            @ApiResponse(code = SUCCESS_CODE, message = SUCCESS_DESCRIPTION),
            @ApiResponse(code = INVLID_CODE, message = INVALID_DESCRIPTION),
            @ApiResponse(code = CONFLICTED_CODE, message = CONFLICTED_DESCRIPTION),
            @ApiResponse(code = SERVER_ERROR_CODE, message = SERVER_ERROR_DESCRIPTION)
    })
    @POST
    public Response createNew(@NotNull @Valid BuildConfigurationSetRest buildConfigurationSetRest, @Context UriInfo uriInfo)
            throws ValidationException {
        return super.createNew(buildConfigurationSetRest, uriInfo);
    }

    @ApiOperation(value = "Gets a specific Build Configuration Set", response = Singleton.class)
    @ApiResponses(value = {
            @ApiResponse(code = SUCCESS_CODE, message = SUCCESS_DESCRIPTION),
            @ApiResponse(code = INVLID_CODE, message = INVALID_DESCRIPTION),
            @ApiResponse(code = NOT_FOUND_CODE, message = NOT_FOUND_DESCRIPTION),
            @ApiResponse(code = SERVER_ERROR_CODE, message = SERVER_ERROR_DESCRIPTION)
    })
    @GET
    @Path("/{id}")
    public Response getSpecific(
            @ApiParam(value = "Build Configuration Set id", required = true) @PathParam("id") Integer id) {
        return super.getSpecific(id);
    }

    @ApiOperation(value = "Updates an existing Build Configuration Set")
    @ApiResponses(value = {
            @ApiResponse(code = SUCCESS_CODE, message = SUCCESS_DESCRIPTION),
            @ApiResponse(code = INVLID_CODE, message = INVALID_DESCRIPTION),
            @ApiResponse(code = CONFLICTED_CODE, message = CONFLICTED_DESCRIPTION),
            @ApiResponse(code = SERVER_ERROR_CODE, message = SERVER_ERROR_DESCRIPTION)
    })
    @PUT
    @Path("/{id}")
    public Response update(@ApiParam(value = "Build Configuration Set id", required = true) @PathParam("id") Integer id,
            @NotNull @Valid BuildConfigurationSetRest buildConfigurationSetRest) throws ValidationException {
        return super.update(id, buildConfigurationSetRest);
    }

    @ApiOperation(value = "Removes a specific Build Configuration Set")
    @ApiResponses(value = {
            @ApiResponse(code = SUCCESS_CODE, message = SUCCESS_DESCRIPTION),
            @ApiResponse(code = INVLID_CODE, message = INVALID_DESCRIPTION),
            @ApiResponse(code = SERVER_ERROR_CODE, message = SERVER_ERROR_DESCRIPTION)
    })
    @DELETE
    @Path("/{id}")
    public Response deleteSpecific(@ApiParam(value = "Build Configuration Set id", required = true) @PathParam("id") Integer id)
            throws ValidationException {
        return super.delete(id);
    }

    @ApiOperation(value = "Gets the Configurations for the Specified Set", response = Page.class)
    @ApiResponses(value = {
            @ApiResponse(code = SUCCESS_CODE, message = SUCCESS_DESCRIPTION),
            @ApiResponse(code = NO_CONTENT_CODE, message = NO_CONTENT_DESCRIPTION),
            @ApiResponse(code = INVLID_CODE, message = INVALID_DESCRIPTION),
            @ApiResponse(code = SERVER_ERROR_CODE, message = SERVER_ERROR_DESCRIPTION)
    })
    @GET
    @Path("/{id}/build-configurations")
    public Response getConfigurations(@ApiParam(value = PAGE_INDEX_DESCRIPTION) @QueryParam(PAGE_INDEX_QUERY_PARAM) @DefaultValue(PAGE_INDEX_DEFAULT_VALUE) int pageIndex,
            @ApiParam(value = PAGE_SIZE_DESCRIPTION) @QueryParam(PAGE_SIZE_QUERY_PARAM) @DefaultValue(PAGE_SIZE_DEFAULT_VALUE) int pageSize,
            @ApiParam(value = SORTING_DESCRIPTION) @QueryParam(SORTING_QUERY_PARAM) String sort,
            @ApiParam(value = QUERY_DESCRIPTION, required = false) @QueryParam(QUERY_QUERY_PARAM) String q,
            @ApiParam(value = "Build Configuration Set id", required = true) @PathParam("id") Integer id) {
        return fromCollection(buildConfigurationProvider.getAllForBuildConfigurationSet(pageIndex, pageSize, sort, q,
                id));
    }

    @ApiOperation(value = "Adds a configuration to the Specified Set")
    @ApiResponses(value = {
            @ApiResponse(code = SUCCESS_CODE, message = SUCCESS_DESCRIPTION),
            @ApiResponse(code = INVLID_CODE, message = INVALID_DESCRIPTION),
            @ApiResponse(code = SERVER_ERROR_CODE, message = SERVER_ERROR_DESCRIPTION)
    })
    @POST
    @Path("/{id}/build-configurations")
    public Response addConfiguration(
            @ApiParam(value = "Build Configuration Set id", required = true) @PathParam("id") Integer id,
            BuildConfigurationRest buildConfig) throws ConflictedEntryException {
        buildConfigurationSetProvider.addConfiguration(id, buildConfig.getId());
        return fromEmpty();
    }

    @ApiOperation(value = "Removes a configuration from the specified config set")
    @ApiResponses(value = {
            @ApiResponse(code = SUCCESS_CODE, message = SUCCESS_DESCRIPTION),
            @ApiResponse(code = INVLID_CODE, message = INVALID_DESCRIPTION),
            @ApiResponse(code = SERVER_ERROR_CODE, message = SERVER_ERROR_DESCRIPTION)
    })
    @DELETE
    @Path("/{id}/build-configurations/{configId}")
    public Response removeConfiguration(
            @ApiParam(value = "Build configuration set id", required = true) @PathParam("id") Integer id,
            @ApiParam(value = "Build configuration id", required = true) @PathParam("configId") Integer configId) {
        buildConfigurationSetProvider.removeConfiguration(id, configId);
        return fromEmpty();
    }

    @ApiOperation(value = "Gets all build records associated with the contained build configurations", response = Page.class)
    @ApiResponses(value = {
            @ApiResponse(code = SUCCESS_CODE, message = SUCCESS_DESCRIPTION),
            @ApiResponse(code = NO_CONTENT_CODE, message = NO_CONTENT_DESCRIPTION),
            @ApiResponse(code = INVLID_CODE, message = INVALID_DESCRIPTION),
            @ApiResponse(code = SERVER_ERROR_CODE, message = SERVER_ERROR_DESCRIPTION)
    })
    @GET
    @Path("/{id}/build-records")
    public Response getBuildRecords(
            @ApiParam(value = "Build configuration set id", required = true) @PathParam("id") Integer id,
            @ApiParam(value = PAGE_INDEX_DESCRIPTION) @QueryParam(PAGE_INDEX_QUERY_PARAM) @DefaultValue(PAGE_INDEX_DEFAULT_VALUE) int pageIndex,
            @ApiParam(value = PAGE_SIZE_DESCRIPTION) @QueryParam(PAGE_SIZE_QUERY_PARAM) @DefaultValue(PAGE_SIZE_DEFAULT_VALUE) int pageSize,
            @ApiParam(value = SORTING_DESCRIPTION) @QueryParam(SORTING_QUERY_PARAM) String sort,
            @ApiParam(value = QUERY_DESCRIPTION, required = false) @QueryParam(QUERY_QUERY_PARAM) String q) {
        return fromCollection(buildRecordProvider.getAllForBuildConfigSetRecord(pageIndex, pageSize, sort, q, id));
    }

    @ApiOperation(value = "Builds the Configurations for the Specified Set")
    @ApiResponses(value = {
            @ApiResponse(code = SUCCESS_CODE, message = SUCCESS_DESCRIPTION),
            @ApiResponse(code = INVLID_CODE, message = INVALID_DESCRIPTION),
            @ApiResponse(code = SERVER_ERROR_CODE, message = SERVER_ERROR_DESCRIPTION)
    })
    @POST
    @Path("/{id}/build")
    @Consumes(MediaType.WILDCARD)
    public Response build(
            @ApiParam(value = "Build Configuration Set id", required = true) @PathParam("id") Integer id,
            @ApiParam(value = "Optional Callback URL", required = false) @QueryParam("callbackUrl") String callbackUrl,
            @Context UriInfo uriInfo) {
        logger.info("Executing build configuration set id: " + id );

        try {
            AuthenticationProvider authProvider = new AuthenticationProvider(httpServletRequest);
            String loggedUser = authProvider.getUserName();
            User currentUser = null;
            if(loggedUser != null && loggedUser != "") {
                currentUser = datastore.retrieveUserByUsername(loggedUser);
            }
            if(currentUser == null) {
                currentUser = User.Builder.newBuilder()
                        .username(loggedUser)
                        .firstName(authProvider.getFirstName())
                        .lastName(authProvider.getLastName())
                        .email(authProvider.getEmail()).build();
                datastore.createNewUser(currentUser);
            }
            Integer runningBuildId = null;
            // if callbackUrl is provided trigger build accordingly
            if (callbackUrl == null || callbackUrl.isEmpty()) {
                runningBuildId = buildTriggerer.triggerBuildConfigurationSet(id, currentUser);
            } else {
                runningBuildId = buildTriggerer.triggerBuildConfigurationSet(id, currentUser, new URL(callbackUrl));
            }

            return fromEmpty();
        } catch (CoreException e) {
            logger.error(e.getMessage(), e);
            return Response.serverError().entity("Core error: " + e.getMessage()).build();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return Response.serverError().entity("Other error: " + e.getMessage()).build();
        }
    }

}
