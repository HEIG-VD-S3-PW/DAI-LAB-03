package ch.heigvd.dai.process;

import java.io.BufferedReader;
import java.io.PrintWriter;

public abstract class Process {

    protected final BufferedReader in;
    protected final PrintWriter out;

    public Process(BufferedReader in, PrintWriter out) {
        this.in = in;
        this.out = out;
    }

    abstract public void execute() throws Exception;
}

