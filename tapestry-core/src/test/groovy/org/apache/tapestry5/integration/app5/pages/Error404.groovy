package org.apache.tapestry5.integration.app5.pages

import org.apache.tapestry5.ioc.annotations.Inject
import org.apache.tapestry5.services.Request
import org.apache.tapestry5.services.Response

import javax.servlet.http.HttpServletResponse

class Error404 {

    @Inject
    Request request

    @Inject
    Response response

    void beginRender() {
        response.setStatus HttpServletResponse.SC_NOT_FOUND
    }
}
