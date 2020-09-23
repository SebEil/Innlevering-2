package no.kristiania;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class QueryStringTest {

    @Test
    void ShouldRetrieveQueryParameters(){
        QueryString queryString = new QueryString("Status=200");
        assertEquals("200", queryString.getParameter("Status"));
    }

    @Test
    void ShouldRetrieveOtherQueryParameters(){
        QueryString queryString = new QueryString("Status=400");
        assertEquals("400", queryString.getParameter("Status"));
    }

    @Test
    void ShouldRetrieveParameterByName(){
        QueryString queryString = new QueryString("text=Hello");
        assertEquals("Hello", queryString.getParameter("text"));
        assertEquals(null, queryString.getParameter("status"));
    }

    @Test
    void ShouldHandleMultipleParameters(){
        QueryString queryString = new QueryString("text=Hello&status=200");
        assertEquals("Hello", queryString.getParameter("text"));
        assertEquals("200", queryString.getParameter("status"));
    }

}