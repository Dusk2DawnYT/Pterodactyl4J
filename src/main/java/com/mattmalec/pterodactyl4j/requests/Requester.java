package com.mattmalec.pterodactyl4j.requests;

import com.mattmalec.pterodactyl4j.PteroAPI;
import com.mattmalec.pterodactyl4j.exceptions.*;
import okhttp3.Request;
import okhttp3.*;
import org.json.JSONObject;

import java.io.IOException;

public class Requester {

	private PteroAPI api;
	private Response response;
	private OkHttpClient okHttpClient = new OkHttpClient();
	private static final String PTERODACTYL_API_PREFIX = "%s/api/";

	public Requester(PteroAPI api) {
		this.api = api;
	}

	public Requester request(Route.CompiledRoute compiledRoute) {
		String url = String.format(PTERODACTYL_API_PREFIX, api.getApplicationUrl()) + compiledRoute.getCompiledRoute();
		if(api.getApplicationUrl() == null || api.getApplicationUrl().isEmpty())
			throw new HttpException("No Pterodactyl URL was defined.");
		Request.Builder builder = new Request.Builder();
		Method method = compiledRoute.getMethod();
		switch(method) {
			case GET: builder.get();
			break;
			case POST: builder.post(RequestBody.create(MediaType.parse("application/json"), compiledRoute.getJSONData().toString().getBytes()));
			break;
			case PATCH: builder.patch(RequestBody.create(MediaType.parse("application/json"), compiledRoute.getJSONData().toString().getBytes()));
			break;
			case DELETE: builder.delete(RequestBody.create(MediaType.parse("application/json"), compiledRoute.getJSONData().toString().getBytes()));
			break;
		}
		builder.addHeader("Accept", "Application/vnd.pterodactyl.v1+json");
		if(api.getToken() == null || api.getToken().isEmpty())
			throw new LoginException("No authorization token was defined.");
		builder.header("Authorization", "Bearer " + api.getToken());
		builder.url(url);
		try {
			this.response = okHttpClient.newCall(builder.build()).execute();
		} catch (IOException ex) {
			throw new HttpException("Could not successfully execute a request.", ex.getCause());
		}
		if(this.response.isSuccessful()) {
			return this;
		} else {
			int responseCode = this.response.code();

			if(responseCode == 403) {
				throw new LoginException("The provided token is either incorrect or does not have access to process this request.");
			}
			if(responseCode == 404) {
				throw new NotFoundException("The requested entity was not found.");
			}
			if(responseCode == 429) {
				throw new RateLimitedException("The request was rate limited.");
			}
			if(responseCode == 500) {
				throw new ServerException("The server has encountered an Internal Server Error.");
			}
		}
		throw new HttpException("Pterodactyl4J has encountered an unknown error.");
	}
	public JSONObject toJSONObject() {
		try {
			return new JSONObject(this.response.body().string());
		} catch (IOException e) {
			throw new HttpException("No JSON body found.");
		}
	}
}
