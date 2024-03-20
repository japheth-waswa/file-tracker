package com.elijahwaswa.filetracker.util;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class CharResponseWrapper extends HttpServletResponseWrapper {
    private CharArrayWriter output;
    public CharResponseWrapper(HttpServletResponse response) {
        super(response);
        output  = new CharArrayWriter();
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        return new PrintWriter(output);
    }

    @Override
    public String toString() {
        return output.toString();
    }
}
