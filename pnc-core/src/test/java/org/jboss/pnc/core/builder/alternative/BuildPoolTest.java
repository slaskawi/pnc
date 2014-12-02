package org.jboss.pnc.core.builder.alternative;

import org.jboss.pnc.core.builder.BuildPool;
import org.jboss.pnc.core.builder.recipe.BuildRecipesProvider;
import org.jboss.pnc.model.*;
import org.jboss.pnc.model.builder.EnvironmentBuilder;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class BuildPoolTest {

    @Test
    public void shouldBuildProjectInProperOrder() throws Exception {
        //given
        BuildRecipesProvider recipesProvider = new BuildRecipesProvider() {
            @Override
            public Function<ProjectBuildConfiguration, ProjectBuildResult> getRecipe(ProjectBuildConfiguration configuration) {
                return (conf) -> {
                    ProjectBuildResult result = new ProjectBuildResult();
                    result.setProjectBuildConfiguration(conf);
                    result.setStatus(BuildStatus.SUCCESS);
                    return result;
                };
            }
        };

        BuildCollection buildCollection = new BuildCollection();
        buildCollection.setProductName("foo");
        buildCollection.setProductVersion("1.0");

        BuildPool buildPool = new BuildPool(1);

        Environment notRelevant = EnvironmentBuilder.defaultEnvironment().build();

        ProjectBuildConfiguration p1 = createProjectBuildConfiguration("p1", notRelevant, buildCollection);
        ProjectBuildConfiguration p2 = createProjectBuildConfiguration("p1", notRelevant, buildCollection, p1);
        ProjectBuildConfiguration p3 = createProjectBuildConfiguration("p1", notRelevant, buildCollection, p2);
        ProjectBuildConfiguration p4 = createProjectBuildConfiguration("p1", notRelevant, buildCollection, p3, p1, p2);

        //when
        BlockingQueue<Future<ProjectBuildResult>> builds = buildPool.submit(recipesProvider, Arrays.asList(p4));

        //then
        assertThat(getBuildResults(builds)).containsExactly(p1, p2, p3, p4);
    }

    private List<ProjectBuildConfiguration> getBuildResults(BlockingQueue<Future<ProjectBuildResult>> workLog) {
        return workLog.stream()
                .map((future) -> {
                    try {
                        return future.get(10, TimeUnit.MILLISECONDS).getProjectBuildConfiguration();
                    } catch (Exception e) {
                        throw new IllegalStateException(e);
                    }
                })
                .collect(Collectors.toList());
    }

    public ProjectBuildConfiguration createProjectBuildConfiguration(String projectName, Environment environment, BuildCollection buildCollection, ProjectBuildConfiguration... dependencies) {
        Project project = new Project();
        project.setName(projectName);

        ProjectBuildConfiguration projectBuildConfiguration = new ProjectBuildConfiguration();
        projectBuildConfiguration.setEnvironment(environment);

        projectBuildConfiguration.setProject(project);
        project.addProjectBuildConfiguration(projectBuildConfiguration);

        buildCollection.addProject(project);
        project.setBuildCollection(buildCollection);

        Stream.of(dependencies).forEach((dependency) -> projectBuildConfiguration.addDependency(dependency));

        return projectBuildConfiguration;
    }

}