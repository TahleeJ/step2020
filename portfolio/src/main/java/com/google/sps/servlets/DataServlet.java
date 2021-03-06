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

import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
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
        Query query = new Query("Comment");

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        PreparedQuery results = datastore.prepare(query);

        Integer numComments = Integer.parseInt(request.getParameter("num-comments"));
        CommentObject.Builder commentBuilder;

        List<CommentObject> comments = new ArrayList<>();
        for (Entity entity : results.asIterable()) {

            if (numComments > 0) {
                long id = entity.getKey().getId();
                CommentObject comment = 
                    CommentObject.parseFrom(((Blob) entity.getProperty("commentInfo")).getBytes());

                comments.add(comment);
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
        String commentText = request.getParameter("comment-text");
            
        if (commentText.length() > 0) {
            long timestamp = System.currentTimeMillis();
            
            Entity newComment = new Entity("Comment");

            CommentObject.Builder commentBuilder = CommentObject.newBuilder();
            commentBuilder.setText(commentText);
            commentBuilder.setTime(timestamp);

            newComment.setProperty("commentInfo", new Blob(commentBuilder.build().toByteArray()));

            DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
            datastore.put(newComment);
        }

        response.sendRedirect("/index.html");
    }
}
