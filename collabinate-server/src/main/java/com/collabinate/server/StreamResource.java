package com.collabinate.server;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

/**
 * Restful resource representing a series of stream entries for an entity.
 * 
 * @author mafuba
 *
 */
public class StreamResource extends ServerResource
{
	@Get
	public String Stream()
	{
		return "";
	}
}