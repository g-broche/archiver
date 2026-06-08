package com.gbroche.archiver.classes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Command {
    private final String executable;
    private final List<String> arguments;

    private Command(Builder builder) {
        this.executable = builder.executable;
        this.arguments = Collections.unmodifiableList(builder.arguments);
    }

    public List<String> toList() {
        List<String> command = new ArrayList<>();
        command.add(executable);
        command.addAll(arguments);
        return command;
    }

    public static class Builder {

        private final String executable;
        private final List<String> arguments = new ArrayList<>();

        public Builder(String executable) {
            this.executable = executable;
        }

        public Builder flag(String flag) {
            arguments.add(flag);
            return this;
        }

        public Builder flag(String flag, String value) {
            arguments.add(flag);
            arguments.add(value);
            return this;
        }

        // For flags that concatenate directly with their value like "-p<password>" or "-o<path>"
        public Builder flagConcat(String flag, String value) {
            arguments.add(flag + value);
            return this;
        }

        public Builder argument(String value) {
            arguments.add(value);
            return this;
        }

        public Command build() {
            return new Command(this);
        }
    }
}
