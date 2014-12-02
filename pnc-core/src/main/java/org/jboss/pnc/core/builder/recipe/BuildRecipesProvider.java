package org.jboss.pnc.core.builder.recipe;

import org.jboss.pnc.model.ProjectBuildConfiguration;
import org.jboss.pnc.model.ProjectBuildResult;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.function.Function;

@ApplicationScoped
public class BuildRecipesProvider {

    @Inject
    MavenBuildRecipe mavenBuildRecipe;

    public Function<ProjectBuildConfiguration, ProjectBuildResult> getRecipe(ProjectBuildConfiguration project) {
        //make some sophisticated decision
        return mavenBuildRecipe;
    }
}
