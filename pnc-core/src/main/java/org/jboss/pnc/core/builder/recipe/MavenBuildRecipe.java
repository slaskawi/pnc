package org.jboss.pnc.core.builder.recipe;

import org.jboss.pnc.core.BuildDriverFactory;
import org.jboss.pnc.core.RepositoryManagerFactory;
import org.jboss.pnc.model.*;
import org.jboss.pnc.spi.builddriver.BuildDriver;
import org.jboss.pnc.spi.environment.EnvironmentDriver;
import org.jboss.pnc.spi.environment.EnvironmentDriverProvider;
import org.jboss.pnc.spi.repositorymanager.RepositoryConfiguration;
import org.jboss.pnc.spi.repositorymanager.RepositoryManager;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.lang.invoke.MethodHandles;
import java.util.function.Function;
import java.util.logging.Logger;

@ApplicationScoped
public class MavenBuildRecipe implements Function<ProjectBuildConfiguration, ProjectBuildResult> {

    private static final Logger logger = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    @Inject
    BuildDriverFactory buildDriverFactory;

    @Inject
    RepositoryManagerFactory repositoryManagerFactory;

    @Inject
    EnvironmentDriverProvider environmentDriverProvider;

    @Override
    public ProjectBuildResult apply(ProjectBuildConfiguration projectBuildConfiguration) {
        ProjectBuildResult result = new ProjectBuildResult();
        result.setProject(projectBuildConfiguration.getProject());
        try {
            BuildDriver buildDriver = buildDriverFactory.getBuildDriver(projectBuildConfiguration.getEnvironment().getBuildType());
            RepositoryManager repositoryManager = repositoryManagerFactory.getRepositoryManager(RepositoryType.MAVEN);

            RepositoryConfiguration repository =
                    repositoryManager.createRepository(projectBuildConfiguration, projectBuildConfiguration.getProject().getBuildCollection());

            buildDriver.setRepository(repository);

            EnvironmentDriver environmentDriver = environmentDriverProvider.getDriver(projectBuildConfiguration.getEnvironment()
                    .getOperationalSystem());
            environmentDriver.buildEnvironment(projectBuildConfiguration.getEnvironment());

            buildDriver.startProjectBuild(projectBuildConfiguration, (projectBuildResult) -> result.setStatus(BuildStatus.SUCCESS));
        } catch (Exception e) {
            result.setStatus(BuildStatus.FAILED);
        }
        return result;
    }
}
