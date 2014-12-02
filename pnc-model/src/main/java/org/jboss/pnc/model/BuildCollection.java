package org.jboss.pnc.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * The Class BuildCollection, that encapsulates the set of buildResults that compose a specific version of a Product (may be
 * refactored to a proper entity)
 *
 * @author avibelli
 */
@Entity
public class BuildCollection implements Serializable {

    private static final long serialVersionUID = 1633628406382742445L;

    @Id
    @GeneratedValue
    private Integer id;

    private String productName;

    private String productVersion;

    private String description;

    @ManyToMany
    private List<ProjectBuildResult> projectBuildResult;

    @OneToMany
    private List<Project> projects;

    /**
     * Instantiates a new builds the collection.
     */
    public BuildCollection() {
        projects = new ArrayList<>();
        projectBuildResult = new ArrayList<>();
    }

    /**
     * Gets the id.
     *
     * @return the id
     */
    public Integer getId() {
        return id;
    }

    /**
     * Sets the id.
     *
     * @param id the new id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Gets the product name.
     *
     * @return the product name
     */
    public String getProductName() {
        return productName;
    }

    /**
     * Sets the product name.
     *
     * @param productName the new product name
     */
    public void setProductName(String productName) {
        this.productName = productName;
    }

    /**
     * Gets the product version.
     *
     * @return the product version
     */
    public String getProductVersion() {
        return productVersion;
    }

    /**
     * Sets the product version.
     *
     * @param productVersion the new product version
     */
    public void setProductVersion(String productVersion) {
        this.productVersion = productVersion;
    }

    /**
     * Gets the description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description.
     *
     * @param description the new description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the project build result.
     *
     * @return the project build result
     */
    public List<ProjectBuildResult> getProjectBuildResult() {
        return projectBuildResult;
    }

    /**
     * Sets the project build result.
     *
     * @param projectBuildResult the new project build result
     */
    public void setProjectBuildResult(List<ProjectBuildResult> projectBuildResult) {
        this.projectBuildResult = projectBuildResult;
    }

    public List<Project> getProjects() {
        return projects;
    }

    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "BuildCollection [productName=" + productName + ", productVersion=" + productVersion + "]";
    }

    public void addProject(Project project) {
        projects.add(project);
    }
}
