package org.jboss.pnc.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

/**
 * Created by <a href="mailto:matejonnet@gmail.com">Matej Lazar</a> on 2014-11-23.
 * 
 * The class Project incapsulates the basic properties of a Project, i.e. the name, description, license. It is linked to a list
 * of ProjectBuildConfigurations, that contain the build configurations of the Project in its lifetime. The class Project is
 * also linked to a list of ProjectBuildResults, that contains the result of the build triggered with a
 * ProjectBuildConfiguration
 */
@Entity
public class Project implements Serializable {

    private static final long serialVersionUID = -4644857058640271044L;

    @Id
    @GeneratedValue
    private Integer id;

    private String name;

    private String description;

    private String issueTrackerUrl;

    private String projectUrl;

    @ManyToOne
    private BuildCollection buildCollection;

    @ManyToOne
    private License license;

    @OneToMany(mappedBy = "project")
    private List<ProjectBuildConfiguration> projectBuildConfigurations;

    @OneToMany(mappedBy = "project")
    private List<ProjectBuildResult> projectBuildResults;

    /**
     * Instantiates a new project.
     */
    public Project() {
        this.projectBuildConfigurations = new ArrayList<>();
    }

    /**
     * Instantiates a new project.
     *
     * @param name the name
     * @param description the description
     * @param issueTrackerUrl the issue tracker url
     * @param projectUrl the project url
     * @param license the license
     * @param projectBuildConfigurations the project build configurations
     */
    public Project(String name, String description, String issueTrackerUrl, String projectUrl, License license,
            ProjectBuildConfiguration... projectBuildConfigurations) {

        this.name = name;
        this.description = description;
        this.issueTrackerUrl = issueTrackerUrl;
        this.projectUrl = projectUrl;
        this.license = license;
        this.projectBuildConfigurations = Arrays.asList(projectBuildConfigurations);
    }

    /**
     * @return the id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the issueTrackerUrl
     */
    public String getIssueTrackerUrl() {
        return issueTrackerUrl;
    }

    /**
     * @param issueTrackerUrl the issueTrackerUrl to set
     */
    public void setIssueTrackerUrl(String issueTrackerUrl) {
        this.issueTrackerUrl = issueTrackerUrl;
    }

    /**
     * @return the projectUrl
     */
    public String getProjectUrl() {
        return projectUrl;
    }

    /**
     * @param projectUrl the projectUrl to set
     */
    public void setProjectUrl(String projectUrl) {
        this.projectUrl = projectUrl;
    }

    /**
     * @return the license
     */
    public License getLicense() {
        return license;
    }

    /**
     * @param license the license to set
     */
    public void setLicense(License license) {
        this.license = license;
    }

    /**
     * @return the projectBuildConfigurations
     */
    public List<ProjectBuildConfiguration> getProjectBuildConfigurations() {
        return projectBuildConfigurations;
    }

    /**
     * @return the projectBuildResults
     */
    public List<ProjectBuildResult> getProjectBuildResults() {
        return projectBuildResults;
    }

    /**
     * @param projectBuildResults the projectBuildResults to set
     */
    public void setProjectBuildResults(List<ProjectBuildResult> projectBuildResults) {
        this.projectBuildResults = projectBuildResults;
    }

    public void addProjectBuildConfiguration(ProjectBuildConfiguration configuration) {
        projectBuildConfigurations.add(configuration);
    }

    public BuildCollection getBuildCollection() {
        return buildCollection;
    }

    public void setBuildCollection(BuildCollection buildCollection) {
        this.buildCollection = buildCollection;
    }


    @Override
    public String toString() {
        return "Project [name=" + name + "]";
    }

}
