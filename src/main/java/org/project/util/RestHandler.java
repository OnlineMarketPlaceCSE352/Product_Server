package org.project.util;

import org.project.dto.Request;
import org.project.dto.Response;

import java.io.*;
import java.io.PrintWriter;
import java.util.Map;

public class RestHandler {
    public static void sendResponse(PrintWriter output, Response response) throws IOException {
        output.print(response.toString());
        output.flush();
    }


    public static Request parseRequest(BufferedReader reader) throws IOException {
        String requestLine = reader.readLine();
        if (requestLine == null || requestLine.isEmpty()) return null;

        String[] parts = requestLine.split(" ");
        if (parts.length < 2) return null;

        Method method = Method.valueOf(parts[0]);
        String path = parts[1];

        Request request = new Request(method, path);

        String headerLine;
        while ((headerLine = reader.readLine()) != null && !headerLine.isEmpty()) {
            int separator = headerLine.indexOf(":");
            if (separator != -1) {
                String key = headerLine.substring(0, separator).trim();
                String value = headerLine.substring(separator + 1).trim();
                request.addHeader(key, value);
            }
        }

        String contentLengthHeader = request.getHeader("Content-Length");
        if (contentLengthHeader != null) {
            int length = Integer.parseInt(contentLengthHeader);
            char[] bodyChars = new char[length];
            int read = reader.read(bodyChars, 0, length);
            request.setBody(new String(bodyChars, 0, read));
        }

        return request;
    }


}