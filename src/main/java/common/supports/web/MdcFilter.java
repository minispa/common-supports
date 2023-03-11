package common.supports.web;

import common.supports.MixAll;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Slf4j
public class MdcFilter implements Filter {

    private FilterConfig filterConfig;

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
    }

    @Override
    public void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse, final FilterChain filterChain) throws IOException, ServletException {
        boolean insertMdc = insertMdc((HttpServletRequest) servletRequest);
        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            if(insertMdc) {
                removeMdc();
            }
        }
    }

    private boolean insertMdc(HttpServletRequest request) {
        String mdcVar = getMdcVar();
        if(MixAll.isBlank(mdcVar)) {
            return false;
        }
        String value = request.getHeader(mdcVar);
        if(MixAll.isBlank(value)) {
            value = MixAll.uuid();
        }
        MDC.put(mdcVar, value);
        return true;
    }

    private void removeMdc() {
        String mdcVar = getMdcVar();
        if(MixAll.isBlank(mdcVar)) {
            return;
        }
        MDC.remove(mdcVar);
    }

    private String getMdcVar() {
        String mdcVar = filterConfig.getInitParameter("mdcVar");
        String property = System.getProperty("system.mdc.var");
        if(MixAll.isBlank(mdcVar)) {
            mdcVar = property;
        }
        return mdcVar;
    }

}
