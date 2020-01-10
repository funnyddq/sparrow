package com.hexminds;

import com.hexminds.sparrow.App;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * AppTest
 */
public class AppTest {
    /**
     * 测试
     */
    @Test
    public void run() {
        String[] args = new String[1];
        args[0] = "src/test/resources/case01.spa";
        App.main(args);
        return;
    }
}
