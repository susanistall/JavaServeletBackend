/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Java Servlet Module" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/HelloWorld
*/

package com.example.Owner.myapplication.backend;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.*;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.repackaged.com.google.gson.Gson;

public class MyDogServlet extends HttpServlet {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        //resp.setContentType("text/plain");
        //resp.getWriter().println("Please use the form to POST to this url");
        String dogName = req.getParameter("dogName").toString();

        Query.Filter filter = new Query.FilterPredicate("dogName", Query.FilterOperator.EQUAL, dogName);
        Query q = new Query("dog").setFilter(filter);

        List<Entity> results = datastore.prepare(q).asList(FetchOptions.Builder.withDefaults());

        //send back json response
        Gson gson = new Gson();
        String json = gson.toJson(results);
        String callback = req.getParameter("callback");

        resp.setContentType("application/json");
        resp.getWriter().println(json);
    }
}
