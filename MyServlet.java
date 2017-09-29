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

public class MyServlet extends HttpServlet {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        //resp.setContentType("text/plain");
        //resp.getWriter().println("Please use the form to POST to this url");
        String userID = req.getParameter("userID").toString();

        Query.Filter filter = new Query.FilterPredicate("userID", Query.FilterOperator.EQUAL, userID);
        Query q = new Query("person").setFilter(filter);

        List<Entity> results = datastore.prepare(q).asList(FetchOptions.Builder.withDefaults());

        //if not a person in database, add them with keyname of userID
        if(results == null || results.isEmpty()) {
            Entity person = new Entity("person");
            person.setProperty("userID", userID);
            datastore.put(person);

            results = datastore.prepare(q).asList(FetchOptions.Builder.withDefaults());
        }
        //send back json response
        Gson gson = new Gson();
        String json = gson.toJson(results);
        String callback = req.getParameter("callback");

        resp.setContentType("application/json");
        resp.getWriter().println(json);
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        String ownerID = req.getParameter("ownerID").toString();
        String dogName = req.getParameter("dogName").toString();
        String breed = req.getParameter("breed").toString();
        String nafaID = req.getParameter("nafaID").toString();

        //nafaID is identifying feature of dog
        Entity dog = new Entity("dog");

        dog.setProperty("ownerID", ownerID);
        dog.setProperty("dogName", dogName);
        dog.setProperty("breed", breed);
        dog.setProperty("nafaID", nafaID);

        //add dog to person
        Query.Filter filter = new Query.FilterPredicate("userID", Query.FilterOperator.EQUAL, ownerID);
        Query q = new Query("person").setFilter(filter);

        List<Entity> results = datastore.prepare(q).asList(FetchOptions.Builder.withDefaults());
        if(results != null && !results.isEmpty()){
            Entity person = results.get(0);
            //get dog list
            ArrayList<String> dogs = (ArrayList<String>) person.getProperty("dogs");
            if(dogs!= null){
                dogs.add(dogName);
            }
            else {
                dogs = new ArrayList<String>();
                dogs.add(dogName);
            }
            person.setProperty("dogs", dogs);
            datastore.put(person);
        }
        datastore.put(dog);
    }

    public void doDelete(HttpServletRequest req, HttpServletResponse resp){
        String dogName = req.getParameter("dogName").toString();

        Query.Filter filter = new Query.FilterPredicate("dogName", Query.FilterOperator.EQUAL, dogName);
        Query q = new Query("dog").setFilter(filter);

        List<Entity> results = datastore.prepare(q).asList(FetchOptions.Builder.withDefaults());

        //if not a dog in database, do nothing
        if(results == null || results.isEmpty()) {
        }
        else {
            Entity dog = results.get(0);
            datastore.delete(dog.getKey());
        }

        //remove dog from person
        String userID = req.getParameter("userID").toString();

        filter = new Query.FilterPredicate("userID", Query.FilterOperator.EQUAL, userID);
        q = new Query("person").setFilter(filter);

        results = datastore.prepare(q).asList(FetchOptions.Builder.withDefaults());
        if(results != null && !results.isEmpty()) {
            Entity person = results.get(0);
            ArrayList<String> dogs = (ArrayList<String>) person.getProperty("dogs");
            if(dogs!= null){
                if(dogs.contains(dogName)) {
                    dogs.remove(dogName);
                    person.setProperty("dogs", dogs);
                }
            }
            datastore.put(person);
        }
    }

    public void doPut(HttpServletRequest req, HttpServletResponse resp){

        String team = req.getParameter("team").toString();
        String region = req.getParameter("region").toString();
        String userID = req.getParameter("userID").toString();


        Query.Filter filter = new Query.FilterPredicate("userID", Query.FilterOperator.EQUAL, userID);
        Query q = new Query("person").setFilter(filter);

        List<Entity> results = datastore.prepare(q).asList(FetchOptions.Builder.withDefaults());
        if(results != null && !results.isEmpty()) {
            Entity person = results.get(0);
            person.setProperty("team", team);
            person.setProperty("region", region);
            datastore.put(person);
        }
    }
}
