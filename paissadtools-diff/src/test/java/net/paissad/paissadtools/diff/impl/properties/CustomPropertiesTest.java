package net.paissad.paissadtools.diff.impl.properties;

import java.io.FileInputStream;
import java.io.IOException;

import net.paissad.paissadtools.diff.TestConstants;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CustomPropertiesTest {

    private CustomProperties propsWithCompareValues;
    private CustomProperties propsWithoutCompareValues;

    @Before
    public final void setUp() throws IOException {
        this.propsWithCompareValues = new CustomProperties(true);
        this.propsWithCompareValues.load(new FileInputStream(TestConstants.PROPERTIES_1_FILE));

        this.propsWithoutCompareValues = new CustomProperties(false);
        this.propsWithoutCompareValues.load(new FileInputStream(TestConstants.PROPERTIES_2_FILE));
    }

    @Test
    public final void testHashCode() {
        Assert.assertTrue(this.propsWithCompareValues.hashCode() != this.propsWithoutCompareValues.hashCode());
    }

}
