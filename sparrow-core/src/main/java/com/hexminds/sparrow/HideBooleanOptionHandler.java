package com.hexminds.sparrow;

import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.OptionDef;
import org.kohsuke.args4j.spi.BooleanOptionHandler;
import org.kohsuke.args4j.spi.Setter;

public class HideBooleanOptionHandler extends BooleanOptionHandler {
    public HideBooleanOptionHandler(CmdLineParser parser, OptionDef option, Setter<? super Boolean> setter) {
        super(parser, option, setter);
    }

    public String printDefaultValue() {
        return null;
    }
}
