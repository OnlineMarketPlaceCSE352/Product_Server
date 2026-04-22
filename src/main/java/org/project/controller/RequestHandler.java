package org.project.controller;

import org.project.dto.Request;
import org.project.dto.Response;

@FunctionalInterface
public interface RequestHandler {
    Response handle(Request request) throws Exception ;
}
