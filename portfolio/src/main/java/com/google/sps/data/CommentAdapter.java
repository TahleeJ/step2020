package com.google.sps.data;

import com.google.gson.JsonParser;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.google.protobuf.util.JsonFormat;
import com.google.sps.data.Comments.CommentObject;
import java.io.IOException;

public final class CommentAdapter extends TypeAdapter<CommentObject> {
		/**
	 * Override the read method to return a {@CommentObject} object from it's json representation.
	 */
	@Override
	public CommentObject read(JsonReader jsonReader) throws IOException {
	    // Create a builder for the CommentObject message
		CommentObject.Builder commentBuilder = CommentObject.newBuilder();
		// Use the JsonFormat class to parse the json string into the builder object
		// The Json string will be parsed fromm the JsonReader object
		JsonParser jsonParser = new JsonParser();
		JsonFormat.parser().merge(jsonParser.parse(jsonReader).toString(), commentBuilder);
		// Return the built CommentObject message
		return commentBuilder.build();
	}

	@Override
	public void write(JsonWriter jsonWriter, CommentObject comment) throws IOException {
		// Call the printer of the JsonFormat class to convert the CommentObject proto message to Json
		jsonWriter.jsonValue(JsonFormat.printer().print(comment));
	}
}