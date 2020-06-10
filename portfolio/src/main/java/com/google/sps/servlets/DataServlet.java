// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
// import com.google.cloud.datastore;
// import com.google.cloud.datastore.Query;
// import com.google.cloud.datastore.StructuredQuery;
import com.google.sps.data.Comments.CommentObject;
import com.google.sps.data.CommentAdapter;
// import com.google.sps.data.CommentAdapter;
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
        System.out.println("get");
        // Query<Entity> query = Query.newEntityQueryBuilder().setKind("Comment").setOrderBy(OrderBy.desc("time")).setLimit(Integer.parseInt(request.getParameter("num-comments"))).build();
        Query query = new Query("Comment").addSort("time", SortDirection.DESCENDING);

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        PreparedQuery results = datastore.prepare(query);
        // QueryResults<Entity> results = datastore.run(query);

        Integer numComments = Integer.parseInt(request.getParameter("num-comments"));
        CommentObject.Builder commentBuilder;

        List<CommentObject> comments = new ArrayList<>();
        for (Entity entity : results.asIterable()) {
            if (numComments > 0) {
                long id = entity.getKey().getId();
                // String comment = entity.getProperty("commentObject").toString();

                String text = (String) entity.getProperty("text");
                long timestamp = (long) entity.getProperty("time");

                commentBuilder = CommentObject.newBuilder();
                commentBuilder.setId(id);
                commentBuilder.setText(text);
                commentBuilder.setTime(timestamp);
                CommentObject hold = commentBuilder.build();
                System.out.println(hold.getText());

                // Comment comment = new Comment(id, text, timestamp);
                comments.add(hold);
                numComments--;
            }
        }

        // System.out.println(comments.size() + "\n");

        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.registerTypeAdapter(CommentObject.class, new CommentAdapter()).create();

        response.setContentType("application/json;");
        response.getWriter().println(gson.toJson(comments));
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("post");
        String comment = request.getParameter("comment-text");
        System.out.println(comment);
        long timestamp = System.currentTimeMillis();
            
        if (comment.length() > 0) {
            Entity newComment = new Entity("Comment");

            newComment.setProperty("text", comment);
            newComment.setProperty("time", timestamp);

            // Comments.CommentObject commentObject = Comments.CommentObject.newBuilder().setText(comment).setTime(timestamp).build();

            // newComment.setProperty("text", commentObject.getText());
            // newComment.setProperty("time", commentObject.getTime());

            System.out.println(newComment.getProperty("text"));

            DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
            datastore.put(newComment);
        }

        response.sendRedirect("/data");
    }
}
