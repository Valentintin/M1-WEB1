package fr.univlyon1.m1if.m1if03.utils;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import java.io.PrintWriter;

/**
 * Classe qui "mocke" une réponse avec un <code>PrintWriter</code> minimal.
 * Elle sera passée aux éléments de la chaîne suivants par le filtre de négociation de contenus,
 * pour que la servlet en bout de chaîne puisse fermer son flux de sortie, mais pas celui de la "vraie" réponse.
 */
public class BufferlessHttpServletResponseWrapper extends HttpServletResponseWrapper {
    private final ServletOutputStream os;

    public BufferlessHttpServletResponseWrapper(HttpServletResponse response) {
        super(response);
        os = new ServletOutputStream() {
            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setWriteListener(WriteListener writeListener) {
            }

            @Override
            public void write(int b) {
            }
        };
    }

    @Override
    public void setCharacterEncoding(String charset) {
    }

    @Override
    public ServletOutputStream getOutputStream() {
        return os;
    }

    @Override
    public PrintWriter getWriter() {
        return new PrintWriter(os);
    }

    @Override
    public void setContentLength(int len) {
    }

    @Override
    public void setContentLengthLong(long len) {
    }

    @Override
    public void setContentType(String type) {
    }

    @Override
    public void setBufferSize(int size) {
    }

    @Override
    public void flushBuffer() {
    }

    @Override
    public void reset() {
    }

    @Override
    public void resetBuffer() {
    }
}
