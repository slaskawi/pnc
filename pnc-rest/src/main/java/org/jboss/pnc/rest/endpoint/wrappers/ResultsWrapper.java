package org.jboss.pnc.rest.endpoint.wrappers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.lang.invoke.MethodHandles;

@XmlRootElement
public class ResultsWrapper<T> {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    protected Integer page;
    protected Integer size;
    protected String errorMessage;
    protected T results;

    public ResultsWrapper() {
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public T getResults() {
        return results;
    }

    public void setResults(T results) {
        this.results = results;
    }

    @XmlTransient
    public static class Builder {

        private Builder() {
        }

        public static <T> ResultsWrapper fromResults(T results,  Integer page, Integer size) {
            ResultsWrapper<T> result = new ResultsWrapper();
            result.page = page;
            result.size = size;
            result.results = results;
            return result;
        }

        public static <T> ResultsWrapper fromResults(T results) {
           return fromResults(results, null, null);
        }

        public static <T> ResultsWrapper fromException(Exception e) {
            ResultsWrapper<T> result = new ResultsWrapper();
            logger.debug("Mapping exception", e);
            result.errorMessage = e.getMessage();
            return result;
        }
    }
}
