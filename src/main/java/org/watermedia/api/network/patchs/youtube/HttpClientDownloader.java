package org.watermedia.api.network.patchs.youtube;

import org.schabi.newpipe.extractor.downloader.Downloader;
import org.schabi.newpipe.extractor.downloader.Request;
import org.schabi.newpipe.extractor.downloader.Response;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class HttpClientDownloader extends Downloader {
	private HttpClient client;

	@Override
	public Response execute(Request request) throws IOException {
		if (client == null) {
			client = HttpClient.newHttpClient();
		}

		var httpRequestBuilder = HttpRequest.newBuilder()
			.method(request.httpMethod(), request.dataToSend() == null ? HttpRequest.BodyPublishers.noBody() : HttpRequest.BodyPublishers.ofByteArray(request.dataToSend()))
			.uri(URI.create(request.url()));

		for (var header : request.headers().entrySet()) {
			httpRequestBuilder = httpRequestBuilder.header(header.getKey(), String.join("; ", header.getValue()));
		}

		var httpRequest = httpRequestBuilder.build();
		HttpResponse<String> httpResponse;
		try {
			httpResponse = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
		} catch (InterruptedException e) {
			throw new IOException(e);
		}
		return new Response(httpResponse.statusCode(), "", httpResponse.headers().map(), httpResponse.body(), request.url());
	}
}