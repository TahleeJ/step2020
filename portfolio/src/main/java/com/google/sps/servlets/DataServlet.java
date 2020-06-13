package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.sps.data.CommentAdapter;
import com.google.sps.data.Comments.CommentObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/data")
public class DataServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Query query = new Query("Comment").addSort("time", SortDirection.DESCENDING);

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        PreparedQuery results = datastore.prepare(query);

        Integer numComments = Integer.parseInt(request.getParameter("num-comments"));
        CommentObject.Builder commentBuilder;

        List<CommentObject> comments = new ArrayList<>();
        for (Entity entity : results.asIterable()) {

            if (numComments > 0) {
                long id = entity.getKey().getId();
                String text = (String) entity.getProperty("text");
                long timestamp = (long) entity.getProperty("time");

                commentBuilder = CommentObject.newBuilder();
                commentBuilder.setId(id);
                commentBuilder.setText(text);
                commentBuilder.setTime(timestamp);
                CommentObject hold = commentBuilder.build();

                comments.add(hold);
                numComments--;
            }
        }

        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.registerTypeAdapter(CommentObject.class, new CommentAdapter()).create();

        response.setContentType("application/json;");
        response.getWriter().println(gson.toJson(comments));
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String comment = request.getParameter("comment-text");
            
        if (comment.length() > 0) {
            long timestamp = System.currentTimeMillis();
            
            Entity newComment = new Entity("Comment");

            newComment.setProperty("text", comment);
            newComment.setProperty("time", timestamp);

            DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
            datastore.put(newComment);
        }

        response.sendRedirect("/index.html");
    }
}
