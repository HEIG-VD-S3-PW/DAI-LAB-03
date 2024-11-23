package ch.heigvd.dai.process;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.PrintWriter;

public abstract class Process {

    protected final BufferedReader in;
    protected final BufferedWriter out;

    public Process(BufferedReader in, BufferedWriter out) {
        this.in = in;
        this.out = out;
    }

    abstract public void execute() throws Exception;
}

