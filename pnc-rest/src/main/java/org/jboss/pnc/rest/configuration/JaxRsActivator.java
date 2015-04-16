package org.jboss.pnc.rest.configuration;

import org.jboss.pnc.rest.debug.TestEndpoint;
import org.jboss.pnc.rest.endpoint.*;
import org.jboss.pnc.rest.endpoint.wrappers.WrapperProducer;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/rest")
public class JaxRsActivator extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new HashSet<>();
        addSwaggerResources(resources);
        addProjectResources(resources);
        return resources;
    }

    private void addProjectResources(Set<Class<?>> resources) {
        resources.add(ProductEndpoint.class);
        resources.add(ProductVersionEndpoint.class);
        resources.add(ProductMilestoneEndpoint.class);
        resources.add(ProductReleaseEndpoint.class);
        resources.add(ProjectEndpoint.class);
        resources.add(BuildConfigurationEndpoint.class);
        resources.add(BuildConfigurationSetEndpoint.class);
        resources.add(BuildRecordEndpoint.class);
        resources.add(BuildRecordSetEndpoint.class);
        resources.add(RunningBuildRecordEndpoint.class);
        resources.add(UserEndpoint.class);
        resources.add(LicenseEndpoint.class);
        resources.add(EnvironmentEndpoint.class);
        resources.add(TestEndpoint.class);
        resources.add(IllegalArgumentExceptionMapper.class);
        resources.add(WrapperProducer.class);
    }

    private void addSwaggerResources(Set<Class<?>> resources) {
        resources.add(com.wordnik.swagger.jaxrs.listing.ApiListingResource.class);
        resources.add(com.wordnik.swagger.jaxrs.listing.ApiDeclarationProvider.class);
        resources.add(com.wordnik.swagger.jaxrs.listing.ApiListingResourceJSON.class);
        resources.add(com.wordnik.swagger.jaxrs.listing.ResourceListingProvider.class);
    }

}
