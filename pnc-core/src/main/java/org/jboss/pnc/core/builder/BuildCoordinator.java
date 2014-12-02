package org.jboss.pnc.core.builder;

import org.jboss.pnc.core.builder.recipe.BuildRecipesProvider;
import org.jboss.pnc.model.ProjectBuildConfiguration;
import org.jboss.pnc.model.ProjectBuildResult;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;

/**
 * Created by <a href="mailto:matejonnet@gmail.com">Matej Lazar</a> on 2014-11-23.
 */
@ApplicationScoped
public class BuildCoordinator {

    private BuildPool buildPool;
    private BuildRecipesProvider buildRecipesProvider;

    @Inject
    public BuildCoordinator(BuildPool buildPool, BuildRecipesProvider buildRecipesProvider) {
        this.buildPool = buildPool;
        this.buildRecipesProvider = buildRecipesProvider;
    }

    public void buildProjects(List<ProjectBuildConfiguration> projectsBuildConfigurations) {
        BlockingQueue<Future<ProjectBuildResult>> builds = buildPool.submit(buildRecipesProvider, projectsBuildConfigurations);
        // do something with builds...
    }

}
