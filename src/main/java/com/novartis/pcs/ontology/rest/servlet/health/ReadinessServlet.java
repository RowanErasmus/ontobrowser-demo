package com.novartis.pcs.ontology.rest.servlet.health;

import com.novartis.pcs.ontology.dao.CuratorDAOLocal;

import javax.ejb.EJB;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/health/ready")
public class ReadinessServlet extends HttpServlet {

    @EJB
    protected CuratorDAOLocal curatorDAOLocal;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            curatorDAOLocal.loadLastCreated(1);
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("{\"status\": \"UP\"}");
            response.getWriter().close();
            response.getWriter().flush();
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            response.getWriter().write("{\"status\": \"DOWN\"}");
            response.getWriter().close();
            response.getWriter().flush();
        }
    }
}
