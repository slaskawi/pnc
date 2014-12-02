package org.jboss.pnc.core.builder;


import org.jboss.pnc.core.builder.recipe.BuildRecipesProvider;
import org.jboss.pnc.model.ProjectBuildConfiguration;
import org.jboss.pnc.model.ProjectBuildResult;

import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Function;

@ApplicationScoped
public class BuildPool {

    final BlockingQueue<Future<ProjectBuildResult>> workLog = new LinkedBlockingQueue<>();
    final ExecutorService executorService;

    public BuildPool() {
        this(3);
    }

    public BuildPool(int numberOfThreads) {
        executorService = Executors.newFixedThreadPool(numberOfThreads);
    }

    public BlockingQueue<Future<ProjectBuildResult>> submit(BuildRecipesProvider buildRecipesProvider, List<ProjectBuildConfiguration> projects) {
        List<ProjectBuildConfiguration> projectsToBeBuilt = submitToBuild(projects, new ArrayList<>());
        projectsToBeBuilt.stream()
                .forEach((project) -> {
                    Function<ProjectBuildConfiguration, ProjectBuildResult> recipe = buildRecipesProvider.getRecipe(project);
                    Future<ProjectBuildResult> buildResult = executorService.submit(() -> recipe.apply(project));
                    workLog.add(buildResult);
                });
        return workLog;
    }

    public void shutDown(long timeout, TimeUnit timeUnit){
        try {
            executorService.shutdown();
            executorService.awaitTermination(timeout, timeUnit);
        } catch (InterruptedException e) {
            // nothing we can do here - interrupting one thread above...
            Thread.currentThread().interrupt();
        }
    }

    private List<ProjectBuildConfiguration> submitToBuild(List<ProjectBuildConfiguration> projectsToBeScanned, List<ProjectBuildConfiguration> workingLog) {
        if(projectsToBeScanned.isEmpty()) {
            //eliminate duplicates
            return new ArrayList<>(new LinkedHashSet<>(workingLog));
        }
        projectsToBeScanned.stream()
                .forEach((project) -> workingLog.add(0, project));

        List<ProjectBuildConfiguration> newProjectToBeScanned = new ArrayList<>();
        for(ProjectBuildConfiguration project : projectsToBeScanned) {
            newProjectToBeScanned.addAll(project.getDependencies());
        }
        return submitToBuild(newProjectToBeScanned, workingLog);
    }
}
