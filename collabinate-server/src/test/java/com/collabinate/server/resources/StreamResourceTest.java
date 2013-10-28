package com.collabinate.server.resources;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.Test;
import org.restlet.Response;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Status;

import com.collabinate.server.activitystreams.Activity;
import com.collabinate.server.activitystreams.Collection;
import com.google.gson.Gson;
import com.google.gson.JsonParser;

/**
 * Tests for the Stream Resource
 * 
 * @author mafuba
 * 
 */
public class StreamResourceTest extends GraphResourceTest
{
	@Test
	public void get_empty_stream_should_get_empty_atom_feed()
	{
		assertEquals(Status.SUCCESS_OK, get().getStatus());
	}
	
	@Test
	public void item_added_to_stream_should_return_201()
	{
		assertEquals(Status.SUCCESS_CREATED, post().getStatus());
	}
	
	@Test
	public void item_added_to_stream_should_create_and_return_child_location()
	{		
		assertEquals(
				getRequest(Method.POST, null).getResourceRef().getPath() + "/",
				post().getLocationRef().getParentRef().getPath());
	}
	
	@Test
	public void item_added_to_stream_should_have_entity_in_post_response_body()
	{
		String entityBody = "TEST";
		Response response = post(entityBody, MediaType.TEXT_PLAIN);
		
		assertEquals(entityBody, response.getEntityAsText());
	}
	
	@Test
	public void stream_should_be_json_object()
	{
		post("TEST", MediaType.TEXT_PLAIN);
		// parser will throw if result is not json
		new JsonParser().parse(get().getEntityAsText());
	}
	
	@Test
	public void item_added_to_stream_should_appear_in_stream()
	{
		String entityBody = "TEST";
		post(entityBody, MediaType.TEXT_PLAIN);
		
		assertThat(get().getEntityAsText(), containsString(entityBody));		
	}
	
	@Test
	public void activity_stream_date_in_post_should_be_used_in_stream()
	{
		DateTime dateTime = new DateTime(1977, 5, 13, 5, 13, DateTimeZone.UTC);
		String dateString = dateTime.toString(ISODateTimeFormat.dateTime());
		Activity activity = new Activity(null, dateString, null);
		String entityBody = new Gson().toJson(activity);
		
		post(entityBody, MediaType.APPLICATION_JSON);
		
		Collection stream = (new Gson())
				.fromJson(get().getEntityAsText(), Collection.class);
		
		assertEquals(dateTime, stream.getItems()[0].getPublished());
	}
	
	@Test
	public void stream_entries_should_appear_in_correct_date_order()
	{
		DateTime dateTime1 = new DateTime(1977, 5, 13, 5, 13, DateTimeZone.UTC);
		String dateString1 = dateTime1.toString(ISODateTimeFormat.dateTime());
		Activity activity1 = new Activity(null, dateString1, null);
		String entityBody1 = new Gson().toJson(activity1);

		DateTime dateTime2 = new DateTime(1973, 6, 28, 6, 28, DateTimeZone.UTC);
		String dateString2 = dateTime2.toString(ISODateTimeFormat.dateTime());
		Activity activity2 = new Activity(null, dateString2, null);
		String entityBody2 = new Gson().toJson(activity2);
		
		post(entityBody1, MediaType.APPLICATION_JSON);
		post(entityBody2, MediaType.APPLICATION_JSON);
		
		Collection stream = (new Gson())
				.fromJson(get().getEntityAsText(), Collection.class);
		
		assertEquals(dateTime1, stream.getItems()[0].getPublished());
}
	
	@Override
	protected String getResourcePath()
	{
		return "/1/tenant/entities/entity/stream";
	}
}
