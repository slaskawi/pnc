package org.jboss.pnc.rest.endpoint.wrappers;

import org.jboss.resteasy.annotations.interception.ServerInterceptor;
import org.jboss.resteasy.spi.interception.MessageBodyWriterContext;
import org.jboss.resteasy.spi.interception.MessageBodyWriterInterceptor;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import java.io.IOException;

/**
 * @deprecated This class should be migrated to http://docs.oracle.com/javaee/7/api/javax/ws/rs/ext/WriterInterceptor.html
 *      as soon as we get EAP7.
 */
@Produces
@ServerInterceptor
public class WrapperProducer implements MessageBodyWriterInterceptor {

    @Override
    public void write(MessageBodyWriterContext context) throws IOException, WebApplicationException {
        Object entity = context.getEntity();

        if(entity instanceof MetadataList) {
            MetadataList<?> metadataEntity = (MetadataList<?>) entity;
            ResultsWrapper wrappedResults = ResultsWrapper.Builder.fromResults(metadataEntity, metadataEntity.getPageIndex(), metadataEntity.getPageSize());
            context.setEntity(wrappedResults);
            context.setType(wrappedResults.getClass());
            context.setGenericType(metadataEntity.getClass().getGenericSuperclass());
        } else {
            ResultsWrapper wrappedResults = ResultsWrapper.Builder.fromResults(entity);
            context.setType(wrappedResults.getClass());
            context.setGenericType(wrappedResults.getClass());
        }

        context.proceed();
    }
}
