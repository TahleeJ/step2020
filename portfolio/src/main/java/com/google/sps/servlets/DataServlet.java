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

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;

import com.google.sps.data.Comment;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.ArrayList;

@WebServlet("/data")
public class DataServlet extends HttpServlet {
    private Integer numComments = Comment.getNumComments();

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Query query = new Query("Comment").addSort("time", SortDirection.DESCENDING);

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        PreparedQuery results = datastore.prepare(query);

        List<Comment> comments = new ArrayList<>();
        for (Entity entity : results.asIterable()) {
        long id = entity.getKey().getId();
        String text = (String) entity.getProperty("text");
        long timestamp = (long) entity.getProperty("time");

        Comment comment = new Comment(id, text, timestamp);
        comments.add(comment);
    }

        Gson gson = new Gson();

        String json = "{";
        json += "\"numComments\": \"" + numComments.toString() + "\", ";
        json += "\"commentList\":" + gson.toJson(comments) + "}";

        response.setContentType("application/json;");
        response.getWriter().println(json);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String comment = request.getParameter("comment-text");
        String tempNum = request.getParameter("num-comments");
        long timestamp = System.currentTimeMillis();
            
        if (comment.length() > 0) {
            Entity newComment = new Entity("Comment");
            newComment.setProperty("text", comment);
            newComment.setProperty("time", timestamp);

            DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
            datastore.put(newComment);
        }

        if (tempNum.length() == 0 || Integer.parseInt(tempNum) > Comment.getNumComments()) {
            numComments = Comment.getNumComments();
        } else {
            numComments = Integer.parseInt(tempNum);
        }

        response.sendRedirect("/index.html");
    }
}
