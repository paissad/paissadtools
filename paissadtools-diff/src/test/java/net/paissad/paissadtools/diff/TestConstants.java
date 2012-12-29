package net.paissad.paissadtools.diff;

import java.io.File;

public interface TestConstants {

    File TEST_DIR                      = new File("src/test/");

    File TEST_RESOURCES_DIR            = new File(TEST_DIR, "resources");

    File LOREM_IPSUM_TEXT_1_FILE       = new File(TEST_RESOURCES_DIR, "lorem_ipsum_1.txt");
    File LOREM_IPSUM_TEXT_2_FILE       = new File(TEST_RESOURCES_DIR, "lorem_ipsum_2.txt");
    File LOREM_IPSUM_ZIP_FILE          = new File(TEST_RESOURCES_DIR, "lorem_ipsum.zip");

    File XML_WITHOUT_NAMESPACES_1_FILE = new File(TEST_RESOURCES_DIR, "xml_without_namespaces_1.xml");
    File XML_WITHOUT_NAMESPACES_2_FILE = new File(TEST_RESOURCES_DIR, "xml_without_namespaces_2.xml");
    File XML_WITHOUT_NAMESPACES_3_FILE = new File(TEST_RESOURCES_DIR, "xml_without_namespaces_3.xml");

    File PROPERTIES_1_FILE             = new File(TEST_RESOURCES_DIR, "props_1.properties");
    File PROPERTIES_2_FILE             = new File(TEST_RESOURCES_DIR, "props_2.properties");
    File PROPERTIES_3_FILE             = new File(TEST_RESOURCES_DIR, "props_3.properties");
    File PROPERTIES_4_FILE             = new File(TEST_RESOURCES_DIR, "props_4.properties");

}
